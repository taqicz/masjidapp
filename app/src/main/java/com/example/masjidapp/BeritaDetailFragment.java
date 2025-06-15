package com.example.masjidapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide; // Pastikan import Glide ada

public class BeritaDetailFragment extends Fragment {
    private TextView txtJudul, txtIsi, txtKategori;
    private ImageView imgBerita;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Cukup inflate layout di sini
        return inflater.inflate(R.layout.fragment_berita_detail, container, false);
    }

    // Pindahkan semua logika setelah inflate ke onViewCreated (praktik terbaik)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtJudul = view.findViewById(R.id.tv_title);
        txtIsi = view.findViewById(R.id.tv_content);
        txtKategori = view.findViewById(R.id.tv_kategori);
        imgBerita = view.findViewById(R.id.iv_berita);

        Bundle args = getArguments();
        if (args != null) {
            // Gunakan kunci (key) yang SAMA PERSIS seperti saat mengirim dari BeritaFragment
            String judul = args.getString("judul_artikel");
            String isi = args.getString("isi_artikel");
            String kategori = args.getString("kategori_artikel");
            String imageUrl = args.getString("gambar_artikel_url");

            // Set data ke TextViews
            txtJudul.setText(judul);
            txtIsi.setText(isi);
            txtKategori.setText(kategori);

            // Hapus logika lama untuk gambar, ganti dengan Glide untuk memuat URL
            if (getContext() != null && imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.logo_app) // Opsional: gambar saat loading
                        .into(imgBerita);
            }
        }
    }
}