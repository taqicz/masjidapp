package com.example.masjidapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;

public class UpdateEventActivity extends AppCompatActivity {

    private TextInputEditText etEventTitle, etEventLocation, etEventType, etEventDescription;
    private TextInputEditText etEventDate, etEventStartTime;
    private ImageView ivEventImage;
    private MaterialButton btnSaveEvent;
    private EventModel eventToEdit;
    private int eventPosition;

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

        eventToEdit = (EventModel) getIntent().getSerializableExtra("EVENT");
        eventPosition = getIntent().getIntExtra("EVENT_POSITION", -1);

        if (eventToEdit != null) {
            etEventTitle.setText(eventToEdit.getTitle());
            etEventLocation.setText(eventToEdit.getLocation());
            etEventType.setText(eventToEdit.getType());
            etEventDescription.setText(eventToEdit.getDescription());
            etEventDate.setText(eventToEdit.getDate());
            etEventStartTime.setText(eventToEdit.getStartTime());
        }

        btnSaveEvent.setOnClickListener(v -> saveEvent());
    }

    private void saveEvent() {
        String title = etEventTitle.getText().toString();
        String location = etEventLocation.getText().toString();
        String type = etEventType.getText().toString();
        String description = etEventDescription.getText().toString();
        String date = etEventDate.getText().toString();
        String startTime = etEventStartTime.getText().toString();

        eventToEdit.setTitle(title);
        eventToEdit.setLocation(location);
        eventToEdit.setType(type);
        eventToEdit.setDescription(description);
        eventToEdit.setDate(date);
        eventToEdit.setStartTime(startTime);

        Toast.makeText(this, "Event berhasil diperbarui", Toast.LENGTH_SHORT).show();

        Intent resultIntent = new Intent();
        resultIntent.putExtra("UPDATED_EVENT", eventToEdit);
        resultIntent.putExtra("EVENT_POSITION", eventPosition);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
