package com.example.masjidapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FragmentHome extends Fragment {

    private RecyclerView eventsRecyclerView;
    private RecyclerView mosquesRecyclerView;
    private RecyclerView recyclerBuku;
    private FloatingActionButton notificationFab;
    private TextView btnLihatLainnya;

    public FragmentHome() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Inisialisasi view
        eventsRecyclerView = view.findViewById(R.id.eventsRecyclerView);
        mosquesRecyclerView = view.findViewById(R.id.mosquesRecyclerView);
        recyclerBuku = view.findViewById(R.id.recyclerBuku);
        notificationFab = view.findViewById(R.id.notificationFab);
        btnLihatLainnya = view.findViewById(R.id.btnLihatLainnya);

        CardView prayerTimeCard = view.findViewById(R.id.prayerTimeCard);
        CardView btnLihatMasjidLainnya = view.findViewById(R.id.btnLihatMasjidLainnya);

        // Klik "Lihat lainnya" untuk buku
        btnLihatLainnya.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ListBukuActivity.class);
            startActivity(intent);
        });

        // Klik "Lihat masjid lainnya"
        btnLihatMasjidLainnya.setOnClickListener(v -> {
            Fragment searchFragment = new SearchFragment();
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, searchFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Klik ke detail jadwal sholat
        prayerTimeCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PrayerTimeDetailActivity.class);
            startActivity(intent);
        });

        // FAB Notifikasi
        notificationFab.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Notifikasi", Toast.LENGTH_SHORT).show();
        });

        // Setup RecyclerView
        setupEventsRecyclerView();
        setupMosquesRecyclerView();
        setupBukuRecyclerView();

        return view;
    }

    private void setupEventsRecyclerView() {
        eventsRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        ArrayList<EventModel> eventList = new ArrayList<>();
        EventAdapter adapter = new EventAdapter(getContext(), eventList);
        eventsRecyclerView.setAdapter(adapter);

        DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference("events");

        eventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventList.clear();
                for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                    EventModel event = eventSnapshot.getValue(EventModel.class);
                    if (event != null) {
                        eventList.add(event);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Gagal memuat data event", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupMosquesRecyclerView() {
        mosquesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<MosqueModel> mosqueList = new ArrayList<>();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("masjid");

        MosqueAdapter adapter = new MosqueAdapter(getContext(), mosqueList, mosque -> {
            Intent intent = new Intent(getActivity(), profilMasjid.class);
            intent.putExtra("MOSQUE_ID", mosque.getId());
            intent.putExtra("MOSQUE_NAME", mosque.getName());
            intent.putExtra("MOSQUE_ADDRESS", mosque.getAddress());
            intent.putExtra("MOSQUE_RATING", mosque.getRating());
            intent.putExtra("MOSQUE_DISTANCE", mosque.getDistance());
            intent.putExtra("MOSQUE_IMAGE_URL", mosque.getImageUrl());
            intent.putExtra("MOSQUE_DESCRIPTION", mosque.getDescription());
            intent.putExtra("MOSQUE_ESTABLISHED_DATE", mosque.getEstablishedDate());
            intent.putExtra("MOSQUE_CHAIRMAN", mosque.getChairman());
            startActivity(intent);
        });

        mosquesRecyclerView.setAdapter(adapter);

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mosqueList.clear();
                int count = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (count >= 3) break;
                    MosqueModel mosque = ds.getValue(MosqueModel.class);
                    if (mosque != null) {
                        mosque.setId(ds.getKey());
                        mosqueList.add(mosque);
                        count++;
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Gagal mengambil data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void setupBukuRecyclerView() {
        recyclerBuku.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        ArrayList<BukuModel> listBuku = new ArrayList<>();
        listBuku.add(new BukuModel(1, R.drawable.buku1, "Tafsir Al-Misbah oleh Quraish Shihab", "2005"));
        listBuku.add(new BukuModel(2, R.drawable.buku2, "Fiqh Wanita - Syaikh Shalih Al-Fauzan", "2010"));
        listBuku.add(new BukuModel(3, R.drawable.buku3, "Sirah Nabawiyah - Ibnu Hisyam", "1998"));

        BukuAdapter bukuAdapter = new BukuAdapter(getContext(), listBuku);
        recyclerBuku.setAdapter(bukuAdapter);
    }
}