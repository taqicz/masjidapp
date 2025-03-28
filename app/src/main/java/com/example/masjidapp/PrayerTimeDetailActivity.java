package com.example.masjidapp;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class PrayerTimeDetailActivity extends AppCompatActivity {
    private TextView waktuSholatSelanjutnya;
    private Handler handler = new Handler();
    private String[] prayerTimes = {"04:08", "04:18", "05:30", "11:38", "14:53", "17:39", "18:48"};
    private String nextPrayerTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_prayer_time_detail);

        waktuSholatSelanjutnya = findViewById(R.id.waktu_selanjutnya);
        nextPrayerTime = getNextPrayerTime();
        handler.post(updateCountdown);
    }

    private String getNextPrayerTime() {
        Calendar now = Calendar.getInstance();

        for (String time : prayerTimes) {
            Calendar prayerTime = Calendar.getInstance();
            String[] parts = time.split(":");
            prayerTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
            prayerTime.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
            prayerTime.set(Calendar.SECOND, 0);

            if (now.before(prayerTime)) {
                return time;
            }
        }

        // Jika sudah lewat semua waktu salat, gunakan waktu pertama dan tambahkan satu hari
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);
        String[] firstTimeParts = prayerTimes[0].split(":");
        tomorrow.set(Calendar.HOUR_OF_DAY, Integer.parseInt(firstTimeParts[0]));
        tomorrow.set(Calendar.MINUTE, Integer.parseInt(firstTimeParts[1]));
        tomorrow.set(Calendar.SECOND, 0);

        return prayerTimes[0];
    }

    private final Runnable updateCountdown = new Runnable() {
        @Override
        public void run() {
            Calendar now = Calendar.getInstance();
            Calendar targetTime = Calendar.getInstance();
            String[] parts = nextPrayerTime.split(":");

            targetTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
            targetTime.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
            targetTime.set(Calendar.SECOND, 0);

            long diff = targetTime.getTimeInMillis() - now.getTimeInMillis();

            if (diff > 0) {
                long hours = (diff / (1000 * 60 * 60)) % 24;
                long minutes = (diff / (1000 * 60)) % 60;
                long seconds = (diff / 1000) % 60;
                waktuSholatSelanjutnya.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds));

                handler.postDelayed(this, 1000);
            } else {
                nextPrayerTime = getNextPrayerTime();
                handler.post(this);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateCountdown);
    }
}
