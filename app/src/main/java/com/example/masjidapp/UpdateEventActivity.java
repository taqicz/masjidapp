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

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class UpdateEventActivity extends AppCompatActivity {

    private TextInputEditText etEventTitle, etEventLocation, etEventType, etEventDescription;
    private TextInputEditText etEventDate, etEventStartTime;
    private ImageView ivEventImage;
    private MaterialButton btnSaveEvent;

    private EventModel eventToEdit;
    private int eventPosition;
    private static final int IMAGE_PICK_CODE = 1001;
    private Uri selectedImageUri;
    private String startHour = "", endHour = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_event);

        etEventTitle = findViewById(R.id.etEventTitle);
        etEventLocation = findViewById(R.id.etEventLocation);
        etEventType = findViewById(R.id.etEventType);
        etEventDescription = findViewById(R.id.etEventDescription);
        etEventDate = findViewById(R.id.etEventDate);
        etEventStartTime = findViewById(R.id.etEventStartTime);
        ivEventImage = findViewById(R.id.ivEventImage);
        btnSaveEvent = findViewById(R.id.btnSaveEvent);

        // Ambil data dari intent
        eventToEdit = (EventModel) getIntent().getSerializableExtra("EVENT");
        eventPosition = getIntent().getIntExtra("EVENT_POSITION", -1);

        if (eventToEdit != null) {
            etEventTitle.setText(eventToEdit.getTitle());
            etEventLocation.setText(eventToEdit.getLocation());
            etEventType.setText(eventToEdit.getType());
            etEventDescription.setText(eventToEdit.getDescription());
            etEventDate.setText(eventToEdit.getDate());
            etEventStartTime.setText(eventToEdit.getStartTime());

            if (eventToEdit.getImageUri() != null) {
                selectedImageUri = Uri.parse(eventToEdit.getImageUri());
                ivEventImage.setImageURI(selectedImageUri);
            }
        }

        ivEventImage.setOnClickListener(v -> pickImage());
        etEventDate.setOnClickListener(v -> showDatePicker());
        etEventStartTime.setOnClickListener(v -> showTimePickerStart());

        btnSaveEvent.setOnClickListener(v -> saveEvent());
    }

    private void saveEvent() {
        String title = etEventTitle.getText().toString();
        String location = etEventLocation.getText().toString();
        String type = etEventType.getText().toString();
        String description = etEventDescription.getText().toString();
        String date = etEventDate.getText().toString();
        String startTime = etEventStartTime.getText().toString();

        if (title.isEmpty() || location.isEmpty() || type.isEmpty() || description.isEmpty() ||
                date.isEmpty() || startTime.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        eventToEdit.setTitle(title);
        eventToEdit.setLocation(location);
        eventToEdit.setType(type);
        eventToEdit.setDescription(description);
        eventToEdit.setDate(date);
        eventToEdit.setStartTime(startHour);
        eventToEdit.setEndTime(endHour);

        if (selectedImageUri != null) {
            eventToEdit.setImageUri(selectedImageUri.toString());
        }

        Toast.makeText(this, "Event berhasil diperbarui", Toast.LENGTH_SHORT).show();

        Intent resultIntent = new Intent();
        resultIntent.putExtra("UPDATED_EVENT", eventToEdit);
        resultIntent.putExtra("EVENT_POSITION", eventPosition);
        setResult(RESULT_OK, resultIntent);
        finish();
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
            etEventDate.setText(formattedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePickerStart() {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            startHour = String.format("%02d:%02d", hourOfDay, minute);

            new TimePickerDialog(this, (view2, endHourOfDay, endMinute) -> {
                endHour = String.format("%02d:%02d", endHourOfDay, endMinute);
                String fullTime = startHour + " - " + endHour + " WIB";
                etEventStartTime.setText(fullTime);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();

        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }
}
