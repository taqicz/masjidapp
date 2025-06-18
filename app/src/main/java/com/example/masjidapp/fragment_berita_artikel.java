package com.example.masjidapp;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

import java.util.Map;

public class fragment_berita_artikel extends Fragment {

    public interface OnArtikelSubmitListener {
        void onArtikelSubmit(String judul, String isi, String kategori, String imageUrl, boolean isUpdate, int updatePosition);
    }

    private OnArtikelSubmitListener listener;

    private EditText edtJudul, edtIsi, edtKategori;
    private Button btnKirim, btnPilihGambar;
    private ImageView imagePreview;
    private ProgressBar progressBar;

    private Uri selectedImageUri;
    private String existingImageUrl;
    private boolean isUpdate = false;
    private int updatePosition = -1;

    private final ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    if (getContext() != null) {
                        Glide.with(this).load(selectedImageUri).into(imagePreview);
                        imagePreview.setVisibility(View.VISIBLE);
                    }
                }
            }
    );

    public fragment_berita_artikel() {}

    public static fragment_berita_artikel newInstanceForEdit(BeritaArtikelModel artikel, int position) {
        fragment_berita_artikel fragment = new fragment_berita_artikel();
        Bundle args = new Bundle();
        args.putBoolean("isUpdate", true);
        args.putInt("updatePosition", position);
        args.putString("judul", artikel.getTitle());
        args.putString("isi", artikel.getContent());
        args.putString("kategori", artikel.getKategori());
        args.putString("imageUrl", artikel.getImageUrl());
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnArtikelSubmitListener(OnArtikelSubmitListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isUpdate = getArguments().getBoolean("isUpdate", false);
            updatePosition = getArguments().getInt("updatePosition", -1);
            existingImageUrl = getArguments().getString("imageUrl", "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_berita_artikel, container, false);

        edtJudul = view.findViewById(R.id.isiJudul);
        edtIsi = view.findViewById(R.id.isiArtikel);
        edtKategori = view.findViewById(R.id.isiKategori);
        btnKirim = view.findViewById(R.id.btnKirim);
        btnPilihGambar = view.findViewById(R.id.btnPilihGambar);
        imagePreview = view.findViewById(R.id.imagePreview);

        if (isUpdate && getArguments() != null) {
            edtJudul.setText(getArguments().getString("judul", ""));
            edtIsi.setText(getArguments().getString("isi", ""));
            edtKategori.setText(getArguments().getString("kategori", ""));
            if (existingImageUrl != null && !existingImageUrl.isEmpty()) {
                imagePreview.setVisibility(View.VISIBLE);
                Glide.with(this).load(existingImageUrl).into(imagePreview);
            }
        }

        btnPilihGambar.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        btnKirim.setOnClickListener(v -> handleKirim());

        return view;
    }

    private void handleKirim() {
        String judul = edtJudul.getText().toString().trim();
        String isi = edtIsi.getText().toString().trim();
        String kategori = edtKategori.getText().toString().trim();

        if (judul.isEmpty() || isi.isEmpty() || kategori.isEmpty()) {
            Toast.makeText(getContext(), "Semua field teks harus diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImageUri == null && !isUpdate) {
            Toast.makeText(getContext(), "Silakan pilih gambar untuk artikel baru", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        if (selectedImageUri != null) {
            uploadImageToCloudinary();
        } else {
            submitDataKeListener(existingImageUrl);
        }
    }

    private void uploadImageToCloudinary() {
        String judul = edtJudul.getText().toString().trim();
        String fileName = judul.replaceAll("\\s+", "_") + "_" + System.currentTimeMillis();

        MediaManager.get().upload(selectedImageUri)
                .option("public_id", "masjidapp/artikel/" + fileName)
                .callback(new UploadCallback() {
                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        if (getContext() == null) return;
                        String imageUrl = (String) resultData.get("secure_url");
                        submitDataKeListener(imageUrl);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Upload gambar gagal: " + error.getDescription(), Toast.LENGTH_LONG).show();
                            setLoading(false);
                        }
                    }

                    @Override public void onStart(String requestId) {}
                    @Override public void onProgress(String requestId, long bytes, long totalBytes) {}
                    @Override public void onReschedule(String requestId, ErrorInfo error) {}
                })
                .dispatch();
    }

    private void submitDataKeListener(String finalImageUrl) {
        String judul = edtJudul.getText().toString().trim();
        String isi = edtIsi.getText().toString().trim();
        String kategori = edtKategori.getText().toString().trim();

        if (listener != null) {
            listener.onArtikelSubmit(judul, isi, kategori, finalImageUrl, isUpdate, updatePosition);
        }

        if (getParentFragmentManager() != null) {
            getParentFragmentManager().popBackStack();
        }
    }

    private void setLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
        if (btnKirim != null) {
            btnKirim.setEnabled(!isLoading);
        }
        if (btnPilihGambar != null) {
            btnPilihGambar.setEnabled(!isLoading);
        }
    }
}