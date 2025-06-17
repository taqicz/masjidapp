/**
 * Kelas EventFragment digunakan untuk menampilkan daftar event dalam bentuk RecyclerView.
 *
 * Fitur yang didukung:
 * - Menampilkan semua event dari Firebase.
 * - Pencarian event berdasarkan judul (fitur search).
 * - Filter event berdasarkan jenis event menggunakan ChipGroup.
 * - Tombol untuk menambah event baru (membuka AddEventFragment).
 * - Fungsi update dan hapus event melalui adapter.
 */

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
    private ArrayList<EventModel> eventList = new ArrayList<>(); // Data asli dari Firebase
    private ArrayList<EventModel> filteredList = new ArrayList<>(); // Data setelah difilter
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

        // Inisialisasi view
        eventsRecyclerView = view.findViewById(R.id.eventsRecyclerView);
        etSearch = view.findViewById(R.id.et_search_event);
        chipGroupFilter = view.findViewById(R.id.chipGroupFilter);
        tvEventSummary = view.findViewById(R.id.tv_event_summary);

        // Setup tampilan RecyclerView
        setupEventsRecyclerView();

        // Referensi database
        eventRef = FirebaseDatabase.getInstance().getReference("events");
        loadEventsFromFirebase();

        // Tombol tambah event
        FloatingActionButton fabAddEvent = view.findViewById(R.id.fab_add_event);
        fabAddEvent.setOnClickListener(v -> openFragment(new AddEventFragment()));

        // Event saat mengetik di search
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterEvents(); // Filter saat keyword berubah
            }
        });

        // Event saat chip filter dipilih
        chipGroupFilter.setOnCheckedChangeListener((group, checkedId) -> filterEvents());
    }

    // Setup adapter dan listener untuk RecyclerView
    private void setupEventsRecyclerView() {
        eventsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        eventAdapter = new EventAdapter(getContext(), filteredList);

        // Listener untuk update event
        eventAdapter.setOnEventUpdateClickListener((event, position) -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("EVENT", event);
            UpdateEventFragment updateFragment = new UpdateEventFragment();
            updateFragment.setArguments(bundle);
            openFragment(updateFragment);
        });

        // Listener untuk hapus event
        eventAdapter.setOnEventDeleteClickListener(position -> {
            EventModel eventToDelete = filteredList.get(position);
            String eventId = eventToDelete.getId();
            if (eventId != null) {
                eventRef.child(eventId).removeValue();
            }
        });

        eventsRecyclerView.setAdapter(eventAdapter);
    }

    // Ambil data event dari Firebase dan tampilkan ke dalam list
    private void loadEventsFromFirebase() {
        eventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded()) return; // Mencegah crash jika fragment belum terpasang

                eventList.clear();
                ArrayList<String> uniqueTypes = new ArrayList<>(); // Untuk membuat chip filter jenis

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    EventModel event = dataSnapshot.getValue(EventModel.class);
                    if (event != null) {
                        event.setId(dataSnapshot.getKey());
                        eventList.add(event);

                        // Ambil jenis event untuk chip filter
                        String type = event.getType();
                        if (type != null && !uniqueTypes.contains(type)) {
                            uniqueTypes.add(type);
                        }
                    }
                }

                populateFilterChips(uniqueTypes);
                updateEventSummary();
                filterEvents(); // Terapkan filter terbaru setelah data dimuat
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error jika perlu
            }
        });
    }

    // Membuat chip filter berdasarkan jenis event
    private void populateFilterChips(ArrayList<String> types) {
        if (!isAdded()) return;

        chipGroupFilter.setOnCheckedChangeListener(null); // Hapus listener sementara
        chipGroupFilter.removeAllViews();

        // Chip default "Semua"
        Chip allChip = new Chip(requireContext());
        allChip.setText("Semua");
        allChip.setCheckable(true);
        allChip.setChecked(true);
        chipGroupFilter.addView(allChip);

        // Chip untuk tiap jenis event unik
        for (String type : types) {
            Chip chip = new Chip(requireContext());
            chip.setText(type);
            chip.setCheckable(true);
            chipGroupFilter.addView(chip);
        }

        chipGroupFilter.setOnCheckedChangeListener((group, checkedId) -> filterEvents());
    }

    // Filter event berdasarkan keyword pencarian dan jenis chip yang dipilih
    private void filterEvents() {
        String keyword = etSearch.getText() != null ? etSearch.getText().toString().toLowerCase() : "";
        String selectedType = "Semua";

        // Ambil chip yang dipilih
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

    // Menampilkan jumlah total event yang ditampilkan
    private void updateEventSummary() {
        if (tvEventSummary != null) {
            tvEventSummary.setText("Total Event: " + filteredList.size());
        }
    }

    // Fungsi untuk berpindah ke fragment lain (tambah atau edit event)
    private void openFragment(Fragment fragment) {
        if (getActivity() == null) return;
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
