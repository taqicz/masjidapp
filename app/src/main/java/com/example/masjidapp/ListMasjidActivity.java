package com.example.masjidapp;

import android.os.Bundle;
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

        adapter = new MosqueAdapter(this, mosqueList);
        recyclerView.setAdapter(adapter);
    }
}
