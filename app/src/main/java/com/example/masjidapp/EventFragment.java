package com.example.masjidapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EventFragment extends Fragment {

    private RecyclerView eventsRecyclerView;
    private ArrayList<EventModel> eventList;
    private EventAdapter eventAdapter;
    private DatabaseReference eventRef;

    public EventFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventsRecyclerView = view.findViewById(R.id.eventsRecyclerView);
        setupEventsRecyclerView();

        eventRef = FirebaseDatabase.getInstance().getReference("events");
        loadEventsFromFirebase();

        FloatingActionButton fabAddEvent = view.findViewById(R.id.fab_add_event);
        fabAddEvent.setOnClickListener(v -> {
            openFragment(new AddEventFragment());
        });
    }

    private void setupEventsRecyclerView() {
        eventsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(getContext(), eventList);

        // Tombol update
        eventAdapter.setOnEventUpdateClickListener((event, position) -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("EVENT", event);
            UpdateEventFragment updateFragment = new UpdateEventFragment();
            updateFragment.setArguments(bundle);
            openFragment(updateFragment);
        });

        // Tombol delete
        eventAdapter.setOnEventDeleteClickListener(position -> {
            EventModel eventToDelete = eventList.get(position);
            String eventId = eventToDelete.getId();
            if (eventId != null) {
                eventRef.child(eventId).removeValue();
            }
        });

        eventsRecyclerView.setAdapter(eventAdapter);
    }

    private void loadEventsFromFirebase() {
        eventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    EventModel event = dataSnapshot.getValue(EventModel.class);
                    if (event != null) {
                        eventList.add(event);
                    }
                }
                eventAdapter.setEvents(eventList);
                eventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Error handling bisa ditambahkan di sini
            }
        });
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment); // Pastikan container ID ini sesuai di layout Activity
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
