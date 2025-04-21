package com.example.masjidapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class FragmentHome extends Fragment {

    private RecyclerView eventsRecyclerView;
    private RecyclerView mosquesRecyclerView;
    private FloatingActionButton notificationFab;

    private List<EventModel> eventList;
    private EventAdapter eventAdapter;

    public FragmentHome() {
        // Required empty public constructor
    }

    public static FragmentHome newInstance(String param1, String param2) {
        FragmentHome fragment = new FragmentHome();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize views
        eventsRecyclerView = view.findViewById(R.id.eventsRecyclerView);
        mosquesRecyclerView = view.findViewById(R.id.mosquesRecyclerView);
        notificationFab = view.findViewById(R.id.notificationFab);
        CardView prayerTimeCard = view.findViewById(R.id.prayerTimeCard);
        CardView btnLihatMasjidLainnya = view.findViewById(R.id.btnLihatMasjidLainnya);

        // Event listeners
        btnLihatMasjidLainnya.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ListMasjidActivity.class);
            startActivity(intent);
        });

        prayerTimeCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PrayerTimeDetailActivity.class);
            startActivity(intent);
        });

        notificationFab.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Notifikasi", Toast.LENGTH_SHORT).show();
        });

        setupEventsRecyclerView();
        setupMosquesRecyclerView();

        return view;
    }

    private void setupEventsRecyclerView() {
        eventsRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        eventList = new ArrayList<>();

        // Tambahkan dummy event
        addDummyEventsToList(eventList);

        eventAdapter = new EventAdapter(getContext(), eventList);

        // Setup klik untuk update
        eventAdapter.setOnEventUpdateClickListener((event, position) -> {
            Intent intent = new Intent(getActivity(), UpdateEventActivity.class);
            intent.putExtra("EVENT", event);
            intent.putExtra("EVENT_POSITION", position);
            startActivity(intent);  // Tidak perlu result karena data lokal saja
        });

        eventsRecyclerView.setAdapter(eventAdapter);
    }

    private void setupMosquesRecyclerView() {
        mosquesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<MosqueModel> mosqueList = new ArrayList<>();
        mosqueList.add(new MosqueModel("Masjid Al-Hikmah", "Jl. Masjid No. 123, Jakarta", 4.5f, "1.2 km", "https://example.com/image1.jpg", "Masjid Al-Hikmah adalah pusat kajian Islam.", "1995", "Ust. Ahmad"));
        mosqueList.add(new MosqueModel("Masjid Jami", "Jl. Raya No. 45, Jakarta", 4.2f, "2.5 km", "https://example.com/image2.jpg", "Masjid Jami terkenal dengan khutbah Jumatnya.", "1980", "Ust. Budi"));
        mosqueList.add(new MosqueModel("Masjid An-Nur", "Jl. Utama No. 67, Jakarta", 4.7f, "3.1 km", "https://example.com/image3.jpg", "Masjid An-Nur menyediakan program buka puasa.", "2005", "Ust. Zaki"));

        MosqueAdapter adapter = new MosqueAdapter(getContext(), mosqueList, mosque -> {
            Intent intent = new Intent(getActivity(), profilMasjid.class);
            intent.putExtra("masjidName", mosque.getName());
            intent.putExtra("masjidAddress", mosque.getAddress());
            intent.putExtra("imageUrl", mosque.getImageUrl());
            intent.putExtra("masjidRating", mosque.getRating());
            intent.putExtra("descriptionMasjid", mosque.getDescription());
            intent.putExtra("establishedDate", mosque.getEstablishedDate());
            intent.putExtra("masjidChairman", mosque.getChairman());
            startActivity(intent);
        });

        mosquesRecyclerView.setAdapter(adapter);
    }

    private void addDummyEventsToList(List<EventModel> eventList) {
        eventList.clear();  // Hapus jika sudah ada (refresh)
        eventList.add(new EventModel(
                "Pengajian Akbar",
                "Masjid Jami",
                "Minggu, 16 Juni 2023",
                "09:00",
                "11:30",
                "Pengajian",
                "android.resource://" + getActivity().getPackageName() + "/drawable/default_event_image",
                "Acara pengajian akbar yang akan dihadiri oleh para ustadz terkemuka."));

        eventList.add(new EventModel(
                "Buka Puasa Bersama",
                "Masjid Al-Hikmah",
                "Senin, 18 Juni 2023",
                "17:30",
                "19:00",
                "Buka Puasa",
                "android.resource://" + getActivity().getPackageName() + "/drawable/default_event_image",
                "Buka puasa bersama masyarakat sekitar masjid untuk meningkatkan silaturahmi."));

        eventList.add(new EventModel(
                "Shalat Taraweh",
                "Masjid Baitul Rahman",
                "Selasa, 19 Juni 2023",
                "20:00",
                "21:30",
                "Taraweh",
                "android.resource://" + getActivity().getPackageName() + "/drawable/default_event_image",
                "Shalat Taraweh bersama umat Muslim setempat."));
    }
}
