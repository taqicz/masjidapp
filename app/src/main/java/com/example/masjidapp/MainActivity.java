package com.example.masjidapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

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

        // Setup RecyclerViews
        setupEventsRecyclerView();
        setupMosquesRecyclerView();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // Already on home
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


        // Setup notification FAB
        notificationFab.setOnClickListener(view -> {
            Toast.makeText(this, "Notifikasi", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupEventsRecyclerView() {
        // Horizontal layout for events
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false);
        eventsRecyclerView.setLayoutManager(layoutManager);

        // Sample data for events
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
        // Vertical layout for mosques
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mosquesRecyclerView.setLayoutManager(layoutManager);

        // Sample data for mosques
        List<MosqueModel> mosqueList = new ArrayList<>();
        mosqueList.add(new MosqueModel("Masjid Al-Hikmah", "Jl. Masjid No. 123, Jakarta", 4.5f, "1.2 km"));
        mosqueList.add(new MosqueModel("Masjid Jami", "Jl. Raya No. 45, Jakarta", 4.2f, "2.5 km"));
        mosqueList.add(new MosqueModel("Masjid An-Nur", "Jl. Utama No. 67, Jakarta", 4.7f, "3.1 km"));

        // Set adapter
        MosqueAdapter mosqueAdapter = new MosqueAdapter(this, mosqueList);
        mosquesRecyclerView.setAdapter(mosqueAdapter);
    }
}

