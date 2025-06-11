package com.example.masjidapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

public class AddEventActivity extends AppCompatActivity {

    private TextInputEditText etTitle, etLocation, etType, etDescription, etDate, etTime;
    private ImageView ivImage;
    private MaterialButton btnSave, btnUploadImage;
    private Uri selectedImageUri;
    private String startHour = "", endHour = "";

    private static final int IMAGE_PICK_CODE = 1001;
    private DatabaseReference eventRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        etTitle = findViewById(R.id.etEventTitle);
        etLocation = findViewById(R.id.etEventLocation);
        etType = findViewById(R.id.etEventType);
        etDescription = findViewById(R.id.etEventDescription);
        etDate = findViewById(R.id.etEventDate);
        etTime = findViewById(R.id.etEventStartTime);
        ivImage = findViewById(R.id.ivEventImage);
        btnSave = findViewById(R.id.btnSaveEvent);
        btnUploadImage = findViewById(R.id.btnUploadImage); // Inisialisasi tombol upload

        eventRef = FirebaseDatabase.getInstance().getReference("events");

        // Aksi klik
        ivImage.setOnClickListener(v -> pickImage());
        btnUploadImage.setOnClickListener(v -> pickImage()); // Tombol juga bisa pilih gambar
        etDate.setOnClickListener(v -> showDatePicker());
        etTime.setOnClickListener(v -> showTimePickerStart());
        btnSave.setOnClickListener(v -> saveEvent());
    }

    private void saveEvent() {
        String id = eventRef.push().getKey();
        String title = etTitle.getText().toString();
        String location = etLocation.getText().toString();
        String type = etType.getText().toString();
        String description = etDescription.getText().toString();
        String date = etDate.getText().toString();
        String startTime = startHour;
        String endTime = endHour;
        String imageUri = selectedImageUri != null ? selectedImageUri.toString() : "";

        if (title.isEmpty() || location.isEmpty() || type.isEmpty() || description.isEmpty() ||
                date.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        EventModel event = new EventModel(id, title, location, date, startTime, endTime, type, imageUri, description);
        eventRef.child(id).setValue(event)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Event berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Gagal menambahkan event", Toast.LENGTH_SHORT).show());
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            // Simpan izin akses persisten
            final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION);
            getContentResolver().takePersistableUriPermission(selectedImageUri, takeFlags);

            Glide.with(this)
                    .load(selectedImageUri)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(ivImage);
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
        new TimePickerDialog(this, (view, hour, minute) -> {
            startHour = String.format("%02d:%02d", hour, minute);

            new TimePickerDialog(this, (view2, endHourOfDay, endMinute) -> {
                endHour = String.format("%02d:%02d", endHourOfDay, endMinute);
                String fullTime = startHour + " - " + endHour + " WIB";
                etTime.setText(fullTime);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();

        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }
}
