package com.example.masjidapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

import java.util.Map;

public class fragment_berita_artikel extends Fragment {

    // Interface untuk komunikasi antar fragment
    public interface OnArtikelSubmitListener {
        // Tambahkan parameter String imageUrl
        void onArtikelSubmitted(String judul, String isi, String kategori, String imageUrl, boolean isUpdate, int updatePosition);
    }

    private OnArtikelSubmitListener listener;
    private EditText edtJudul, edtIsi, edtKategori;
    private Button btnKirim, btnPilihGambar;
    private ImageView imagePreview;

    private Uri selectedImageUri;
    private String existingImageUrl = ""; // Untuk menyimpan URL gambar saat mode edit
    private boolean isUpdate = false;
    private int updatePosition = -1;

    public fragment_berita_artikel() {}

    public void setOnArtikelSubmitListener(OnArtikelSubmitListener listener) {
        this.listener = listener;
    }

    public void setArtikelToEdit(BeritaArtikelModel artikel, int position) {
        isUpdate = true;
        updatePosition = position;
        existingImageUrl = artikel.getImageUrl(); // Simpan URL gambar yang sudah ada

        Bundle args = new Bundle();
        args.putString("judul", artikel.getTitle());
        args.putString("isi", artikel.getContent());
        args.putString("kategori", artikel.getKategori());
        args.putString("imageUrl", artikel.getImageUrl());
        this.setArguments(args);
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

        if (getArguments() != null) {
            edtJudul.setText(getArguments().getString("judul", ""));
            edtIsi.setText(getArguments().getString("isi", ""));
            edtKategori.setText(getArguments().getString("kategori", ""));
            String imageUrl = getArguments().getString("imageUrl", "");
            if (!imageUrl.isEmpty()) {
                imagePreview.setVisibility(View.VISIBLE);
                Glide.with(this).load(imageUrl).into(imagePreview);
            }
        }

        btnPilihGambar.setOnClickListener(v -> openImageChooser());

        btnKirim.setOnClickListener(v -> handleKirim());

        return view;
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(intent, 1001);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            if (getContext() != null) {
                try {
                    getContext().getContentResolver().takePersistableUriPermission(selectedImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
                imagePreview.setVisibility(View.VISIBLE);
                Glide.with(this).load(selectedImageUri).into(imagePreview);
            }
        }
    }

    private void handleKirim() {
        String judul = edtJudul.getText().toString().trim();
        String isi = edtIsi.getText().toString().trim();
        String kategori = edtKategori.getText().toString().trim();

        if (judul.isEmpty() || isi.isEmpty() || kategori.isEmpty()) {
            Toast.makeText(getContext(), "Semua field teks harus diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cek jika ada gambar BARU yang dipilih untuk di-upload
        if (selectedImageUri != null) {
            uploadImageToCloudinary(judul, isi, kategori);
        } else {
            // Jika tidak ada gambar baru, kirim data dengan URL gambar yang sudah ada (jika mode edit)
            if (listener != null) {
                listener.onArtikelSubmitted(judul, isi, kategori, existingImageUrl, isUpdate, updatePosition);
            }
            getParentFragmentManager().popBackStack();
        }
    }

    private void uploadImageToCloudinary(String judul, String isi, String kategori) {
        Toast.makeText(getContext(), "Mengunggah gambar...", Toast.LENGTH_SHORT).show();
        String uploadPreset = "masjidapp"; // GANTI DENGAN NAMA UNSIGNED PRESET ANDA

        MediaManager.get().upload(selectedImageUri)
                .unsigned(uploadPreset)
                .callback(new UploadCallback() {
                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        if (getContext() == null) return;
                        String imageUrl = (String) resultData.get("secure_url");
                        if (listener != null) {
                            listener.onArtikelSubmitted(judul, isi, kategori, imageUrl, isUpdate, updatePosition);
                        }
                        getParentFragmentManager().popBackStack();
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Upload gambar gagal: " + error.getDescription(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onStart(String requestId) {}
                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {}
                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {}
                }).dispatch();
    }
}