package com.example.masjidapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ListBukuActivity extends AppCompatActivity {

    private RecyclerView recyclerBuku;
    private BukuAdapter bukuAdapter;
    private ArrayList<BukuModel> listBuku;
    private FloatingActionButton fabTambahBuku;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_buku);

        recyclerBuku = findViewById(R.id.recyclerBukuFull);
        fabTambahBuku = findViewById(R.id.fabTambahBuku);

        listBuku = SharedBukuData.getBukuList();

        bukuAdapter = new BukuAdapter(this, listBuku);
        recyclerBuku.setLayoutManager(new LinearLayoutManager(this));
        recyclerBuku.setAdapter(bukuAdapter);

        fabTambahBuku.setOnClickListener(v -> showTambahBukuDialog());

        bukuAdapter.setOnItemLongClickListener(buku -> showPilihanEditHapusDialog(buku));
    }

    private void showTambahBukuDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tambah Buku Baru");

        View view = getLayoutInflater().inflate(R.layout.dialog_tambah_buku, null);
        EditText etDeskripsi = view.findViewById(R.id.etDeskripsi);
        EditText etTahun = view.findViewById(R.id.etTahun);

        builder.setView(view);
        builder.setPositiveButton("Tambah", (dialog, which) -> {
            String deskripsi = etDeskripsi.getText().toString();
            String tahun = etTahun.getText().toString();

            if (!deskripsi.isEmpty() && !tahun.isEmpty()) {
                int idBaru = SharedBukuData.generateNewId();
                int gambarDefault = R.drawable.buku1;

                BukuModel bukuBaru = new BukuModel(idBaru, gambarDefault, deskripsi, tahun);
                SharedBukuData.addBuku(bukuBaru);
                bukuAdapter.notifyItemInserted(listBuku.size() - 1);
            } else {
                Toast.makeText(this, "Deskripsi dan tahun wajib diisi", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Batal", null);
        builder.show();
    }

    private void showPilihanEditHapusDialog(BukuModel buku) {
        String[] opsi = {"Edit Deskripsi", "Hapus Buku"};

        new AlertDialog.Builder(this)
                .setTitle("Pilih Aksi")
                .setItems(opsi, (dialog, which) -> {
                    if (which == 0) {
                        showEditDeskripsiDialog(buku);
                    } else if (which == 1) {
                        SharedBukuData.deleteBuku(buku.getId());
                        bukuAdapter.notifyDataSetChanged();
                        Toast.makeText(this, "Buku dihapus", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void showEditDeskripsiDialog(BukuModel buku) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Deskripsi");

        EditText input = new EditText(this);
        input.setText(buku.getDeskripsi());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Simpan", (dialog, which) -> {
            String deskripsiBaru = input.getText().toString();
            if (!deskripsiBaru.isEmpty()) {
                SharedBukuData.updateBuku(buku.getId(), deskripsiBaru);
                bukuAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Deskripsi diperbarui", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Batal", null);
        builder.show();
    }
}
