package com.example.masjidapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class profilMasjid extends AppCompatActivity {

    private TextView tvNamaMasjidDiToolbar;
    private TextView tvDeskripsi, tvTanggalBerdiri, tvAlamat, tvKetuaTakmir;
    private ImageView imgMasjid;
    private Toolbar toolbar;
    private String mosqueNameForFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_masjid);

        // Hubungkan elemen UI
        tvNamaMasjidDiToolbar = findViewById(R.id.tvNamaMasjid);
        imgMasjid = findViewById(R.id.imgMasjid);
        tvDeskripsi = findViewById(R.id.tvDeskripsi);
        tvTanggalBerdiri = findViewById(R.id.tvTanggalBerdiri);
        tvAlamat = findViewById(R.id.tvAlamat);
        tvKetuaTakmir = findViewById(R.id.tvKetuaTakmir);

        // Ambil data dari Intent
        Intent intent = getIntent();
        String mosqueId = intent.getStringExtra(SearchFragment.EXTRA_MOSQUE_ID);
        String masjidName = intent.getStringExtra(SearchFragment.EXTRA_MOSQUE_NAME);
        String masjidAddress = intent.getStringExtra(SearchFragment.EXTRA_MOSQUE_ADDRESS);
        String masjidImageUrl = intent.getStringExtra(SearchFragment.EXTRA_MOSQUE_IMAGE_URL);
        float masjidRating = intent.getFloatExtra(SearchFragment.EXTRA_MOSQUE_RATING, 0f);
        String masjidDescription = intent.getStringExtra(SearchFragment.EXTRA_MOSQUE_DESCRIPTION);
        String masjidEstablishedDate = intent.getStringExtra(SearchFragment.EXTRA_MOSQUE_ESTABLISHED_DATE);
        String masjidChairman = intent.getStringExtra(SearchFragment.EXTRA_MOSQUE_CHAIRMAN);

        mosqueNameForFirebase = mosqueId; // key untuk update/delete

        // Set data ke UI, dengan fallback jika kosong/null
        tvNamaMasjidDiToolbar.setText(nonNull(masjidName, "Nama masjid tidak tersedia"));
        tvAlamat.setText(nonNull(masjidAddress, "Alamat tidak tersedia"));
        tvDeskripsi.setText(nonNull(masjidDescription, "Deskripsi tidak tersedia."));
        tvTanggalBerdiri.setText(nonNull(masjidEstablishedDate, "Tanggal berdiri tidak tersedia"));
        tvKetuaTakmir.setText(nonNull(masjidChairman, "Ketua takmir tidak tersedia"));

        // Load gambar dari URL
        if (masjidImageUrl != null && !masjidImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(masjidImageUrl)
                    .placeholder(R.drawable.sample_masjid)
                    .error(R.drawable.sample_masjid)
                    .into(imgMasjid);
        } else {
            imgMasjid.setImageResource(R.drawable.sample_masjid);
        }

        // Update Rating
        Button btnUpdateRating = findViewById(R.id.btnUpdateRating);
        btnUpdateRating.setOnClickListener(v -> {
            // Inflate layout dialog_rating.xml
            android.view.LayoutInflater inflater = getLayoutInflater();
            android.view.View dialogView = inflater.inflate(R.layout.dialog_rating, null);
            RatingBar ratingBar = dialogView.findViewById(R.id.ratingBar);
            ratingBar.setNumStars(5);
            ratingBar.setStepSize(0.5f);
            ratingBar.setRating(masjidRating);

            new AlertDialog.Builder(this)
                    .setTitle("Update Rating Masjid")
                    .setView(dialogView)
                    .setPositiveButton("Update", (dialog, which) -> {
                        float newRating = ratingBar.getRating();
                        // Update ke Firebase
                        if (mosqueNameForFirebase != null) {
                            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("masjid");
                            databaseRef.child(mosqueNameForFirebase).child("rating").setValue(newRating)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Rating diperbarui ke: " + newRating, Toast.LENGTH_SHORT).show();
                                        tvDeskripsi.setText("Rating: " + newRating); // Opsional, update UI
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(this, "Gagal update rating", Toast.LENGTH_SHORT).show());
                        }
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });

        // Delete
        Button btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Hapus Masjid")
                    .setMessage("Apakah Anda yakin ingin menghapus masjid '" + nonNull(masjidName, "-") + "'?")
                    .setPositiveButton("Ya", (dialog, which) -> {
                        if (mosqueNameForFirebase != null) {
                            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("masjid");
                            databaseRef.child(mosqueNameForFirebase).removeValue()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Masjid '" + masjidName + "' telah dihapus.", Toast.LENGTH_SHORT).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(this, "Gagal menghapus masjid.", Toast.LENGTH_SHORT).show());
                        }
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });
    }

    // Helper untuk fallback jika null
    private String nonNull(String value, String fallback) {
        return (value != null && !value.isEmpty()) ? value : fallback;
    }
}