package com.example.masjidapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddEventActivity extends AppCompatActivity {

    EditText etTitle, etLocation, etDate, etStartTime, etType, etDescription;
    Button btnSave, btnUploadImage;
    ImageView ivEventImage;
    private static final int IMAGE_PICK_CODE = 1001;
    Uri selectedImageUri;

    String startHour = "", endHour = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        etTitle = findViewById(R.id.etEventTitle);
        etLocation = findViewById(R.id.etEventLocation);
        etDate = findViewById(R.id.etEventDate);
        etStartTime = findViewById(R.id.etEventStartTime);
        etType = findViewById(R.id.etEventType);
        etDescription = findViewById(R.id.etEventDescription);

        btnSave = findViewById(R.id.btnSaveEvent);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        ivEventImage = findViewById(R.id.ivEventImage);

        btnUploadImage.setOnClickListener(v -> pickImage());
        etDate.setOnClickListener(v -> showDatePicker());
        etStartTime.setOnClickListener(v -> showTimePickerStart());

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String location = etLocation.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String timeRange = etStartTime.getText().toString().trim();
            String type = etType.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            if (title.isEmpty() || location.isEmpty() || date.isEmpty() || timeRange.isEmpty() || type.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            } else {
                EventModel newEvent = new EventModel(
                        title,
                        location,
                        date,
                        startHour,
                        endHour,
                        type,
                        selectedImageUri != null ? selectedImageUri.toString() : "",
                        description
                );

                // Kirimkan ke activity pemanggil
                Intent resultIntent = new Intent();
                resultIntent.putExtra("newEvent", newEvent);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            ivEventImage.setImageURI(selectedImageUri);
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy", new Locale("id", "ID"));
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth);
            String formattedDate = sdf.format(selectedDate.getTime());
            etDate.setText(formattedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePickerStart() {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(this, (view, startHourOfDay, startMinute) -> {
            startHour = String.format("%02d:%02d", startHourOfDay, startMinute);

            new TimePickerDialog(this, (view2, endHourOfDay, endMinute) -> {
                endHour = String.format("%02d:%02d", endHourOfDay, endMinute);
                String fullTime = startHour + " - " + endHour + " WIB";
                etStartTime.setText(fullTime);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();

        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }
}
