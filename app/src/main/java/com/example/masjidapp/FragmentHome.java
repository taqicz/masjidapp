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
import androidx.fragment.app.FragmentTransaction;
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

    private DatabaseReference eventsRef;

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

        btnLihatMasjidLainnya.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ListMasjidActivity.class);
            startActivity(intent);
        });

        prayerTimeCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PrayerTimeDetailActivity.class);
            startActivity(intent);
        });

        notificationFab.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Notifikasi", Toast.LENGTH_SHORT).show();
        });

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

        eventsRef = FirebaseDatabase.getInstance().getReference("events");

        // Listener Update
        adapter.setOnEventUpdateClickListener((event, position) -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("EVENT", event);
            UpdateEventFragment updateFragment = new UpdateEventFragment();
            updateFragment.setArguments(bundle);
            openFragment(updateFragment);
        });

        // Listener Delete
        adapter.setOnEventDeleteClickListener(position -> {
            EventModel eventToDelete = eventList.get(position);
            String eventId = eventToDelete.getId();
            if (eventId != null) {
                eventsRef.child(eventId).removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Event berhasil dihapus", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Gagal menghapus event", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

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

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment); // Pastikan ID ini sesuai
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void setupMosquesRecyclerView() {
        mosquesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<MosqueModel> mosqueList = new ArrayList<>();
        mosqueList.add(new MosqueModel("Masjid Al-Hikmah", "Jl. Masjid No. 123, Jakarta", 4.5f, "https://example.com/image1.jpg", "Masjid Al-Hikmah adalah pusat kajian Islam.", "1995", "Ust. Ahmad"));
        mosqueList.add(new MosqueModel("Masjid Jami", "Jl. Raya No. 45, Jakarta", 4.2f, "https://example.com/image2.jpg", "Masjid Jami terkenal dengan khutbah Jumatnya.", "1980", "Ust. Budi"));
        mosqueList.add(new MosqueModel("Masjid An-Nur", "Jl. Utama No. 67, Jakarta", 4.7f, "https://example.com/image3.jpg", "Masjid An-Nur menyediakan program buka puasa.", "2005", "Ust. Zaki"));

        MosqueAdapter adapter = new MosqueAdapter(getContext(), mosqueList, mosque -> {
            Intent intent = new Intent(getActivity(), profilMasjid.class);
            intent.putExtra("masjidName", mosque.getName());
            intent.putExtra("masjidAddress", mosque.getAddress());
            intent.putExtra("imageUrl", mosque.getImageUrl());
            intent.putExtra("masjidRating", mosque.getRating());
            intent.putExtra("descriptionMasjid", mosque.getDescription());
            intent.putExtra("establishedDate", mosque.getEstablishedDate());
            intent.putExtra("masjidChairman", mosque.getChairman());
            startActivity(intent);
        });

        mosquesRecyclerView.setAdapter(adapter);
    }

    private void setupBukuRecyclerView() {
        recyclerBuku.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        ArrayList<BukuModel> listBuku = new ArrayList<>();
        listBuku.add(new BukuModel(1, R.drawable.buku1, "Tafsir Al-Misbah oleh Quraish Shihab", "2005"));
        listBuku.add(new BukuModel(2, R.drawable.buku2, "Fiqh Wanita - Syaikh Shalih Al-Fauzan", "2010"));
        listBuku.add(new BukuModel(3, R.drawable.buku3, "Sirah Nabawiyah - Ibnu Hisyam", "1998"));

        BukuAdapter bukuAdapter = new BukuAdapter(getContext(), listBuku);
        recyclerBuku.setAdapter(bukuAdapter);

        btnLihatLainnya.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ListBukuActivity.class);
            startActivity(intent);
        });
    }
}
