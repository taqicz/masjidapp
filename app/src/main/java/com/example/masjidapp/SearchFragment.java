package com.example.masjidapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private MosqueAdapter adapter;
    private List<MosqueModel> mosqueList;
    private MaterialToolbar toolbar;
    private Button btnBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Initialize views
        toolbar = view.findViewById(R.id.toolbar);
        recyclerView = view.findViewById(R.id.recyclerView);
        btnBack = view.findViewById(R.id.btn_back);

        setupToolbar();
        setupRecyclerView();
        setupBackButton();

        return view;
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }

    private void setupRecyclerView() {
        // Initialize mosque list with complete data
        mosqueList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            mosqueList.add(new MosqueModel(
                    "Masjid Agung " + i,
                    "Jl. Masjid Raya No." + i + ", Kota Contoh",
                    4.0f + i * 0.1f,
                    i + " km",
                    "https://example.com/mosque_images/image" + i + ".jpg",
                    "Masjid Agung " + i + " adalah masjid bersejarah yang dibangun pada tahun 19" + (i < 10 ? "0" + i : i),
                    "19" + (i < 10 ? "0" + i : i) + "-01-01",
                    "H. Ahmad Budiman No." + i
            ));
        }

        // Setup adapter with click listener
        adapter = new MosqueAdapter(requireContext(), mosqueList, mosque -> {
            Toast.makeText(requireContext(), "Mengunjungi: " + mosque.getName(), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(requireActivity(), profilMasjid.class);
            // Pass all mosque data to detail activity
            intent.putExtra("masjidName", mosque.getName());
            intent.putExtra("masjidAddress", mosque.getAddress());
            intent.putExtra("masjidRating", mosque.getRating());
            intent.putExtra("masjidDistance", mosque.getDistance());
            intent.putExtra("masjidImageUrl", mosque.getImageUrl());
            intent.putExtra("masjidDescription", mosque.getDescription());
            intent.putExtra("masjidEstablishedDate", mosque.getEstablishedDate());
            intent.putExtra("masjidChairman", mosque.getChairman());

            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }
}