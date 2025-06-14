package com.example.masjidapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.masjidapp.network.CloudinaryApi;
import com.example.masjidapp.network.CloudinaryResponse;
import com.example.masjidapp.network.RetrofitClient;
import com.example.masjidapp.utils.PathUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.*;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListBukuActivity extends AppCompatActivity {

    private RecyclerView recyclerBuku;
    private BukuAdapter bukuAdapter;
    private ArrayList<BukuModel> listBuku;
    private FloatingActionButton fabTambahBuku;
    private Uri selectedImageUri;
    private DatabaseReference bukuRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_buku);

        recyclerBuku = findViewById(R.id.recyclerBukuFull);
        fabTambahBuku = findViewById(R.id.fabTambahBuku);

        listBuku = new ArrayList<>();
        bukuAdapter = new BukuAdapter(this, listBuku);
        recyclerBuku.setLayoutManager(new LinearLayoutManager(this));
        recyclerBuku.setAdapter(bukuAdapter);

        bukuRef = FirebaseDatabase.getInstance().getReference("buku");

        fabTambahBuku.setOnClickListener(v -> openImagePicker());
        bukuAdapter.setOnItemLongClickListener(this::showPilihanEditHapusDialog);

        loadBuku();
    }

    private void loadBuku() {
        bukuRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                listBuku.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    BukuModel buku = data.getValue(BukuModel.class);
                    listBuku.add(buku);
                }
                bukuAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            showTambahBukuDialog();
        }
    }

    private void showTambahBukuDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_tambah_buku, null);
        EditText etDeskripsi = view.findViewById(R.id.etDeskripsi);
        EditText etTahun = view.findViewById(R.id.etTahun);

        new AlertDialog.Builder(this)
                .setTitle("Tambah Buku Baru")
                .setView(view)
                .setPositiveButton("Tambah", (dialog, which) -> {
                    String deskripsi = etDeskripsi.getText().toString();
                    String tahun = etTahun.getText().toString();
                    if (!deskripsi.isEmpty() && !tahun.isEmpty() && selectedImageUri != null) {
                        uploadToCloudinaryThenSave(deskripsi, tahun, selectedImageUri);
                    } else {
                        Toast.makeText(this, "Lengkapi semua data", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void uploadToCloudinaryThenSave(String deskripsi, String tahun, Uri imageUri) {
        RequestBody requestFile = PathUtil.getRequestBodyFromUri(this, imageUri);
        if (requestFile == null) {
            Toast.makeText(this, "Gagal membaca gambar", Toast.LENGTH_SHORT).show();
            return;
        }

        MultipartBody.Part body = MultipartBody.Part.createFormData("file", "image.jpg", requestFile);
        RequestBody preset = RequestBody.create(okhttp3.MultipartBody.FORM, "masjidapp");

        CloudinaryApi api = RetrofitClient.getInstance().create(CloudinaryApi.class);
        api.uploadImage(preset, body).enqueue(new Callback<CloudinaryResponse>() {
            @Override
            public void onResponse(Call<CloudinaryResponse> call, Response<CloudinaryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String imageUrl = response.body().getSecureUrl();
                    String id = bukuRef.push().getKey();
                    BukuModel buku = new BukuModel(id, imageUrl, deskripsi, tahun);
                    bukuRef.child(id).setValue(buku);
                } else {
                    Toast.makeText(ListBukuActivity.this, "Upload gagal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CloudinaryResponse> call, Throwable t) {
                Toast.makeText(ListBukuActivity.this, "Gagal koneksi Cloudinary", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPilihanEditHapusDialog(BukuModel buku) {
        String[] opsi = {"Edit Deskripsi", "Hapus Buku"};

        new AlertDialog.Builder(this)
                .setTitle("Pilih Aksi")
                .setItems(opsi, (dialog, which) -> {
                    if (which == 0) {
                        showEditDeskripsiDialog(buku);
                    } else if (which == 1) {
                        bukuRef.child(buku.getId()).removeValue();
                    }
                })
                .show();
    }

    private void showEditDeskripsiDialog(BukuModel buku) {
        EditText input = new EditText(this);
        input.setText(buku.getDeskripsi());

        new AlertDialog.Builder(this)
                .setTitle("Edit Deskripsi")
                .setView(input)
                .setPositiveButton("Simpan", (dialog, which) -> {
                    String newDesc = input.getText().toString();
                    bukuRef.child(buku.getId()).child("deskripsi").setValue(newDesc);
                })
                .setNegativeButton("Batal", null)
                .show();
    }
}
