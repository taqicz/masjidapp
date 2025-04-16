package com.example.masjidapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddEventActivity extends AppCompatActivity {

    EditText etTitle, etLocation, etDate, etTime;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        etTitle = findViewById(R.id.etEventTitle);
        etLocation = findViewById(R.id.etEventLocation);
        etDate = findViewById(R.id.etEventDate);
        etTime = findViewById(R.id.etEventTime);
        btnSave = findViewById(R.id.btnSaveEvent);

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String location = etLocation.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String time = etTime.getText().toString().trim();

            if (title.isEmpty() || location.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            } else {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("title", title);
                resultIntent.putExtra("location", location);
                resultIntent.putExtra("date", date);
                resultIntent.putExtra("time", time);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}
