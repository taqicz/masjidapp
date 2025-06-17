/**
 * Kelas EventDetailActivity digunakan untuk menampilkan detail lengkap dari sebuah event.
 *
 * Fitur yang disediakan:
 * - Menampilkan data event (judul, lokasi, tanggal, jam, tipe, deskripsi, dan gambar).
 * - Menampilkan gambar dari URL Cloudinary menggunakan Glide.
 * - Fitur untuk mengambil screenshot dari tampilan detail dan menyimpannya ke penyimpanan lokal.
 */

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

    // Komponen UI untuk detail event
    private ImageView eventDetailImage;
    private TextView eventDetailTitle, eventDetailLocation, eventDetailDate,
            eventDetailTime, eventDetailType, eventDetailDescription;
    private ExtendedFloatingActionButton btnDownloadEvent;

    // Bitmap hasil screenshot yang akan disimpan
    private Bitmap bitmapToSave;

    // Launcher untuk membuka dialog simpan file (ACTION_CREATE_DOCUMENT)
    private final ActivityResultLauncher<Intent> createFileLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null && bitmapToSave != null) {
                        saveBitmapToUri(uri); // Simpan bitmap ke URI yang dipilih
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        // Inisialisasi semua komponen tampilan
        eventDetailImage = findViewById(R.id.eventDetailImage);
        eventDetailTitle = findViewById(R.id.eventDetailTitle);
        eventDetailLocation = findViewById(R.id.eventDetailLocation);
        eventDetailDate = findViewById(R.id.eventDetailDate);
        eventDetailTime = findViewById(R.id.eventDetailTime);
        eventDetailType = findViewById(R.id.eventDetailType);
        eventDetailDescription = findViewById(R.id.eventDetailDescription);
        btnDownloadEvent = findViewById(R.id.btnDownloadEvent);

        // Ambil data event yang dikirim dari intent
        Intent intent = getIntent();
        EventModel event = (EventModel) intent.getSerializableExtra("EVENT_DETAIL");

        if (event != null) {
            // Tampilkan data event ke tampilan
            eventDetailTitle.setText(event.getTitle());
            eventDetailLocation.setText(event.getLocation());
            eventDetailDate.setText(event.getDate());

            String time = event.getStartTime() + " - " + event.getEndTime() + " WIB";
            eventDetailTime.setText(time);
            eventDetailType.setText(event.getType());
            eventDetailDescription.setText(event.getDescription());

            // Tampilkan gambar event dari URL menggunakan Glide
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

        // Ambil root view dari layar untuk screenshot
        View rootView = getWindow().getDecorView().getRootView();
        btnDownloadEvent.setOnClickListener(v -> {
            bitmapToSave = takeScreenshot(rootView); // Ambil screenshot layar
            if (bitmapToSave != null) {
                launchCreateFileIntent(); // Tampilkan intent simpan file
            } else {
                Toast.makeText(this, "Gagal mengambil screenshot", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Mengambil screenshot dari suatu view dan mengubahnya menjadi Bitmap.
     */
    private Bitmap takeScreenshot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas); // Gambar seluruh tampilan ke canvas
        return bitmap;
    }

    /**
     * Meluncurkan intent untuk membuat file PNG untuk menyimpan screenshot.
     */
    private void launchCreateFileIntent() {
        String fileName = "event_" + new Date().getTime() + ".png";
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_TITLE, fileName); // Nama default file
        createFileLauncher.launch(intent);
    }

    /**
     * Menyimpan bitmap ke lokasi file yang dipilih oleh pengguna.
     */
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
