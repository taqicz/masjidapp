/**
 * Kelas AddEventFragment digunakan untuk menambahkan event ke aplikasi MasjidApp.
 *
 * Fitur yang didukung:
 * - Input informasi event (judul, lokasi, jenis, deskripsi, tanggal, waktu, dan gambar).
 * - Pemilihan gambar dari galeri dan upload ke Cloudinary.
 * - Penyimpanan data event beserta URL gambar ke Firebase Realtime Database.
 * - Menggunakan DatePickerDialog dan TimePickerDialog untuk input tanggal dan waktu.
 */

package com.example.masjidapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

public class AddEventFragment extends Fragment {

    // Deklarasi komponen UI
    private TextInputEditText etTitle, etLocation, etType, etDescription, etDate, etTime;
    private ImageView ivImage;
    private MaterialButton btnSave, btnUploadImage;

    // Uri untuk gambar yang dipilih
    private Uri selectedImageUri;

    // Menyimpan jam mulai dan selesai
    private String startHour = "", endHour = "";

    // Referensi ke Firebase Realtime Database
    private DatabaseReference eventRef;

    // Launcher untuk mengambil gambar dari galeri
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();

                    // Simpan izin akses persistable untuk gambar
                    final int takeFlags = result.getData().getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    requireContext().getContentResolver().takePersistableUriPermission(selectedImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    // Tampilkan gambar di ImageView
                    Glide.with(requireContext())
                            .load(selectedImageUri)
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_launcher_foreground)
                            .into(ivImage);
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_event, container, false);

        // Inisialisasi komponen UI
        etTitle = view.findViewById(R.id.etEventTitle);
        etLocation = view.findViewById(R.id.etEventLocation);
        etType = view.findViewById(R.id.etEventType);
        etDescription = view.findViewById(R.id.etEventDescription);
        etDate = view.findViewById(R.id.etEventDate);
        etTime = view.findViewById(R.id.etEventStartTime);
        ivImage = view.findViewById(R.id.ivEventImage);
        btnSave = view.findViewById(R.id.btnSaveEvent);
        btnUploadImage = view.findViewById(R.id.btnUploadImage);

        // Ambil referensi database
        eventRef = FirebaseDatabase.getInstance().getReference("events");

        // Pasang listener untuk berbagai aksi
        ivImage.setOnClickListener(v -> pickImage());
        btnUploadImage.setOnClickListener(v -> pickImage());
        etDate.setOnClickListener(v -> showDatePicker());
        etTime.setOnClickListener(v -> showTimePickerStart());
        btnSave.setOnClickListener(v -> saveEvent());

        return view;
    }

    // Fungsi untuk membuka galeri dan memilih gambar
    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    // Fungsi untuk menyimpan event ke Firebase setelah gambar diunggah ke Cloudinary
    private void saveEvent() {
        // Ambil input dari user
        String title = etTitle.getText().toString();
        String location = etLocation.getText().toString();
        String type = etType.getText().toString();
        String description = etDescription.getText().toString();
        String date = etDate.getText().toString();
        String startTime = startHour;
        String endTime = endHour;

        // Validasi input
        if (title.isEmpty() || location.isEmpty() || type.isEmpty() || description.isEmpty() ||
                date.isEmpty() || startTime.isEmpty() || endTime.isEmpty() || selectedImageUri == null) {
            Toast.makeText(requireContext(), "Semua field harus diisi termasuk gambar", Toast.LENGTH_SHORT).show();
            return;
        }

        // Upload gambar ke Cloudinary
        Toast.makeText(requireContext(), "Mengunggah gambar...", Toast.LENGTH_SHORT).show();
        MediaManager.get().upload(selectedImageUri)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {}

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {}

                    // Setelah berhasil upload gambar, simpan data event ke Firebase
                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String imageUrl = resultData.get("secure_url").toString();

                        String id = eventRef.push().getKey();
                        EventModel event = new EventModel(id, title, location, date, startTime, endTime, type, imageUrl, description);

                        eventRef.child(id).setValue(event)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(requireContext(), "Event berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                                    requireActivity().onBackPressed();
                                })
                                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Gagal menambahkan event", Toast.LENGTH_SHORT).show());
                    }

                    // Tangani error saat upload
                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Toast.makeText(requireContext(), "Upload gagal: " + error.getDescription(), Toast.LENGTH_SHORT).show();
                    }

                    // Jika upload dijadwalkan ulang oleh Cloudinary
                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        Toast.makeText(requireContext(), "Upload dijadwalkan ulang", Toast.LENGTH_SHORT).show();
                    }
                })
                .dispatch();
    }

    // Menampilkan DatePickerDialog untuk memilih tanggal event
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, dayOfMonth);
            String formatted = new SimpleDateFormat("EEEE, dd MMMM yyyy", new Locale("id", "ID")).format(selected.getTime());
            etDate.setText(formatted);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    // Menampilkan TimePickerDialog untuk memilih jam mulai dan jam selesai
    private void showTimePickerStart() {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(requireContext(), (view, hour, minute) -> {
            startHour = String.format("%02d:%02d", hour, minute);

            // Setelah memilih jam mulai, tampilkan dialog untuk memilih jam selesai
            new TimePickerDialog(requireContext(), (view2, endHourOfDay, endMinute) -> {
                endHour = String.format("%02d:%02d", endHourOfDay, endMinute);
                String fullTime = startHour + " - " + endHour + " WIB";
                etTime.setText(fullTime);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();

        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }
}
