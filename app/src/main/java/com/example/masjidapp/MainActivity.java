package com.example.masjidapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView eventsRecyclerView;
    private RecyclerView mosquesRecyclerView;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton notificationFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);
        mosquesRecyclerView = findViewById(R.id.mosquesRecyclerView);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        notificationFab = findViewById(R.id.notificationFab);
        CardView btnLihatMasjidLainnya = findViewById(R.id.btnLihatMasjidLainnya);

        // Perbaikan setOnClickListener untuk tombol "Lihat Masjid Lainnya"
        btnLihatMasjidLainnya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListMasjidActivity.class);
                startActivity(intent);
            }
        });

        // Setup RecyclerViews
        setupEventsRecyclerView();
        setupMosquesRecyclerView();

        // Setup Bottom Navigation
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // Sudah di halaman home
                return true;
            } else if (itemId == R.id.nav_search) {
                Toast.makeText(MainActivity.this, "Pencarian", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_events) {
                Toast.makeText(MainActivity.this, "Event", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_profile) {
                Toast.makeText(MainActivity.this, "Profil", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

        // Inisialisasi view untuk jadwal sholat
        CardView prayerTimeCard = findViewById(R.id.prayerTimeCard);

        // Event klik untuk membuka PrayerTimeDetailActivity
        prayerTimeCard.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, PrayerTimeDetailActivity.class);
            startActivity(intent);
        });

        // Setup Floating Action Button untuk notifikasi
        notificationFab.setOnClickListener(view -> {
            Toast.makeText(this, "Notifikasi", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupEventsRecyclerView() {
        // Layout manager horizontal untuk event
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false);
        eventsRecyclerView.setLayoutManager(layoutManager);

        // Contoh data event
        List<EventModel> eventList = new ArrayList<>();
        eventList.add(new EventModel("Kajian Tafsir Al-Quran", "Masjid Al-Hikmah",
                "Sabtu, 15 Juni 2023", "19:30 - 21:00 WIB"));
        eventList.add(new EventModel("Pengajian Akbar", "Masjid Jami",
                "Minggu, 16 Juni 2023", "09:00 - 11:30 WIB"));
        eventList.add(new EventModel("Buka Puasa Bersama", "Masjid An-Nur",
                "Senin, 17 Juni 2023", "17:30 - 19:00 WIB"));

        // Set adapter
        EventAdapter eventAdapter = new EventAdapter(this, eventList);
        eventsRecyclerView.setAdapter(eventAdapter);
    }

    private void setupMosquesRecyclerView() {
        // Layout manager vertikal untuk daftar masjid
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mosquesRecyclerView.setLayoutManager(layoutManager);

        // Contoh data masjid
        List<MosqueModel> mosqueList = new ArrayList<>();
        mosqueList.add(new MosqueModel("Masjid Al-Hikmah", "Jl. Masjid No. 123, Jakarta", 4.5f, "1.2 km", "https://example.com/image1.jpg"));
        mosqueList.add(new MosqueModel("Masjid Jami", "Jl. Raya No. 45, Jakarta", 4.2f, "2.5 km", "https://example.com/image2.jpg"));
        mosqueList.add(new MosqueModel("Masjid An-Nur", "Jl. Utama No. 67, Jakarta", 4.7f, "3.1 km", "https://example.com/image3.jpg"));

        // Set adapter
        MosqueAdapter mosqueAdapter = new MosqueAdapter(this, mosqueList, mosque -> {
            // Aksi saat masjid diklik: buka profil masjid
            Intent intent = new Intent(this, profilMasjid.class);
            intent.putExtra("masjidName", mosque.getName());
            intent.putExtra("masjidAddress", mosque.getAddress());
            intent.putExtra("descriptionMasjid", mosque.getDescription());
            intent.putExtra("establishedDate", mosque.getEstablishedDate());
            intent.putExtra("masjidChairman", mosque.getChairman());
            intent.putExtra("imageUrl", ""); // Kosong atau URL jika ada


            startActivity(intent);
        });
        mosquesRecyclerView.setAdapter(mosqueAdapter);
    }
}
