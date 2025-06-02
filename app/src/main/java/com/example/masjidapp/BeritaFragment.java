package com.example.masjidapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import java.util.ArrayList;
import java.util.List;

public class BeritaFragment extends Fragment {
    private RecyclerView recyclerViewTrending;
    private RecyclerView recyclerViewArtikel;
    private BeritaTrendingAdapter trendingAdapter;
    private BeritaArtikelAdapter artikelAdapter;
    private List<BeritaTrendingModel> trendingList;
    private List<BeritaArtikelModel> artikelList;

    public BeritaFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        trendingList = new ArrayList<>();
        trendingList.add(new BeritaTrendingModel("Politik", "Kebijakan Baru Pemerintah"));
        trendingList.add(new BeritaTrendingModel("Ekonomi", "Inflasi Menurun di Kuartal Pertama"));
        trendingList.add(new BeritaTrendingModel("Teknologi", "AI Meningkatkan Produktivitas"));
        trendingList.add(new BeritaTrendingModel("Olahraga", "Tim Nasional Raih Kemenangan"));
        trendingList.add(new BeritaTrendingModel("Hiburan", "Film Baru Pecahkan Rekor"));

        artikelList = new ArrayList<>();
        artikelList.add(new BeritaArtikelModel("Sejarah Masjid", "Masjid ini dibangun pada abad ke-18 dengan arsitektur tradisional...", "Sejarah"));
        artikelList.add(new BeritaArtikelModel("Kajian Rutin", "Jangan lewatkan kajian rutin setiap Jumat malam di masjid...", "Kajian"));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_berita, container, false);

        recyclerViewTrending = view.findViewById(R.id.recyclerViewTrending);
        recyclerViewArtikel = view.findViewById(R.id.recyclerViewArtikel);

        trendingAdapter = new BeritaTrendingAdapter(trendingList);
        artikelAdapter = new BeritaArtikelAdapter(artikelList);

        // ✅ Listener hapus
        artikelAdapter.setOnDeleteClickListener(position -> {
            artikelList.remove(position);
            artikelAdapter.notifyItemRemoved(position);
        });

        recyclerViewTrending.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewTrending.setAdapter(trendingAdapter);

        recyclerViewArtikel.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewArtikel.setAdapter(artikelAdapter);

        Button buttonAdd = view.findViewById(R.id.berita_add);
        buttonAdd.setOnClickListener(v -> {
            fragment_berita_artikel fragment = new fragment_berita_artikel();
            fragment.setOnArtikelSubmitListener((judul, isi, kategori, isUpdate, updatePosition) -> {
                if (isUpdate && updatePosition >= 0) {
                    BeritaArtikelModel model = artikelList.get(updatePosition);
                    model.setTitle(judul);
                    model.setContent(isi);
                    model.setKategori(kategori);
                    artikelAdapter.notifyItemChanged(updatePosition);
                } else {
                    artikelList.add(new BeritaArtikelModel(judul, isi, kategori));
                    artikelAdapter.notifyItemInserted(artikelList.size() - 1);
                }
            });

            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Long press untuk mengedit
        artikelAdapter.setOnItemLongClickListener(position -> {
            fragment_berita_artikel fragment = new fragment_berita_artikel();
            BeritaArtikelModel artikel = artikelList.get(position);

            Bundle bundle = new Bundle();
            bundle.putString("judul", artikel.getTitle());
            bundle.putString("isi", artikel.getContent());
            bundle.putString("kategori", artikel.getKategori());

            fragment.setArguments(bundle);
            fragment.setOnArtikelSubmitListener((judul, isi, kategori, isUpdate, updatePosition) -> {
                if (isUpdate && updatePosition >= 0) {
                    BeritaArtikelModel model = artikelList.get(updatePosition);
                    model.setTitle(judul);
                    model.setContent(isi);
                    model.setKategori(kategori);
                    artikelAdapter.notifyItemChanged(updatePosition);
                } else {
                    artikelList.add(new BeritaArtikelModel(judul, isi, kategori));
                    artikelAdapter.notifyItemInserted(artikelList.size() - 1);
                }
            });

            fragment.setArtikelToEdit(artikel, position);

            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Klik biasa untuk buka detail
        artikelAdapter.setOnItemClickListener(position -> {
            BeritaArtikelModel artikel = artikelList.get(position);
            BeritaDetailFragment detailFragment = new BeritaDetailFragment();

            Bundle bundle = new Bundle();
            bundle.putString("judul", artikel.getTitle());
            bundle.putString("isi", artikel.getContent());
            bundle.putString("kategori", artikel.getKategori());

            detailFragment.setArguments(bundle);

            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, detailFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }
}
