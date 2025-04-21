package com.example.masjidapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

        // Set click listener untuk tombol
        btnLihatMasjidLainnya.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ListMasjidActivity.class);
            startActivity(intent);
        });

        // Event klik ke detail jadwal sholat
        prayerTimeCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PrayerTimeDetailActivity.class);
            startActivity(intent);
        });

        // FAB Notifikasi
        notificationFab.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Notifikasi", Toast.LENGTH_SHORT).show();
        });

        // Setup RecyclerView untuk event, masjid, dan buku
        setupEventsRecyclerView();
        setupMosquesRecyclerView();
        setupBukuRecyclerView();

        return view;
    }

    private void setupEventsRecyclerView() {
        eventsRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        List<EventModel> eventList = new ArrayList<>();
        eventList.add(new EventModel("Kajian Tafsir Al-Quran", "Masjid Al-Hikmah",
                "Sabtu, 15 Juni 2023", "19:30 - 21:00 WIB"));
        eventList.add(new EventModel("Pengajian Akbar", "Masjid Jami",
                "Minggu, 16 Juni 2023", "09:00 - 11:30 WIB"));
        eventList.add(new EventModel("Buka Puasa Bersama", "Masjid An-Nur",
                "Senin, 17 Juni 2023", "17:30 - 19:00 WIB"));

        EventAdapter adapter = new EventAdapter(getContext(), eventList);
        eventsRecyclerView.setAdapter(adapter);
    }

    private void setupMosquesRecyclerView() {
        mosquesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<MosqueModel> mosqueList = new ArrayList<>();
        mosqueList.add(new MosqueModel("Masjid Al-Hikmah", "Jl. Masjid No. 123, Jakarta", 4.5f, "1.2 km", "https://example.com/image1.jpg", "Masjid Al-Hikmah adalah pusat kajian Islam.", "1995", "Ust. Ahmad"));
        mosqueList.add(new MosqueModel("Masjid Jami", "Jl. Raya No. 45, Jakarta", 4.2f, "2.5 km", "https://example.com/image2.jpg", "Masjid Jami terkenal dengan khutbah Jumatnya.", "1980", "Ust. Budi"));
        mosqueList.add(new MosqueModel("Masjid An-Nur", "Jl. Utama No. 67, Jakarta", 4.7f, "3.1 km", "https://example.com/image3.jpg", "Masjid An-Nur menyediakan program buka puasa.", "2005", "Ust. Zaki"));

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

        // Lihat lainnya
        btnLihatLainnya.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ListBukuActivity.class);
            startActivity(intent);
        });
    }}

