package com.example.masjidapp;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class BeritaDetailFragment extends Fragment {
    private TextView txtJudul, txtIsi, txtKategori;
    private ImageView imgBerita;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_berita_detail, container, false);

        txtJudul = view.findViewById(R.id.tv_title);
        txtIsi = view.findViewById(R.id.tv_content);
        txtKategori = view.findViewById(R.id.tv_kategori);
        imgBerita = view.findViewById(R.id.iv_berita);

        Bundle args = getArguments();
        if (args != null) {
            txtJudul.setText(args.getString("judul"));
            txtIsi.setText(args.getString("isi"));
            txtKategori.setText(args.getString("kategori"));

            // Untuk gambar, Anda dapat menggunakan drawable lokal atau menangani seperti sebelumnya
            if (args.getInt("imageResource", 0) != 0) {
                imgBerita.setImageResource(args.getInt("imageResource"));
            }
        }

        return view;
    }
}