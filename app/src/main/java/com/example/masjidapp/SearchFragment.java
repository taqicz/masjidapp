package com.example.masjidapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private MosqueAdapter adapter;
    private List<MosqueModel> mosqueList;
    private DatabaseReference databaseRef;

    private MaterialToolbar toolbar;
    private Button btnBack, btnAdd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Init view
        toolbar = view.findViewById(R.id.toolbar);
        recyclerView = view.findViewById(R.id.recyclerView);
        btnBack = view.findViewById(R.id.btn_back);
        btnAdd = view.findViewById(R.id.btn_add);

        databaseRef = FirebaseDatabase.getInstance().getReference("masjid");

        setupToolbar();
        setupRecyclerView();
        setupAddButton();
        setupBackButton();
        fetchDataFromFirebase();

        return view;
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> {
            if (getActivity() != null) getActivity().onBackPressed();
        });
    }

    private void setupRecyclerView() {
        mosqueList = new ArrayList<>();
        adapter = new MosqueAdapter(requireContext(), mosqueList, mosque -> {
            Toast.makeText(requireContext(), "Klik: " + mosque.getName(), Toast.LENGTH_SHORT).show();
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) getActivity().onBackPressed();
        });
    }

    private void setupAddButton() {
        btnAdd.setOnClickListener(v -> showMosqueDialog(null));
    }

    private void fetchDataFromFirebase() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                mosqueList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    MosqueModel mosque = ds.getValue(MosqueModel.class);
                    if (mosque != null) mosqueList.add(mosque);
                }
                adapter.notifyDataSetChanged();
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Gagal mengambil data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showMosqueDialog(@Nullable MosqueModel editMosque) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(editMosque == null ? "Tambah Masjid" : "Edit Masjid");

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(30, 30, 30, 30);

        EditText nameInput = createEditText("Nama Masjid");
        EditText addressInput = createEditText("Alamat");
        EditText ratingInput = createEditText("Rating");
        ratingInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        EditText distanceInput = createEditText("Jarak (km)");
        EditText imageUrlInput = createEditText("URL Gambar");
        EditText descriptionInput = createEditText("Deskripsi");
        EditText dateInput = createEditText("Tanggal Berdiri (YYYY-MM-DD)");
        EditText chairmanInput = createEditText("Nama Ketua");

        layout.addView(nameInput);
        layout.addView(addressInput);
        layout.addView(ratingInput);
        layout.addView(distanceInput);
        layout.addView(imageUrlInput);
        layout.addView(descriptionInput);
        layout.addView(dateInput);
        layout.addView(chairmanInput);

        if (editMosque != null) {
            nameInput.setText(editMosque.getName());
            addressInput.setText(editMosque.getAddress());
            ratingInput.setText(String.valueOf(editMosque.getRating()));
            distanceInput.setText(editMosque.getDistance());
            imageUrlInput.setText(editMosque.getImageUrl());
            descriptionInput.setText(editMosque.getDescription());
            dateInput.setText(editMosque.getEstablishedDate());
            chairmanInput.setText(editMosque.getChairman());
        }

        builder.setView(layout);

        builder.setPositiveButton(editMosque == null ? "Tambah" : "Update", (dialog, which) -> {
            String name = nameInput.getText().toString();
            String address = addressInput.getText().toString();
            float rating = Float.parseFloat(ratingInput.getText().toString());
            String distance = distanceInput.getText().toString();
            String imageUrl = imageUrlInput.getText().toString();
            String description = descriptionInput.getText().toString();
            String date = dateInput.getText().toString();
            String chairman = chairmanInput.getText().toString();

            MosqueModel newMosque = new MosqueModel(name, address, rating, distance, imageUrl, description, date, chairman);
            databaseRef.child(name).setValue(newMosque)
                    .addOnSuccessListener(unused -> Toast.makeText(getContext(), "Data berhasil disimpan", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Gagal menyimpan data", Toast.LENGTH_SHORT).show());
        });

        builder.setNegativeButton("Batal", null);
        builder.show();
    }

    private EditText createEditText(String hint) {
        EditText et = new EditText(requireContext());
        et.setHint(hint);
        return et;
    }
}
