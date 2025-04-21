package com.example.masjidapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class EventFragment extends Fragment {

    private RecyclerView eventsRecyclerView;
    private ArrayList<EventModel> eventList;
    private EventAdapter eventAdapter;

    private static final int REQUEST_ADD_EVENT = 1;  // Request code untuk menambah event
    private static final int REQUEST_UPDATE_EVENT = 2;  // Request code untuk update event

    public EventFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup RecyclerView
        eventsRecyclerView = view.findViewById(R.id.eventsRecyclerView);
        setupEventsRecyclerView();

        // Setup FAB untuk menambah acara baru
        FloatingActionButton fabAddEvent = view.findViewById(R.id.fab_add_event);
        fabAddEvent.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddEventActivity.class);
            startActivityForResult(intent, REQUEST_ADD_EVENT);  // Start AddEventActivity dengan request code
        });
    }

    private void setupEventsRecyclerView() {
        eventsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));

        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(getContext(), eventList);

        // Set listener untuk tombol update
        eventAdapter.setOnEventUpdateClickListener((event, position) -> {
            Intent intent = new Intent(getContext(), UpdateEventActivity.class);
            intent.putExtra("EVENT", event);
            intent.putExtra("EVENT_POSITION", position);
            startActivityForResult(intent, REQUEST_UPDATE_EVENT);  // Menggunakan request code untuk update
        });

        // Set listener untuk tombol delete
        eventAdapter.setOnEventDeleteClickListener(position -> {
            // Hapus event dari eventList
            eventList.remove(position);
            eventAdapter.notifyItemRemoved(position);  // Memberitahu adapter bahwa item telah dihapus
            eventAdapter.notifyItemRangeChanged(position, eventList.size());  // Perbarui item setelah penghapusan
        });

        eventsRecyclerView.setAdapter(eventAdapter);

        // Dummy data langsung ditambahkan
        eventList.add(new EventModel(
                "Pengajian Akbar",
                "Masjid Jami",
                "Minggu, 16 Juni 2023",
                "09:00",
                "11:30",
                "Pengajian",
                "https://example.com/image2.jpg",
                "Acara pengajian akbar yang akan dihadiri oleh para ustadz terkemuka."
        ));

        eventList.add(new EventModel(
                "Buka Puasa Bersama",
                "Masjid Al-Hikmah",
                "Senin, 18 Juni 2023",
                "17:30",
                "19:00",
                "Buka Puasa",
                "https://example.com/image3.jpg",
                "Buka puasa bersama masyarakat sekitar masjid untuk meningkatkan silaturahmi."
        ));

        eventList.add(new EventModel(
                "Shalat Taraweh",
                "Masjid Baitul Rahman",
                "Selasa, 19 Juni 2023",
                "20:00",
                "21:30",
                "Taraweh",
                "https://example.com/image4.jpg",
                "Shalat taraweh berjamaah setiap malam selama bulan Ramadhan."
        ));

        eventAdapter.setEvents(eventList);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_EVENT && resultCode == getActivity().RESULT_OK && data != null) {
            EventModel newEvent = (EventModel) data.getSerializableExtra("newEvent");
            if (newEvent != null) {
                eventList.add(newEvent);  // Tambahkan event baru ke eventList
                eventAdapter.notifyItemInserted(eventList.size() - 1);  // Notifikasi bahwa data baru ditambahkan
            }
        }
        if (requestCode == REQUEST_UPDATE_EVENT && resultCode == getActivity().RESULT_OK && data != null) {
            EventModel updatedEvent = (EventModel) data.getSerializableExtra("UPDATED_EVENT");
            int position = data.getIntExtra("EVENT_POSITION", -1);
            if (updatedEvent != null && position >= 0) {
                eventList.set(position, updatedEvent);  // Update event yang ada pada posisi yang sesuai
                eventAdapter.notifyItemChanged(position);  // Notifikasi bahwa item diubah
            }
        }
    }
}
