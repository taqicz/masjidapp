/**
 * UpdateEventFragment digunakan untuk mengedit data event yang sudah ada.
 *
 * Fitur yang disediakan:
 * - Menampilkan data event lama untuk diedit.
 * - Memperbarui data event ke Firebase.
 * - Mengunggah gambar baru ke Cloudinary jika gambar diubah.
 * - Menggunakan DatePicker dan TimePicker untuk memilih tanggal dan waktu.
 */

package com.example.masjidapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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

public class UpdateEventFragment extends Fragment {

    // Komponen UI
    private TextInputEditText etTitle, etLocation, etType, etDescription, etDate, etTime;
    private ImageView ivImage;
    private MaterialButton btnSave, btnUploadImage;

    // URI gambar baru dan gambar lama
    private Uri selectedImageUri;
    private String currentImageUriString;

    // Jam mulai dan selesai (baru)
    private String startHour = "", endHour = "";

    private EventModel eventToEdit; // Data event yang akan diedit
    private DatabaseReference eventRef; // Referensi ke Firebase
    private static final String TAG = "UpdateEventFragment";

    // Launcher untuk memilih gambar dari galeri
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    try {
                        // Berikan izin untuk URI yang dipilih
                        requireActivity().getContentResolver().takePersistableUriPermission(
                                selectedImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } catch (SecurityException e) {
                        Log.e(TAG, "Gagal mengambil izin URI: " + e.getMessage());
                    }

                    // Tampilkan gambar baru yang dipilih
                    Glide.with(requireContext())
                            .load(selectedImageUri)
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_launcher_foreground)
                            .into(ivImage);
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_event, container, false);

        // Inisialisasi UI
        etTitle = view.findViewById(R.id.etEventTitle);
        etLocation = view.findViewById(R.id.etEventLocation);
        etType = view.findViewById(R.id.etEventType);
        etDescription = view.findViewById(R.id.etEventDescription);
        etDate = view.findViewById(R.id.etEventDate);
        etTime = view.findViewById(R.id.etEventStartTime);
        ivImage = view.findViewById(R.id.ivEventImage);
        btnSave = view.findViewById(R.id.btnSaveEvent);
        btnUploadImage = view.findViewById(R.id.btnUploadImage);

        eventRef = FirebaseDatabase.getInstance().getReference("events");

        // Ambil data event dari argument fragment
        if (getArguments() != null) {
            eventToEdit = (EventModel) getArguments().getSerializable("EVENT");

            if (eventToEdit != null) {
                // Tampilkan data lama ke input field
                etTitle.setText(eventToEdit.getTitle());
                etLocation.setText(eventToEdit.getLocation());
                etType.setText(eventToEdit.getType());
                etDescription.setText(eventToEdit.getDescription());
                etDate.setText(eventToEdit.getDate());

                if (eventToEdit.getStartTime() != null && eventToEdit.getEndTime() != null) {
                    etTime.setText(eventToEdit.getStartTime() + " - " + eventToEdit.getEndTime() + " WIB");
                } else if (eventToEdit.getStartTime() != null) {
                    etTime.setText(eventToEdit.getStartTime() + " WIB");
                }

                currentImageUriString = eventToEdit.getImageUri();
                if (currentImageUriString != null && !currentImageUriString.isEmpty()) {
                    Glide.with(requireContext())
                            .load(currentImageUriString)
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_launcher_foreground)
                            .into(ivImage);
                } else {
                    ivImage.setImageResource(R.drawable.ic_launcher_background);
                }
            } else {
                Toast.makeText(requireContext(), "Event tidak ditemukan", Toast.LENGTH_SHORT).show();
            }
        }

        // Event listener
        ivImage.setOnClickListener(v -> pickImage());
        btnUploadImage.setOnClickListener(v -> pickImage());
        etDate.setOnClickListener(v -> showDatePicker());
        etTime.setOnClickListener(v -> showTimePickerStart());
        btnSave.setOnClickListener(v -> updateEvent());

        return view;
    }

    // Fungsi untuk membuka galeri dan memilih gambar
    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        imagePickerLauncher.launch(intent);
    }

    // Fungsi untuk memperbarui data event
    private void updateEvent() {
        // Ambil input
        String title = etTitle.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String type = etType.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String date = etDate.getText().toString().trim();

        // Validasi input
        if (title.isEmpty() || location.isEmpty() || type.isEmpty() || description.isEmpty() || date.isEmpty()
                || (startHour.isEmpty() && eventToEdit.getStartTime() == null)) {
            Toast.makeText(requireContext(), "Semua field harus diisi, termasuk waktu.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set nilai baru ke objek event
        eventToEdit.setTitle(title);
        eventToEdit.setLocation(location);
        eventToEdit.setType(type);
        eventToEdit.setDescription(description);
        eventToEdit.setDate(date);
        if (!startHour.isEmpty()) eventToEdit.setStartTime(startHour);
        if (!endHour.isEmpty()) eventToEdit.setEndTime(endHour);

        // Upload gambar jika diubah
        if (selectedImageUri != null && (currentImageUriString == null || !selectedImageUri.toString().equals(currentImageUriString))) {
            MediaManager.get().upload(selectedImageUri)
                    .callback(new UploadCallback() {
                        @Override
                        public void onStart(String requestId) {
                            Toast.makeText(requireContext(), "Mengunggah gambar...", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onProgress(String requestId, long bytes, long totalBytes) {}

                        @Override
                        public void onSuccess(String requestId, Map resultData) {
                            // Simpan URL gambar baru ke event
                            String imageUrl = resultData.get("secure_url").toString();
                            eventToEdit.setImageUri(imageUrl);
                            saveEventToFirebase(); // Simpan ke Firebase
                        }

                        @Override
                        public void onError(String requestId, ErrorInfo error) {
                            Toast.makeText(requireContext(), "Gagal upload gambar: " + error.getDescription(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onReschedule(String requestId, ErrorInfo error) {}
                    }).dispatch();
        } else {
            // Jika gambar tidak berubah
            saveEventToFirebase();
        }
    }

    // Menyimpan event yang telah diedit ke Firebase
    private void saveEventToFirebase() {
        eventRef.child(eventToEdit.getId()).setValue(eventToEdit)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Event berhasil diperbarui", Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed(); // Kembali ke halaman sebelumnya
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Gagal memperbarui event: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    // Menampilkan dialog untuk memilih tanggal
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, dayOfMonth);
            String formatted = new SimpleDateFormat("EEEE, dd MMMM yyyy", new Locale("id", "ID")).format(selected.getTime());
            etDate.setText(formatted);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    // Menampilkan dua TimePicker untuk memilih jam mulai dan jam selesai
    private void showTimePickerStart() {
        Calendar calendar = Calendar.getInstance();
        int initialHour = calendar.get(Calendar.HOUR_OF_DAY);
        int initialMinute = calendar.get(Calendar.MINUTE);

        // Ambil waktu awal dari data jika tersedia
        if (eventToEdit != null && eventToEdit.getStartTime() != null) {
            try {
                String[] parts = eventToEdit.getStartTime().split(":");
                initialHour = Integer.parseInt(parts[0]);
                initialMinute = Integer.parseInt(parts[1]);
            } catch (Exception ignored) {}
        }

        new TimePickerDialog(requireContext(), (view, hourOfDay, minute) -> {
            startHour = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);

            new TimePickerDialog(requireContext(), (view2, endHourVal, endMinuteVal) -> {
                endHour = String.format(Locale.getDefault(), "%02d:%02d", endHourVal, endMinuteVal);
                etTime.setText(startHour + " - " + endHour + " WIB");
            }, hourOfDay, minute, true).show();

        }, initialHour, initialMinute, true).show();
    }
}
