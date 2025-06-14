package com.example.masjidapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.io.OutputStream;
import java.io.IOException;
import java.util.Date;

public class EventDetailActivity extends AppCompatActivity {

    private ImageView eventDetailImage;
    private TextView eventDetailTitle, eventDetailLocation, eventDetailDate,
            eventDetailTime, eventDetailType, eventDetailDescription;
    private ExtendedFloatingActionButton btnDownloadEvent; // ✅ Revisi

    private Bitmap bitmapToSave;

    private final ActivityResultLauncher<Intent> createFileLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null && bitmapToSave != null) {
                        saveBitmapToUri(uri);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        // Inisialisasi View
        eventDetailImage = findViewById(R.id.eventDetailImage);
        eventDetailTitle = findViewById(R.id.eventDetailTitle);
        eventDetailLocation = findViewById(R.id.eventDetailLocation);
        eventDetailDate = findViewById(R.id.eventDetailDate);
        eventDetailTime = findViewById(R.id.eventDetailTime);
        eventDetailType = findViewById(R.id.eventDetailType);
        eventDetailDescription = findViewById(R.id.eventDetailDescription);
        btnDownloadEvent = findViewById(R.id.btnDownloadEvent); // ✅ Tidak perlu casting ke ImageButton

        // Ambil data dari Intent
        Intent intent = getIntent();
        EventModel event = (EventModel) intent.getSerializableExtra("EVENT_DETAIL");

        if (event != null) {
            eventDetailTitle.setText(event.getTitle());
            eventDetailLocation.setText(event.getLocation());
            eventDetailDate.setText( event.getDate());

            String time = event.getStartTime() + " - " + event.getEndTime() + " WIB";
            eventDetailTime.setText(time);
            eventDetailType.setText(event.getType());
            eventDetailDescription.setText(event.getDescription());

            // Tampilkan gambar dari URL (Cloudinary)
            if (event.getImageUri() != null && !event.getImageUri().isEmpty()) {
                Uri imageUri = Uri.parse(event.getImageUri());
                Log.d("EVENT_DEBUG", "Image URI: " + event.getImageUri());

                Glide.with(this)
                        .load(imageUri)
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.default_event_image)
                                .error(R.drawable.default_event_image)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .centerCrop())
                        .into(eventDetailImage);
            } else {
                eventDetailImage.setImageResource(R.drawable.default_event_image);
            }
        }

        // Ambil screenshot dan minta lokasi simpan
        View rootView = getWindow().getDecorView().getRootView();
        btnDownloadEvent.setOnClickListener(v -> {
            bitmapToSave = takeScreenshot(rootView);
            if (bitmapToSave != null) {
                launchCreateFileIntent();
            } else {
                Toast.makeText(this, "Gagal mengambil screenshot", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Bitmap takeScreenshot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private void launchCreateFileIntent() {
        String fileName = "event_" + new Date().getTime() + ".png";
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        createFileLauncher.launch(intent);
    }

    private void saveBitmapToUri(Uri uri) {
        try (OutputStream out = getContentResolver().openOutputStream(uri)) {
            bitmapToSave.compress(Bitmap.CompressFormat.PNG, 100, out);
            Toast.makeText(this, "Gambar berhasil disimpan!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal menyimpan gambar", Toast.LENGTH_SHORT).show();
        }
    }
}
