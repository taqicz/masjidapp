package com.example.masjidapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class EventDetailActivity extends AppCompatActivity {

    private ImageView eventDetailImage;
    private TextView eventDetailTitle, eventDetailLocation, eventDetailDate, eventDetailTime, eventDetailType, eventDetailDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        // Inisialisasi Views
        eventDetailImage = findViewById(R.id.eventDetailImage);
        eventDetailTitle = findViewById(R.id.eventDetailTitle);
        eventDetailLocation = findViewById(R.id.eventDetailLocation);
        eventDetailDate = findViewById(R.id.eventDetailDate);
        eventDetailTime = findViewById(R.id.eventDetailTime);
        eventDetailType = findViewById(R.id.eventDetailType);
        eventDetailDescription = findViewById(R.id.eventDetailDescription);

        // Ambil data EventModel dari Intent
        Intent intent = getIntent();
        EventModel event = (EventModel) intent.getSerializableExtra("EVENT_DETAIL");

        // Tampilkan data ke dalam views
        if (event != null) {
            eventDetailTitle.setText(event.getTitle());
            eventDetailLocation.setText("Location: " + event.getLocation());
            eventDetailDate.setText("Date: " + event.getDate());
            String combinedTime = event.getStartTime() + " - " + event.getEndTime() + " WIB";
            eventDetailTime.setText("Time: " + combinedTime);
            eventDetailType.setText("Type: " + event.getType());
            eventDetailDescription.setText(event.getDescription());

            // Load event image with Glide
            if (event.getImageUri() != null && !event.getImageUri().isEmpty()) {
                Glide.with(this)
                        .load(event.getImageUri())
                        .into(eventDetailImage);
            } else {
                eventDetailImage.setImageResource(R.drawable.default_event_image); // Default image if no image URL
            }
        }
    }
}
