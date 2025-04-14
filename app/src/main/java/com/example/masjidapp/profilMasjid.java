package com.example.masjidapp;

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
    }
}
