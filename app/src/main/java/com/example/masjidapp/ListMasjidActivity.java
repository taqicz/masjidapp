package com.example.masjidapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ListMasjidActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MosqueAdapter adapter;
    private List<MosqueModel> mosqueList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_masjid);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mosqueList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            mosqueList.add(new MosqueModel("Masjid " + i, "Alamat " + i, 4.0f + i * 0.1f, i + " km", "https://example.com/image" + i + ".jpg"));
        }

        adapter = new MosqueAdapter(this, mosqueList, new MosqueAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MosqueModel mosque) {
                // Aksi ketika item diklik, misalnya:
                Toast.makeText(ListMasjidActivity.this, "Klik: " + mosque.getName(), Toast.LENGTH_SHORT).show();

                // Intent untuk pindah ke ProfilMasjidActivity
                Intent intent = new Intent(ListMasjidActivity.this, profilMasjid.class);

                // Kirim data ke ProfilMasjidActivity
                intent.putExtra("masjidName", mosque.getName());
                intent.putExtra("masjidAddress", mosque.getAddress());
                intent.putExtra("masjidImageUrl", mosque.getImageUrl());
                intent.putExtra("masjidRating", mosque.getRating());

                // Start ProfilMasjidActivity
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
    }
}
