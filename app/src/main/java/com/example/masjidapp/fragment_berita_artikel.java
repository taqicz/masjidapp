package com.example.masjidapp;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class fragment_berita_artikel extends Fragment {

    private OnArtikelSubmitListener listener;
    private EditText edtJudul, edtIsi, edtKategori;
    private Button btnKirim;
    private boolean isUpdate = false;
    private int updatePosition = -1;

    public fragment_berita_artikel() {}

    // Interface untuk komunikasi antar fragment
    public interface OnArtikelSubmitListener {
        void onArtikelSubmitted(String judul, String isi, String kategori, boolean isUpdate, int updatePosition);
    }

    // Setter untuk listener
    public void setOnArtikelSubmitListener(OnArtikelSubmitListener listener) {
        this.listener = listener;
    }

    // Method untuk menerima artikel yang akan diedit
    public void setArtikelToEdit(BeritaArtikelModel artikel, int position) {
        isUpdate = true;
        updatePosition = position;

        if (edtJudul != null && edtIsi != null && edtKategori != null) {
            edtJudul.setText(artikel.getTitle());
            edtIsi.setText(artikel.getContent());
            edtKategori.setText(artikel.getKategori());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_berita_artikel, container, false);

        edtJudul = view.findViewById(R.id.isiJudul);  // Pastikan ini sudah benar
        edtIsi = view.findViewById(R.id.isiArtikel);
        edtKategori = view.findViewById(R.id.isiKategori);
        btnKirim = view.findViewById(R.id.btnKirim);

        // Jika mode edit, data artikel harus dimasukkan ke EditText
        if (getArguments() != null) {
            edtJudul.setText(getArguments().getString("judul", ""));
            edtIsi.setText(getArguments().getString("isi", ""));
            edtKategori.setText(getArguments().getString("kategori", ""));
        }

        // Menangani klik pada tombol Kirim
        btnKirim.setOnClickListener(v -> {
            String judul = edtJudul.getText().toString().trim();
            String isi = edtIsi.getText().toString().trim();
            String kategori = edtKategori.getText().toString().trim();

            if (!judul.isEmpty() && !isi.isEmpty() && !kategori.isEmpty()) {
                if (listener != null) {
                    listener.onArtikelSubmitted(judul, isi, kategori, isUpdate, updatePosition);
                }
                getParentFragmentManager().popBackStack();  // Menutup fragment setelah data dikirim
            }
        });

        return view;
    }

}
