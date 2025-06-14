package com.example.masjidapp;

import android.app.Application;
import com.cloudinary.android.MediaManager;
import java.util.HashMap;
import java.util.Map;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Konfigurasi untuk Cloudinary diletakkan di sini
        // Kode ini hanya akan berjalan SATU KALI saat aplikasi dibuka
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dxu91lnwm");
        config.put("api_key", "796833387833151");
        config.put("api_secret", "VEPcdNFJUflVLTqouk1H6U5nhdw");

        MediaManager.init(this, config);
    }
}
