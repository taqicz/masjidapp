package com.example.masjidapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class AddEditMosqueFragment extends Fragment {
    private EditText nameInput, addressInput, ratingInput, descriptionInput, dateInput, chairmanInput;
    private Button btnSelectImage, btnSave;
    private ImageView imagePreview;
    private Uri selectedImageUri;
    private DatabaseReference databaseRef;
    private MosqueModel editMosque;

    public static final String ARG_EDIT_MOSQUE = "edit_mosque";

    public AddEditMosqueFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_edit_mosque, container, false);

        databaseRef = FirebaseDatabase.getInstance().getReference("masjid");

        nameInput = view.findViewById(R.id.input_name);
        addressInput = view.findViewById(R.id.input_address);
        ratingInput = view.findViewById(R.id.input_rating);
        descriptionInput = view.findViewById(R.id.input_description);
        dateInput = view.findViewById(R.id.input_date);
        chairmanInput = view.findViewById(R.id.input_chairman);
        imagePreview = view.findViewById(R.id.image_preview);
        btnSelectImage = view.findViewById(R.id.btn_select_image);
        btnSave = view.findViewById(R.id.btn_save);

        btnSelectImage.setOnClickListener(v -> openImageChooser());
        btnSave.setOnClickListener(v -> saveMosque());

        if (getArguments() != null && getArguments().containsKey(ARG_EDIT_MOSQUE)) {
            editMosque = (MosqueModel) getArguments().getSerializable(ARG_EDIT_MOSQUE);
            fillFormWithMosque(editMosque);
        }

        return view;
    }

    private void fillFormWithMosque(MosqueModel mosque) {
        nameInput.setText(mosque.getName());
        addressInput.setText(mosque.getAddress());
        ratingInput.setText(String.valueOf(mosque.getRating()));
        descriptionInput.setText(mosque.getDescription());
        dateInput.setText(mosque.getEstablishedDate());
        chairmanInput.setText(mosque.getChairman());

        if (mosque.getImageUrl() != null && !mosque.getImageUrl().isEmpty()) {
            imagePreview.setVisibility(View.VISIBLE);
            Glide.with(this).load(mosque.getImageUrl()).into(imagePreview);
        }
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(intent, 1001);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imagePreview.setVisibility(View.VISIBLE);
            Glide.with(this).load(selectedImageUri).into(imagePreview);
        }
    }

    private void saveMosque() {
        String name = nameInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();
        String ratingStr = ratingInput.getText().toString().trim();
        String desc = descriptionInput.getText().toString().trim();
        String date = dateInput.getText().toString().trim();
        String chairman = chairmanInput.getText().toString().trim();

        float rating = 0;
        try {
            rating = Float.parseFloat(ratingStr);
        } catch (Exception ignored) {}

        String key = editMosque != null ? editMosque.getId() : databaseRef.push().getKey();
        if (key == null) {
            Toast.makeText(getContext(), "Gagal membuat ID masjid", Toast.LENGTH_SHORT).show();
            return;
        }

        MosqueModel mosque = new MosqueModel(name, address, rating, "", desc, date, chairman);
        mosque.setId(key);

        if (editMosque != null && editMosque.getImageUrl() != null && selectedImageUri == null) {
            mosque.setImageUrl(editMosque.getImageUrl());
        }

        if (selectedImageUri != null) {
            MediaManager.get().upload(selectedImageUri)
                    .unsigned("masjidapp") // Sesuaikan dengan preset di Cloudinary
                    .callback(new UploadCallback() {
                        @Override public void onStart(String requestId) {}
                        @Override public void onProgress(String requestId, long bytes, long totalBytes) {}
                        @Override public void onSuccess(String requestId, Map resultData) {
                            String url = (String) resultData.get("secure_url");
                            mosque.setImageUrl(url);
                            saveToFirebase(key, mosque);
                        }
                        @Override public void onError(String requestId, ErrorInfo error) {
                            Toast.makeText(getContext(), "Gagal upload gambar", Toast.LENGTH_SHORT).show();
                        }
                        @Override public void onReschedule(String requestId, ErrorInfo error) {}
                    }).dispatch();
        } else {
            saveToFirebase(key, mosque);
        }
    }

    private void saveToFirebase(String key, MosqueModel mosque) {
        databaseRef.child(key).setValue(mosque).addOnSuccessListener(unused -> {
            Toast.makeText(getContext(), "Masjid disimpan", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Gagal menyimpan data", Toast.LENGTH_SHORT).show();
        });
    }
}
