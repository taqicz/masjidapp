package com.example.masjidapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Donasi extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DonasiAdapter adapter;
    private List<ModelDonasi> listDonasi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donasi);

        recyclerView = findViewById(R.id.rvDonasi);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listDonasi = new ArrayList<>();
        listDonasi.add(new ModelDonasi("Ahmad", "Rp 100.000"));
        listDonasi.add(new ModelDonasi("Fatimah", "Rp 50.000"));

        adapter = new DonasiAdapter(listDonasi);
        recyclerView.setAdapter(adapter);
    }
}
