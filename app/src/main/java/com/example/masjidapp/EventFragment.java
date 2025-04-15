package com.example.masjidapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class EventFragment extends Fragment {

    private RecyclerView eventsRecyclerView;

    public EventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventsRecyclerView = view.findViewById(R.id.eventsRecyclerView);
        setupEventsRecyclerView();
    }

    private void setupEventsRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        eventsRecyclerView.setLayoutManager(gridLayoutManager);

        List<EventModel> eventList = new ArrayList<>();
        eventList.add(new EventModel("Kajian Tafsir Al-Quran", "Masjid Al-Hikmah",
                "Sabtu, 15 Juni 2023", "19:30 - 21:00 WIB"));
        eventList.add(new EventModel("Pengajian Akbar", "Masjid Jami",
                "Minggu, 16 Juni 2023", "09:00 - 11:30 WIB"));
        eventList.add(new EventModel("Buka Puasa Bersama", "Masjid An-Nur",
                "Senin, 17 Juni 2023", "17:30 - 19:00 WIB"));
        eventList.add(new EventModel("Santunan Anak Yatim", "Masjid Al-Falah",
                "Selasa, 18 Juni 2023", "16:00 - 18:00 WIB"));

        EventAdapter adapter = new EventAdapter(getContext(), eventList);
        eventsRecyclerView.setAdapter(adapter);
    }
}
