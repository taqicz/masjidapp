package com.example.masjidapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class FragmentHome extends Fragment {

    private RecyclerView eventsRecyclerView;
    private RecyclerView mosquesRecyclerView;
    private FloatingActionButton notificationFab;

    public FragmentHome() {
        // Required empty public constructor
    }

    public static FragmentHome newInstance(String param1, String param2) {
        FragmentHome fragment = new FragmentHome();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize RecyclerView and other views
        eventsRecyclerView = view.findViewById(R.id.eventsRecyclerView);
        mosquesRecyclerView = view.findViewById(R.id.mosquesRecyclerView);
        notificationFab = view.findViewById(R.id.notificationFab);
        CardView prayerTimeCard = view.findViewById(R.id.prayerTimeCard);
        CardView btnLihatMasjidLainnya = view.findViewById(R.id.btnLihatMasjidLainnya);

        // Event for button "Lihat Masjid Lainnya"
        btnLihatMasjidLainnya.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ListMasjidActivity.class);
            startActivity(intent);
        });

        // Event for prayer time card
        prayerTimeCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PrayerTimeDetailActivity.class);
            startActivity(intent);
        });

        // Event for notification FAB
        notificationFab.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Notifikasi", Toast.LENGTH_SHORT).show();
        });

        // Setup RecyclerViews
        setupEventsRecyclerView();
        setupMosquesRecyclerView();

        return view;
    }

    private void setupEventsRecyclerView() {
        eventsRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        List<EventModel> eventList = loadEventsFromPreferences();

        EventAdapter adapter = new EventAdapter(getContext(), eventList);

        // Setup onClick listener for updating events
        adapter.setOnEventUpdateClickListener((event, position) -> {
            Intent intent = new Intent(getActivity(), UpdateEventActivity.class);
            intent.putExtra("EVENT", event);  // Passing event data to the update activity
            intent.putExtra("EVENT_POSITION", position);
            startActivityForResult(intent, 100);  // Use startActivityForResult for returning updated data
        });

        eventsRecyclerView.setAdapter(adapter);
    }

    // Handle the result from UpdateEventActivity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == getActivity().RESULT_OK && data != null) {
            // Get the updated event and position
            EventModel updatedEvent = (EventModel) data.getSerializableExtra("UPDATED_EVENT");
            int position = data.getIntExtra("EVENT_POSITION", -1);

            if (updatedEvent != null && position != -1) {
                // Update the event in the list
                List<EventModel> eventList = loadEventsFromPreferences();
                eventList.set(position, updatedEvent); // Update the item at the position

                // Refresh RecyclerView
                EventAdapter adapter = (EventAdapter) eventsRecyclerView.getAdapter();
                if (adapter != null) {
                    adapter.notifyDataSetChanged(); // Notify adapter to refresh all items
                }

                // Save updated events to SharedPreferences
                saveEventsToPreferences(eventList);
            }
        }
    }

    private List<EventModel> loadEventsFromPreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Events", getContext().MODE_PRIVATE);
        List<EventModel> eventList = new ArrayList<>();

        int eventCount = sharedPreferences.getInt("eventCount", 0);
        Log.d("FragmentHome", "eventCount from SharedPreferences: " + eventCount);

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

        addDummyEventsToList(eventList);  // Optionally, add dummy events to the list

        return eventList;
    }

    private void saveEventsToPreferences(List<EventModel> eventList) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Events", getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();  // Clear old events

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

        editor.apply();  // Apply changes to SharedPreferences
        Log.d("FragmentHome", "Updated events saved to SharedPreferences");
    }

    private void addDummyEventsToList(List<EventModel> eventList) {
        // Adding dummy events with both start and end times, and description
        eventList.add(new EventModel(
                "Pengajian Akbar",
                "Masjid Jami",
                "Minggu, 16 Juni 2023",
                "09:00",
                "11:30",
                "Pengajian",
                "https://example.com/image2.jpg",
                "Acara pengajian akbar yang akan dihadiri oleh para ustadz terkemuka."));

        eventList.add(new EventModel(
                "Buka Puasa Bersama",
                "Masjid Al-Hikmah",
                "Senin, 18 Juni 2023",
                "17:30",
                "19:00",
                "Buka Puasa",
                "https://example.com/image3.jpg",
                "Buka puasa bersama masyarakat sekitar masjid untuk meningkatkan silaturahmi."));

        eventList.add(new EventModel(
                "Shalat Taraweh",
                "Masjid Baitul Rahman",
                "Selasa, 19 Juni 2023",
                "20:00",
                "21:30",
                "Taraweh",
                "https://example.com/image4.jpg",
                "Shalat taraweh berjamaah setiap malam selama bulan Ramadhan."));
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
}
