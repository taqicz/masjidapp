package com.example.masjidapp;

import android.app.AlertDialog;
import android.widget.EditText;
import android.widget.RatingBar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class profilMasjid extends AppCompatActivity {

    private TextView tvNamaMasjid, tvDeskripsi;
    private TextView tvTanggalBerdiri, tvAlamat, tvKetuaTakmir;
    private ImageView imgMasjid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_masjid);

        // Menghubungkan elemen UI dengan komponen di layout
        tvNamaMasjid = findViewById(R.id.tvNamaMasjid);
        imgMasjid = findViewById(R.id.imgMasjid);
        tvDeskripsi = findViewById(R.id.tvDeskripsi);
        tvTanggalBerdiri = findViewById(R.id.tvTanggalBerdiri);
        tvAlamat = findViewById(R.id.tvAlamat);
        tvKetuaTakmir = findViewById(R.id.tvKetuaTakmir);

        // Mendapatkan data yang dikirimkan melalui Intent
        Intent intent = getIntent();
        String masjidName = intent.getStringExtra("masjidName");
        String masjidAddress = intent.getStringExtra("masjidAddress");
        String masjidImageUrl = intent.getStringExtra("masjidImageUrl");
        float masjidRating = intent.getFloatExtra("masjidRating", 0f);
        String description = intent.getStringExtra("descriptionMasjid");
        if (description != null && !description.isEmpty()) {
            tvDeskripsi.setText(description);
        } else {
            tvDeskripsi.setText("Rating: " + masjidRating); // Fallback ke rating jika deskripsi tidak ada
        }

        // Menampilkan data di UI
        tvNamaMasjid.setText(masjidName);
        tvAlamat.setText("Alamat: " + masjidAddress);
        tvDeskripsi.setText("Rating: " + masjidRating);

        // TODO: Load image from masjidImageUrl ke imgMasjid jika pakai Glide/Picasso

        Button btnInfaq = findViewById(R.id.btnInfaq);
        btnInfaq.setOnClickListener(v -> {
            Toast.makeText(this, "Terima kasih atas niat baik Anda untuk berinfaq 😊", Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(this, Donasi.class);
            startActivity(intent1);
        });

        Button btnUpdateRating = findViewById(R.id.btnUpdateRating);
        btnUpdateRating.setOnClickListener(v -> {
            // Dialog input rating
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Update Rating Masjid");

            RatingBar ratingBar = new RatingBar(this);
            ratingBar.setNumStars(5);
            ratingBar.setStepSize(0.5f);
            ratingBar.setRating(masjidRating);

            builder.setView(ratingBar);

            builder.setPositiveButton("Update", (dialog, which) -> {
                float newRating = ratingBar.getRating();
                tvDeskripsi.setText("Rating: " + newRating);
                Toast.makeText(this, "Rating diperbarui ke: " + newRating, Toast.LENGTH_SHORT).show();
                // TODO: Kirim ke server jika pakai database
            });

            builder.setNegativeButton("Batal", null);
            builder.show();
        });

        Button btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Hapus Masjid")
                    .setMessage("Apakah Anda yakin ingin menghapus masjid ini?")
                    .setPositiveButton("Ya", (dialog, which) -> {
                        Toast.makeText(this, "Masjid telah dihapus.", Toast.LENGTH_SHORT).show();
                        // TODO: Delete dari database jika perlu
                        finish(); // Kembali ke activity sebelumnya
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });

    }
}
