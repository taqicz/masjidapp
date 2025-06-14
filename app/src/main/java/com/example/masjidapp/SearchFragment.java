package com.example.masjidapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // <-- IMPORT GLIDE
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private MosqueAdapter adapter;
    private List<MosqueModel> mosqueList;
    private DatabaseReference databaseRef;
    private Uri selectedImageUri;

    // Deklarasikan ImageView di sini agar bisa diakses dari onActivityResult
    private ImageView imagePreview;

    private MaterialToolbar toolbar;
    private Button btnBack, btnAdd;

    public static final String EXTRA_MOSQUE_NAME = "MOSQUE_NAME";
    public static final String EXTRA_MOSQUE_ADDRESS = "MOSQUE_ADDRESS";
    public static final String EXTRA_MOSQUE_RATING = "MOSQUE_RATING";
    public static final String EXTRA_MOSQUE_DISTANCE = "MOSQUE_DISTANCE";
    public static final String EXTRA_MOSQUE_IMAGE_URL = "MOSQUE_IMAGE_URL";
    public static final String EXTRA_MOSQUE_DESCRIPTION = "MOSQUE_DESCRIPTION";
    public static final String EXTRA_MOSQUE_ESTABLISHED_DATE = "MOSQUE_ESTABLISHED_DATE";
    public static final String EXTRA_MOSQUE_CHAIRMAN = "MOSQUE_CHAIRMAN";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

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
            Intent intent = new Intent(getActivity(), profilMasjid.class);
            intent.putExtra(EXTRA_MOSQUE_NAME, mosque.getName());
            intent.putExtra(EXTRA_MOSQUE_ADDRESS, mosque.getAddress());
            intent.putExtra(EXTRA_MOSQUE_RATING, mosque.getRating());
            intent.putExtra(EXTRA_MOSQUE_DISTANCE, mosque.getDistance());
            intent.putExtra(EXTRA_MOSQUE_IMAGE_URL, mosque.getImageUrl());
            intent.putExtra(EXTRA_MOSQUE_DESCRIPTION, mosque.getDescription());
            intent.putExtra(EXTRA_MOSQUE_ESTABLISHED_DATE, mosque.getEstablishedDate());
            intent.putExtra(EXTRA_MOSQUE_CHAIRMAN, mosque.getChairman());
            startActivity(intent);
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
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mosqueList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    MosqueModel mosque = ds.getValue(MosqueModel.class);
                    if (mosque != null) {
                        mosque.setId(ds.getKey());
                        mosqueList.add(mosque);
                    }
                }
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Gagal mengambil data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
                Log.e("FirebaseFetch", "Gagal mengambil data", error.toException());
            }
        });
    }

    private void showMosqueDialog(@Nullable MosqueModel editMosque) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(editMosque == null ? "Tambah Masjid" : "Edit Masjid");

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 40);

        EditText nameInput = createEditText("Nama Masjid");
        EditText addressInput = createEditText("Alamat");
        EditText ratingInput = createEditText("Rating (0.0 - 5.0)");
        ratingInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        EditText distanceInput = createEditText("Jarak (misal: 1.5 km)");
        EditText imageUrlInput = createEditText("URL Gambar (otomatis diisi)");
        imageUrlInput.setEnabled(false);
        Button uploadButton = new Button(requireContext());
        uploadButton.setText("Pilih Gambar");
        uploadButton.setOnClickListener(v -> openImageChooser());

        // --- PERUBAHAN 1: Buat ImageView untuk preview ---
        imagePreview = new ImageView(requireContext());
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                300 // Tinggi preview 300px
        );
        imageParams.setMargins(0, 20, 0, 20);
        imagePreview.setLayoutParams(imageParams);
        imagePreview.setVisibility(View.GONE); // Sembunyikan di awal
        imagePreview.setAdjustViewBounds(true);
        // ---------------------------------------------

        EditText descriptionInput = createEditText("Deskripsi");
        EditText dateInput = createEditText("Tanggal Berdiri (YYYY-MM-DD)");
        EditText chairmanInput = createEditText("Nama Ketua Takmir");

        layout.addView(nameInput);
        layout.addView(addressInput);
        layout.addView(ratingInput);
        layout.addView(distanceInput);
        layout.addView(imageUrlInput);
        layout.addView(uploadButton);
        layout.addView(imagePreview); // <-- Tambahkan ImageView ke layout
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

            // Tampilkan gambar yang sudah ada saat mode edit
            if (editMosque.getImageUrl() != null && !editMosque.getImageUrl().isEmpty()) {
                imagePreview.setVisibility(View.VISIBLE);
                Glide.with(this).load(editMosque.getImageUrl()).into(imagePreview);
            }
        }

        builder.setView(layout);

        builder.setPositiveButton(editMosque == null ? "Tambah" : "Update", (dialog, which) -> {
            String name = nameInput.getText().toString().trim();
            String address = addressInput.getText().toString().trim();
            String ratingStr = ratingInput.getText().toString().trim();
            String distance = distanceInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();
            String date = dateInput.getText().toString().trim();
            String chairman = chairmanInput.getText().toString().trim();

            if (name.isEmpty() || address.isEmpty() || date.isEmpty() || chairman.isEmpty()) {
                Toast.makeText(getContext(), "Nama, Alamat, Tanggal Berdiri, dan Ketua Takmir tidak boleh kosong!", Toast.LENGTH_LONG).show();
                return;
            }

            float rating = 0.0f;
            if (!ratingStr.isEmpty()) {
                try {
                    rating = Float.parseFloat(ratingStr);
                    if (rating < 0.0f || rating > 5.0f) {
                        Toast.makeText(getContext(), "Rating harus antara 0.0 dan 5.0", Toast.LENGTH_LONG).show();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Format rating tidak valid!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            MosqueModel newMosque = new MosqueModel(name, address, rating, distance, "", description, date, chairman);

            final String key;
            if (editMosque != null) {
                key = editMosque.getId();
                newMosque.setImageUrl(editMosque.getImageUrl());
            } else {
                key = databaseRef.push().getKey();
            }

            if (key == null) {
                Toast.makeText(getContext(), "Gagal membuat ID unik untuk data.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedImageUri != null) {
                Toast.makeText(getContext(), "Mengunggah gambar...", Toast.LENGTH_SHORT).show();
                String uploadPreset = "masjidapp";

                MediaManager.get().upload(selectedImageUri)
                        .unsigned(uploadPreset)
                        .callback(new UploadCallback() {
                            @Override
                            public void onStart(String requestId) {}

                            @Override
                            public void onProgress(String requestId, long bytes, long totalBytes) {}

                            @Override
                            public void onSuccess(String requestId, Map resultData) {
                                if (getContext() == null) return;
                                String imageUrl = (String) resultData.get("secure_url");
                                newMosque.setImageUrl(imageUrl);
                                saveMosqueToFirebase(key, newMosque);
                            }

                            @Override
                            public void onError(String requestId, ErrorInfo error) {
                                if (getContext() != null) {
                                    Toast.makeText(getContext(), "Upload gambar gagal: " + error.getDescription(), Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onReschedule(String requestId, ErrorInfo error) {}
                        }).dispatch();
            } else {
                saveMosqueToFirebase(key, newMosque);
            }
        });

        builder.setNegativeButton("Batal", (dialog, which) -> {
            selectedImageUri = null;
        });
        builder.show();
    }

    private EditText createEditText(String hint) {
        EditText et = new EditText(requireContext());
        et.setHint(hint);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 16);
        et.setLayoutParams(params);
        return et;
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");

        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivityForResult(intent, 1001);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            if (getContext() != null && selectedImageUri != null) {
                try {
                    final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                    getContext().getContentResolver().takePersistableUriPermission(selectedImageUri, takeFlags);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }

                // --- PERUBAHAN 2: Tampilkan preview gambar yang dipilih ---
                if (imagePreview != null) {
                    imagePreview.setVisibility(View.VISIBLE);
                    Glide.with(this) // Memakai konteks dari Fragment
                            .load(selectedImageUri)
                            .into(imagePreview);
                }
                // ----------------------------------------------------
            }
        }
    }

    private void saveMosqueToFirebase(String key, MosqueModel mosque) {
        databaseRef.child(key).setValue(mosque)
                .addOnSuccessListener(unused -> {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
                    }
                    selectedImageUri = null;
                })
                .addOnFailureListener(e -> {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Gagal menyimpan data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    Log.e("FirebaseSave", "Gagal menyimpan data", e);
                });
    }
}