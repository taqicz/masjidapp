package com.example.masjidapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeritaFragment extends Fragment {
    private RecyclerView recyclerViewTrending;
    private RecyclerView recyclerViewArtikel;
    private BeritaTrendingAdapter trendingAdapter;
    private BeritaArtikelAdapter artikelAdapter;
    private List<BeritaTrendingModel> trendingList;
    private List<BeritaArtikelModel> artikelList;

    private DatabaseReference databaseRefArtikel;

    public BeritaFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        trendingList = new ArrayList<>();
        artikelList = new ArrayList<>();
        databaseRefArtikel = FirebaseDatabase.getInstance().getReference("artikel");

        trendingList.add(new BeritaTrendingModel("Politik", "Kebijakan Baru Pemerintah"));
        trendingList.add(new BeritaTrendingModel("Ekonomi", "Inflasi Menurun di Kuartal Pertama"));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_berita, container, false);

        recyclerViewTrending = view.findViewById(R.id.recyclerViewTrending);
        recyclerViewArtikel = view.findViewById(R.id.recyclerViewArtikel);

        trendingAdapter = new BeritaTrendingAdapter(trendingList);
        artikelAdapter = new BeritaArtikelAdapter(getContext(), artikelList);

        recyclerViewTrending.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewTrending.setAdapter(trendingAdapter);
        recyclerViewArtikel.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewArtikel.setAdapter(artikelAdapter);

        Button buttonAdd = view.findViewById(R.id.berita_add);
        buttonAdd.setOnClickListener(v -> bukaFormArtikel(null, -1));

        setupArtikelListeners();
        fetchArtikelFromFirebase();

        return view;
    }

    private void fetchArtikelFromFirebase() {
        databaseRefArtikel.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                artikelList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    BeritaArtikelModel artikel = ds.getValue(BeritaArtikelModel.class);
                    if (artikel != null) {
                        artikel.setId(ds.getKey());
                        artikelList.add(artikel);
                    }
                }
                if (artikelAdapter != null) {
                    artikelAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Gagal mengambil data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("FirebaseFetch", "Error: ", error.toException());
                }
            }
        });
    }

    private void setupArtikelListeners() {
        artikelAdapter.setOnItemClickListener(artikel -> {
            BeritaDetailFragment detailFragment = new BeritaDetailFragment();
            Bundle args = new Bundle();
            args.putString("judul_artikel", artikel.getTitle());
            args.putString("isi_artikel", artikel.getContent());
            args.putString("kategori_artikel", artikel.getKategori());
            args.putString("gambar_artikel_url", artikel.getImageUrl());
            detailFragment.setArguments(args);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });

        artikelAdapter.setOnDeleteClickListener(position -> {
            if (position >= 0 && position < artikelList.size()) {
                String artikelId = artikelList.get(position).getId();
                if (artikelId != null && !artikelId.isEmpty()) {
                    databaseRefArtikel.child(artikelId).removeValue();
                }
            }
        });

        artikelAdapter.setOnItemLongClickListener(position -> {
            if (position >= 0 && position < artikelList.size()) {
                BeritaArtikelModel artikelToEdit = artikelList.get(position);
                bukaFormArtikel(artikelToEdit, position);
            }
        });
    }

    private void bukaFormArtikel(@Nullable BeritaArtikelModel artikel, int position) {
        fragment_berita_artikel formFragment;

        if (artikel != null && position != -1) {
            formFragment = fragment_berita_artikel.newInstanceForEdit(artikel, position);
        } else {
            formFragment = new fragment_berita_artikel();
        }

        formFragment.setOnArtikelSubmitListener((judul, isi, kategori, imageUrl, isUpdate, updatePosition) -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) {
                Toast.makeText(getContext(), "Silakan login untuk membuat atau mengubah artikel", Toast.LENGTH_SHORT).show();
                return;
            }

            String authorUid = currentUser.getUid();
            String authorName = currentUser.getDisplayName();

            if (authorName == null || authorName.isEmpty()) {
                authorName = "Anggota";
            }

            Map<String, Object> artikelValues = new HashMap<>();
            artikelValues.put("title", judul);
            artikelValues.put("content", isi);
            artikelValues.put("kategori", kategori);
            artikelValues.put("imageUrl", imageUrl);
            artikelValues.put("authorUid", authorUid);
            artikelValues.put("authorName", authorName);

            if (!isUpdate) {
                artikelValues.put("timestamp", ServerValue.TIMESTAMP);
            }

            if (isUpdate && updatePosition >= 0 && updatePosition < artikelList.size()) {
                String artikelIdToUpdate = artikelList.get(updatePosition).getId();
                if (artikelIdToUpdate != null) {
                    databaseRefArtikel.child(artikelIdToUpdate).updateChildren(artikelValues);
                    Toast.makeText(getContext(), "Artikel berhasil diperbarui!", Toast.LENGTH_SHORT).show();
                }
            } else {
                databaseRefArtikel.push().setValue(artikelValues);
                Toast.makeText(getContext(), "Artikel berhasil disimpan!", Toast.LENGTH_SHORT).show();
            }
        });

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, formFragment)
                .addToBackStack(null)
                .commit();
    }
}