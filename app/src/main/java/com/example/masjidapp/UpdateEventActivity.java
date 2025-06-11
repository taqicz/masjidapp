package com.example.masjidapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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
    private MaterialButton btnSave, btnUploadImage;
    private Uri selectedImageUri;
    private String currentImageUriString;

    private String startHour = "", endHour = "";

    private EventModel eventToEdit;
    private DatabaseReference eventRef;
    private static final int IMAGE_PICK_CODE = 1001;
    private static final String TAG = "UpdateEventActivity";

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
        btnUploadImage = findViewById(R.id.btnUploadImage); // Tambahkan ini

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
                etTime.setText(eventToEdit.getStartTime() + " WIB");
            }

            currentImageUriString = eventToEdit.getImageUri();
            if (currentImageUriString != null && !currentImageUriString.isEmpty()) {
                selectedImageUri = Uri.parse(currentImageUriString);
                try {
                    Glide.with(this)
                            .load(selectedImageUri)
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_launcher_foreground)
                            .into(ivImage);
                } catch (Exception e) {
                    Log.e(TAG, "Gagal memuat gambar sebelumnya", e);
                    ivImage.setImageResource(R.drawable.ic_launcher_background);
                }
            } else {
                ivImage.setImageResource(R.drawable.ic_launcher_background);
            }
        } else {
            Toast.makeText(this, "Event tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Listener
        ivImage.setOnClickListener(v -> pickImage());
        btnUploadImage.setOnClickListener(v -> pickImage()); // Pemicu galeri dari tombol
        etDate.setOnClickListener(v -> showDatePicker());
        etTime.setOnClickListener(v -> showTimePickerStart());
        btnSave.setOnClickListener(v -> updateEvent());
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            // IZIN PERSISTEN untuk akses gambar lintas activity
            final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                getContentResolver().takePersistableUriPermission(selectedImageUri, takeFlags);
            } catch (SecurityException e) {
                Log.e(TAG, "Gagal mengambil izin URI: " + e.getMessage());
            }

            Glide.with(this)
                    .load(selectedImageUri)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(ivImage);
        }
    }


    private void updateEvent() {
        String title = etTitle.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String type = etType.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String date = etDate.getText().toString().trim();

        if (title.isEmpty() || location.isEmpty() || type.isEmpty() || description.isEmpty() || date.isEmpty() || (startHour.isEmpty() && eventToEdit.getStartTime() == null)) {
            Toast.makeText(this, "Semua field harus diisi, termasuk waktu.", Toast.LENGTH_SHORT).show();
            return;
        }

        eventToEdit.setTitle(title);
        eventToEdit.setLocation(location);
        eventToEdit.setType(type);
        eventToEdit.setDescription(description);
        eventToEdit.setDate(date);

        if (!startHour.isEmpty()) {
            eventToEdit.setStartTime(startHour);
        }
        if (!endHour.isEmpty()) {
            eventToEdit.setEndTime(endHour);
        }

        if (selectedImageUri != null && (currentImageUriString == null || !selectedImageUri.toString().equals(currentImageUriString))) {
            eventToEdit.setImageUri(selectedImageUri.toString());
        }

        eventRef.child(eventToEdit.getId()).setValue(eventToEdit)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(UpdateEventActivity.this, "Event berhasil diperbarui", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(UpdateEventActivity.this, "Gagal memperbarui event: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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
        int initialHour = calendar.get(Calendar.HOUR_OF_DAY);
        int initialMinute = calendar.get(Calendar.MINUTE);

        if (eventToEdit != null && eventToEdit.getStartTime() != null && !eventToEdit.getStartTime().isEmpty()) {
            try {
                String[] timeParts = eventToEdit.getStartTime().split(":");
                initialHour = Integer.parseInt(timeParts[0]);
                initialMinute = Integer.parseInt(timeParts[1]);
            } catch (Exception e) {
                Log.w(TAG, "Gagal parse waktu mulai: " + eventToEdit.getStartTime());
            }
        }

        new TimePickerDialog(this, (view, hour, minute) -> {
            startHour = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);

            int initialEndHour = hour;
            int initialEndMinute = minute;

            if (eventToEdit != null && eventToEdit.getEndTime() != null && !eventToEdit.getEndTime().isEmpty()) {
                try {
                    String[] timeParts = eventToEdit.getEndTime().split(":");
                    initialEndHour = Integer.parseInt(timeParts[0]);
                    initialEndMinute = Integer.parseInt(timeParts[1]);
                } catch (Exception e) {
                    Log.w(TAG, "Gagal parse waktu selesai: " + eventToEdit.getEndTime());
                }
            }

            new TimePickerDialog(this, (view2, endHourOfDay, endMinute) -> {
                endHour = String.format(Locale.getDefault(), "%02d:%02d", endHourOfDay, endMinute);
                etTime.setText(startHour + " - " + endHour + " WIB");
            }, initialEndHour, initialEndMinute, true).show();

        }, initialHour, initialMinute, true).show();
    }
}
