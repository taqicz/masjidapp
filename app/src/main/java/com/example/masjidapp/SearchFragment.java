// SearchFragment.java
package com.example.masjidapp;

import android.app.AlertDialog;
import android.content.Intent; // Tambahkan import ini
import android.os.Bundle;
import android.text.InputType;
import android.util.Log; // Tambahkan untuk debugging jika perlu
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

    // Definisikan konstanta untuk kunci Intent agar konsisten
    public static final String EXTRA_MOSQUE_NAME = "MOSQUE_NAME";
    public static final String EXTRA_MOSQUE_ADDRESS = "MOSQUE_ADDRESS";
    public static final String EXTRA_MOSQUE_RATING = "MOSQUE_RATING";
    public static final String EXTRA_MOSQUE_DISTANCE = "MOSQUE_DISTANCE"; // Anda mungkin ingin menampilkan ini juga
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
        setupRecyclerView(); // Perubahan utama ada di sini
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
        // Modifikasi lambda di sini untuk menangani klik item
        adapter = new MosqueAdapter(requireContext(), mosqueList, mosque -> {
            // Aksi ketika item diklik
            Intent intent = new Intent(getActivity(), profilMasjid.class);

            // Masukkan data masjid ke Intent
            // Gunakan konstanta yang sudah didefinisikan
            intent.putExtra(EXTRA_MOSQUE_NAME, mosque.getName());
            intent.putExtra(EXTRA_MOSQUE_ADDRESS, mosque.getAddress());
            intent.putExtra(EXTRA_MOSQUE_RATING, mosque.getRating());
            intent.putExtra(EXTRA_MOSQUE_DISTANCE, mosque.getDistance()); // Jika ingin dikirim
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
                        mosqueList.add(mosque);
                    }
                }
                if (adapter != null) { // Pastikan adapter tidak null
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
        layout.setPadding(50, 40, 50, 40); // Sedikit penyesuaian padding

        EditText nameInput = createEditText("Nama Masjid");
        EditText addressInput = createEditText("Alamat");
        EditText ratingInput = createEditText("Rating (0.0 - 5.0)"); // Hint lebih jelas
        ratingInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        EditText distanceInput = createEditText("Jarak (misal: 1.5 km)"); // Hint lebih jelas
        EditText imageUrlInput = createEditText("URL Gambar (opsional)");
        EditText descriptionInput = createEditText("Deskripsi (opsional)");
        EditText dateInput = createEditText("Tanggal Berdiri (YYYY-MM-DD)");
        EditText chairmanInput = createEditText("Nama Ketua Takmir");

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
            String name = nameInput.getText().toString().trim();
            String address = addressInput.getText().toString().trim();
            String ratingStr = ratingInput.getText().toString().trim();
            String distance = distanceInput.getText().toString().trim();
            String imageUrl = imageUrlInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();
            String date = dateInput.getText().toString().trim();
            String chairman = chairmanInput.getText().toString().trim();

            // Validasi dasar
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


            MosqueModel newMosque = new MosqueModel(name, address, rating, distance, imageUrl, description, date, chairman);
            // Menggunakan nama masjid sebagai ID unik. Pertimbangkan menggunakan push().getKey() untuk ID unik jika nama bisa sama atau mengandung karakter tidak valid.
            if (editMosque != null || (newMosque.getName() != null && !newMosque.getName().isEmpty())) {
                String key = (editMosque != null) ? editMosque.getName() : newMosque.getName(); // Gunakan nama lama jika edit, atau nama baru.
                // TODO: Pertimbangkan untuk tidak membiarkan nama (yang jadi key) diubah saat edit, atau handle perubahan key.
                // Atau lebih baik, setiap masjid punya ID unik yang tidak berubah.
                databaseRef.child(key).setValue(newMosque)
                        .addOnSuccessListener(unused -> {
                            if(getContext() != null) Toast.makeText(getContext(), "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            if(getContext() != null) Toast.makeText(getContext(), "Gagal menyimpan data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("FirebaseSave", "Gagal menyimpan data", e);
                        });
            } else {
                if(getContext() != null) Toast.makeText(getContext(), "Nama masjid tidak boleh kosong untuk dijadikan ID", Toast.LENGTH_LONG).show();
            }
        });

        builder.setNegativeButton("Batal", null);
        builder.show();
    }

    private EditText createEditText(String hint) {
        EditText et = new EditText(requireContext());
        et.setHint(hint);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0,0,0,16); // Tambahkan margin bawah
        et.setLayoutParams(params);
        return et;
    }
}