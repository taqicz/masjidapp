package com.example.masjidapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // <-- IMPORT GLIDE
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private MosqueAdapter adapter;
    private List<MosqueModel> mosqueList;
    private DatabaseReference databaseRef;

    private MaterialToolbar toolbar;
    private Button btnBack, btnAdd;

    public static final String EXTRA_MOSQUE_ID = "MOSQUE_ID";
    public static final String EXTRA_MOSQUE_NAME = "MOSQUE_NAME";
    public static final String EXTRA_MOSQUE_ADDRESS = "MOSQUE_ADDRESS";
    public static final String EXTRA_MOSQUE_RATING = "MOSQUE_RATING";
    public static final String EXTRA_MOSQUE_DISTANCE = "MOSQUE_DISTANCE";
    public static final String EXTRA_MOSQUE_IMAGE_URL = "MOSQUE_IMAGE_URL";
    public static final String EXTRA_MOSQUE_DESCRIPTION = "MOSQUE_DESCRIPTION";
    public static final String EXTRA_MOSQUE_ESTABLISHED_DATE = "MOSQUE_ESTABLISHED_DATE";
    public static final String EXTRA_MOSQUE_CHAIRMAN = "MOSQUE_CHAIRMAN";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        recyclerView = view.findViewById(R.id.recyclerView);
        btnBack = view.findViewById(R.id.btn_back);
        btnAdd = view.findViewById(R.id.btn_add);

        databaseRef = FirebaseDatabase.getInstance().getReference("masjid");

        setupToolbar();
        setupRecyclerView();
        setupAddButton();
        setupBackButton();
        fetchDataFromFirebase();

        return view;
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> {
            if (getActivity() != null) getActivity().onBackPressed();
        });
    }

    private void setupRecyclerView() {
        mosqueList = new ArrayList<>();
        adapter = new MosqueAdapter(requireContext(), mosqueList, mosque -> {
            Intent intent = new Intent(getActivity(), profilMasjid.class);
            intent.putExtra(EXTRA_MOSQUE_ID, mosque.getId());
            intent.putExtra(EXTRA_MOSQUE_NAME, mosque.getName());
            intent.putExtra(EXTRA_MOSQUE_ADDRESS, mosque.getAddress());
            intent.putExtra(EXTRA_MOSQUE_RATING, mosque.getRating());
            intent.putExtra(EXTRA_MOSQUE_IMAGE_URL, mosque.getImageUrl());
            intent.putExtra(EXTRA_MOSQUE_DESCRIPTION, mosque.getDescription());
            intent.putExtra(EXTRA_MOSQUE_ESTABLISHED_DATE, mosque.getEstablishedDate());
            intent.putExtra(EXTRA_MOSQUE_CHAIRMAN, mosque.getChairman());
            startActivity(intent);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) getActivity().onBackPressed();
        });
    }

    private void setupAddButton() {
        btnAdd.setOnClickListener(v -> {
            FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, new AddEditMosqueFragment());
            ft.addToBackStack(null);
            ft.commit();
        });
    }

    private void fetchDataFromFirebase() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mosqueList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    MosqueModel mosque = ds.getValue(MosqueModel.class);
                    if (mosque != null) {
                        mosque.setId(ds.getKey());
                        mosqueList.add(mosque);
                    }
                }
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Gagal mengambil data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
                Log.e("FirebaseFetch", "Gagal mengambil data", error.toException());
            }
        });
    }
}