package com.example.masjidapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EventFragment extends Fragment {

    private RecyclerView eventsRecyclerView;
    private ArrayList<EventModel> eventList = new ArrayList<>();
    private ArrayList<EventModel> filteredList = new ArrayList<>();
    private EventAdapter eventAdapter;
    private DatabaseReference eventRef;
    private TextInputEditText etSearch;
    private ChipGroup chipGroupFilter;
    private TextView tvEventSummary;

    public EventFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventsRecyclerView = view.findViewById(R.id.eventsRecyclerView);
        etSearch = view.findViewById(R.id.et_search_event);
        chipGroupFilter = view.findViewById(R.id.chipGroupFilter);
        tvEventSummary = view.findViewById(R.id.tv_event_summary);

        setupEventsRecyclerView();

        eventRef = FirebaseDatabase.getInstance().getReference("events");
        loadEventsFromFirebase();

        FloatingActionButton fabAddEvent = view.findViewById(R.id.fab_add_event);
        fabAddEvent.setOnClickListener(v -> openFragment(new AddEventFragment()));

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterEvents();
            }
        });

        chipGroupFilter.setOnCheckedChangeListener((group, checkedId) -> filterEvents());
    }

    private void setupEventsRecyclerView() {
        eventsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        eventAdapter = new EventAdapter(getContext(), filteredList);

        eventAdapter.setOnEventUpdateClickListener((event, position) -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("EVENT", event);
            UpdateEventFragment updateFragment = new UpdateEventFragment();
            updateFragment.setArguments(bundle);
            openFragment(updateFragment);
        });

        eventAdapter.setOnEventDeleteClickListener(position -> {
            EventModel eventToDelete = filteredList.get(position);
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
                ArrayList<String> uniqueTypes = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    EventModel event = dataSnapshot.getValue(EventModel.class);
                    if (event != null) {
                        event.setId(dataSnapshot.getKey());
                        eventList.add(event);

                        String type = event.getType();
                        if (type != null && !uniqueTypes.contains(type)) {
                            uniqueTypes.add(type);
                        }
                    }
                }

                populateFilterChips(uniqueTypes); // <- CHIP DINAMIS
                updateEventSummary();
                filterEvents();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // error handling
            }
        });
    }

    private void populateFilterChips(ArrayList<String> types) {
        chipGroupFilter.setOnCheckedChangeListener(null); // sementara disable listener
        chipGroupFilter.removeAllViews();

        // Chip "Semua"
        Chip allChip = new Chip(requireContext());
        allChip.setText("Semua");
        allChip.setCheckable(true);
        allChip.setChecked(true); // default
        chipGroupFilter.addView(allChip);

        // Chip dari type unik
        for (String type : types) {
            Chip chip = new Chip(requireContext());
            chip.setText(type);
            chip.setCheckable(true);
            chipGroupFilter.addView(chip);
        }

        chipGroupFilter.setOnCheckedChangeListener((group, checkedId) -> filterEvents());
    }

    private void filterEvents() {
        String keyword = etSearch.getText() != null ? etSearch.getText().toString().toLowerCase() : "";
        String selectedType = "Semua";

        Chip checkedChip = chipGroupFilter.findViewById(chipGroupFilter.getCheckedChipId());
        if (checkedChip != null) {
            selectedType = checkedChip.getText().toString();
        }

        filteredList.clear();
        for (EventModel event : eventList) {
            boolean matchesKeyword = event.getTitle().toLowerCase().contains(keyword);
            boolean matchesType = selectedType.equals("Semua") || event.getType().equalsIgnoreCase(selectedType);

            if (matchesKeyword && matchesType) {
                filteredList.add(event);
            }
        }

        updateEventSummary();
        eventAdapter.setEvents(filteredList);
        eventAdapter.notifyDataSetChanged();
    }

    private void updateEventSummary() {
        tvEventSummary.setText("Total Event: " + filteredList.size());
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
