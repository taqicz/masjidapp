package com.example.masjidapp;

import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.List;

public class EventFragment extends Fragment {

    private RecyclerView eventsRecyclerView;
    private List<EventModel> eventList;
    private EventAdapter eventAdapter;

    public EventFragment() {}

    private final ActivityResultLauncher<Intent> updateEventLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    EventModel updatedEvent = (EventModel) result.getData().getSerializableExtra("UPDATED_EVENT");
                    int position = result.getData().getIntExtra("EVENT_POSITION", -1);

                    if (updatedEvent != null && position != -1 && position < eventList.size()) {
                        eventList.set(position, updatedEvent);
                        eventAdapter.notifyItemChanged(position);
                        saveEventsToPreferences();
                    }
                }
            });

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

        FloatingActionButton fabAddEvent = view.findViewById(R.id.fab_add_event);
        fabAddEvent.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddEventActivity.class);
            startActivity(intent);
        });
    }

    private void setupEventsRecyclerView() {
        eventsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));

        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(getContext(), eventList);

        // ✅ Tambahkan listener setelah adapter dibuat
        eventAdapter.setOnEventUpdateClickListener((event, position) -> {
            Intent intent = new Intent(getContext(), UpdateEventActivity.class);
            intent.putExtra("EVENT", event);
            intent.putExtra("EVENT_POSITION", position);
            updateEventLauncher.launch(intent);
        });

        eventsRecyclerView.setAdapter(eventAdapter);
        loadEventsFromPreferences();
    }

    private void loadEventsFromPreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Events", getContext().MODE_PRIVATE);
        eventList.clear();

        int eventCount = sharedPreferences.getInt("eventCount", 0);

        for (int i = 0; i < eventCount; i++) {
            String title = sharedPreferences.getString("eventTitle_" + i, "");
            String location = sharedPreferences.getString("eventLocation_" + i, "");
            String date = sharedPreferences.getString("eventDate_" + i, "");
            String startTime = sharedPreferences.getString("eventStartTime_" + i, "");
            String endTime = sharedPreferences.getString("eventEndTime_" + i, "");
            String type = sharedPreferences.getString("eventType_" + i, "");
            String imageUri = sharedPreferences.getString("eventImage_" + i, null);
            String description = sharedPreferences.getString("eventDescription_" + i, "");

            eventList.add(new EventModel(title, location, date, startTime, endTime, type, imageUri, description));
        }

        addDummyEventsToList(); // Optional untuk testing
        eventAdapter.setEvents(eventList);
    }

    private void saveEventsToPreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Events", getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();

        editor.putInt("eventCount", eventList.size());
        for (int i = 0; i < eventList.size(); i++) {
            EventModel event = eventList.get(i);
            editor.putString("eventTitle_" + i, event.getTitle());
            editor.putString("eventLocation_" + i, event.getLocation());
            editor.putString("eventDate_" + i, event.getDate());
            editor.putString("eventStartTime_" + i, event.getStartTime());
            editor.putString("eventEndTime_" + i, event.getEndTime());
            editor.putString("eventType_" + i, event.getType());
            editor.putString("eventImage_" + i, event.getImageUri());
            editor.putString("eventDescription_" + i, event.getDescription());
        }

        editor.apply();
    }

    private void addDummyEventsToList() {
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
    }

    @Override
    public void onResume() {
        super.onResume();
        loadEventsFromPreferences();
    }
}
