package com.example.masjidapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log; // Import Log
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

// Sangat direkomendasikan untuk menggunakan library seperti Glide atau Picasso
// Untuk memuat gambar. Tambahkan dependensi di build.gradle (Module: app)
// implementation 'com.github.bumptech.glide:glide:4.12.0' // Cek versi terbaru
// annotationProcessor 'com.github.bumptech.glide:compiler:4.12.0'
import com.bumptech.glide.Glide; // Contoh jika menggunakan Glide

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class UpdateEventActivity extends AppCompatActivity {

    private TextInputEditText etTitle, etLocation, etType, etDescription, etDate, etTime;
    private ImageView ivImage;
    private MaterialButton btnSave;
    private Uri selectedImageUri; // Ini akan menyimpan URI gambar yang baru dipilih atau URI lama
    private String currentImageUriString; // Untuk menyimpan URI string dari eventToEdit

    private String startHour = "", endHour = "";

    private EventModel eventToEdit;
    private DatabaseReference eventRef;
    private static final int IMAGE_PICK_CODE = 1001;
    private static final String TAG = "UpdateEventActivity"; // Untuk logging

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_event);

        // Inisialisasi view
        etTitle = findViewById(R.id.etEventTitle);
        etLocation = findViewById(R.id.etEventLocation);
        etType = findViewById(R.id.etEventType);
        etDescription = findViewById(R.id.etEventDescription);
        etDate = findViewById(R.id.etEventDate);
        etTime = findViewById(R.id.etEventStartTime);
        ivImage = findViewById(R.id.ivEventImage);
        btnSave = findViewById(R.id.btnSaveEvent);

        // Firebase reference
        eventRef = FirebaseDatabase.getInstance().getReference("events");

        // Ambil data event yang akan diedit
        eventToEdit = (EventModel) getIntent().getSerializableExtra("EVENT");

        if (eventToEdit != null) {
            etTitle.setText(eventToEdit.getTitle());
            etLocation.setText(eventToEdit.getLocation());
            etType.setText(eventToEdit.getType());
            etDescription.setText(eventToEdit.getDescription());
            etDate.setText(eventToEdit.getDate());
            if (eventToEdit.getStartTime() != null && eventToEdit.getEndTime() != null) {
                etTime.setText(eventToEdit.getStartTime() + " - " + eventToEdit.getEndTime() + " WIB");
            } else if (eventToEdit.getStartTime() != null) {
                etTime.setText(eventToEdit.getStartTime() + " WIB"); // Jika hanya ada start time
            }


            currentImageUriString = eventToEdit.getImageUri(); // Simpan URI string yang ada
            if (currentImageUriString != null && !currentImageUriString.isEmpty()) {
                selectedImageUri = Uri.parse(currentImageUriString); // Inisialisasi selectedImageUri dengan URI lama
                try {
                    // Coba muat gambar. Ini adalah baris yang menyebabkan error (line 66 di kode Anda)
                    // Menggunakan Glide untuk penanganan yang lebih baik (opsional, tapi direkomendasikan)
                    Glide.with(this)
                            .load(selectedImageUri)
                            .placeholder(R.drawable.ic_launcher_background) // Ganti dengan placeholder Anda
                            .error(R.drawable.ic_launcher_foreground) // Ganti dengan gambar error Anda
                            .into(ivImage);
                    // Jika tidak menggunakan Glide:
                    // ivImage.setImageURI(selectedImageUri);
                } catch (SecurityException e) {
                    Log.e(TAG, "SecurityException saat memuat URI gambar lama: " + selectedImageUri, e);
                    Toast.makeText(this, "Gagal memuat gambar sebelumnya. Izin mungkin telah dicabut.", Toast.LENGTH_LONG).show();
                    // Set gambar placeholder jika gagal
                    ivImage.setImageResource(R.drawable.ic_launcher_background); // Ganti dengan placeholder Anda
                } catch (Exception e) {
                    Log.e(TAG, "Exception lain saat memuat URI gambar lama: " + selectedImageUri, e);
                    Toast.makeText(this, "Gagal memuat gambar sebelumnya.", Toast.LENGTH_LONG).show();
                    ivImage.setImageResource(R.drawable.ic_launcher_background); // Ganti dengan placeholder Anda
                }
            } else {
                // Jika tidak ada URI gambar sebelumnya, set placeholder
                ivImage.setImageResource(R.drawable.ic_launcher_background); // Ganti dengan placeholder Anda
            }
        } else {
            Toast.makeText(this, "Event tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ivImage.setOnClickListener(v -> pickImage());
        etDate.setOnClickListener(v -> showDatePicker());
        etTime.setOnClickListener(v -> showTimePickerStart());
        btnSave.setOnClickListener(v -> updateEvent());
    }

    private void updateEvent() {
        String title = etTitle.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String type = etType.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String date = etDate.getText().toString().trim();

        if (title.isEmpty() || location.isEmpty() || type.isEmpty() || description.isEmpty() || date.isEmpty() || (startHour.isEmpty() && eventToEdit.getStartTime() == null) ) {
            Toast.makeText(this, "Semua field harus diisi, termasuk waktu.", Toast.LENGTH_SHORT).show();
            return;
        }

        eventToEdit.setTitle(title);
        eventToEdit.setLocation(location);
        eventToEdit.setType(type);
        eventToEdit.setDescription(description);
        eventToEdit.setDate(date);

        // Hanya update waktu jika pengguna memilih waktu baru
        if (!startHour.isEmpty()) {
            eventToEdit.setStartTime(startHour);
        }
        if (!endHour.isEmpty()) {
            eventToEdit.setEndTime(endHour);
        }
        // Jika pengguna tidak memilih waktu baru, waktu lama (dari eventToEdit.getStartTime/EndTime) akan tetap digunakan.

        // Cek apakah gambar baru dipilih.
        // selectedImageUri akan berbeda dari Uri.parse(currentImageUriString) jika gambar baru dipilih.
        // Atau jika currentImageUriString awalnya null/kosong dan gambar baru dipilih.
        if (selectedImageUri != null && (currentImageUriString == null || !selectedImageUri.toString().equals(currentImageUriString))) {
            // Ini berarti ada gambar baru yang dipilih atau gambar awal tidak ada dan sekarang ada
            eventToEdit.setImageUri(selectedImageUri.toString());
        } else if (selectedImageUri == null && currentImageUriString != null) {
            // Jika gambar dihapus (misalnya, ada fitur hapus gambar yang tidak ada di kode ini)
            // eventToEdit.setImageUri(null); // Atau string kosong, tergantung model Anda
        }
        // Jika tidak ada perubahan pada gambar (selectedImageUri sama dengan currentImageUriString yang lama, atau keduanya null),
        // maka eventToEdit.getImageUri() tidak perlu diubah.


        eventRef.child(eventToEdit.getId()).setValue(eventToEdit)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(UpdateEventActivity.this, "Event berhasil diperbarui", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(UpdateEventActivity.this, "Gagal memperbarui event: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        // Baris ini penting untuk Android versi baru agar izin URI bertahan lebih lama jika memungkinkan
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData(); // Ini adalah URI baru yang memiliki izin

            // Penting: Coba ambil izin persisten jika memungkinkan (tidak selalu berhasil)
            // try {
            //    final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //    getContentResolver().takePersistableUriPermission(selectedImageUri, takeFlags);
            // } catch (SecurityException se) {
            //    Log.e(TAG, "Gagal mengambil izin URI persisten.", se);
            // }

            // Menggunakan Glide untuk memuat gambar (opsional, tapi direkomendasikan)
            Glide.with(this)
                    .load(selectedImageUri)
                    .placeholder(R.drawable.ic_launcher_background) // Ganti dengan placeholder Anda
                    .error(R.drawable.ic_launcher_foreground) // Ganti dengan gambar error Anda
                    .into(ivImage);
            // Jika tidak menggunakan Glide:
            // ivImage.setImageURI(selectedImageUri);
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, dayOfMonth);
            String formatted = new SimpleDateFormat("EEEE, dd MMMM yyyy", new Locale("id", "ID")).format(selected.getTime());
            etDate.setText(formatted);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePickerStart() {
        Calendar calendar = Calendar.getInstance();
        // Ambil waktu mulai yang sudah ada jika ada, untuk default picker
        int initialHour = calendar.get(Calendar.HOUR_OF_DAY);
        int initialMinute = calendar.get(Calendar.MINUTE);

        if (eventToEdit != null && eventToEdit.getStartTime() != null && !eventToEdit.getStartTime().isEmpty()) {
            try {
                String[] timeParts = eventToEdit.getStartTime().split(":");
                initialHour = Integer.parseInt(timeParts[0]);
                initialMinute = Integer.parseInt(timeParts[1]);
            } catch (Exception e) {
                Log.w(TAG, "Gagal parse waktu mulai yang ada: " + eventToEdit.getStartTime());
            }
        }


        new TimePickerDialog(this, (view, hour, minute) -> {
            startHour = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);

            // Setelah memilih waktu mulai, langsung tampilkan dialog untuk waktu selesai
            // Ambil waktu selesai yang sudah ada jika ada, untuk default picker waktu selesai
            int initialEndHour = hour; // Default ke waktu mulai jika tidak ada waktu selesai sebelumnya
            int initialEndMinute = minute;

            if (eventToEdit != null && eventToEdit.getEndTime() != null && !eventToEdit.getEndTime().isEmpty()) {
                try {
                    String[] timeParts = eventToEdit.getEndTime().split(":");
                    initialEndHour = Integer.parseInt(timeParts[0]);
                    initialEndMinute = Integer.parseInt(timeParts[1]);
                } catch (Exception e) {
                    Log.w(TAG, "Gagal parse waktu selesai yang ada: " + eventToEdit.getEndTime());
                }
            }


            new TimePickerDialog(this, (view2, endHourOfDay, endMinute) -> {
                endHour = String.format(Locale.getDefault(), "%02d:%02d", endHourOfDay, endMinute);
                String fullTime = startHour + " - " + endHour + " WIB";
                etTime.setText(fullTime);
            }, initialEndHour, initialEndMinute, true).show(); // true untuk format 24 jam

        }, initialHour, initialMinute, true).show(); // true untuk format 24 jam
    }
}