package com.example.arena; // KENDİ PAKET ADINI YAZ!

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    private LinearLayout navHome, navLeaderboard, navProfile, navSettings;
    private TextView tvNavHome, tvNavLeaderboard, tvNavProfile, tvNavSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        navHome = findViewById(R.id.navHome);
        navLeaderboard = findViewById(R.id.navLeaderboard);
        navProfile = findViewById(R.id.navProfile);
        navSettings = findViewById(R.id.navSettings);

        tvNavHome = findViewById(R.id.tvNavHome);
        tvNavLeaderboard = findViewById(R.id.tvNavLeaderboard);
        tvNavProfile = findViewById(R.id.tvNavProfile);
        tvNavSettings = findViewById(R.id.tvNavSettings);

        // İlk açılışta HomeFragment'i yükle
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        navHome.setOnClickListener(v -> {
            loadFragment(new HomeFragment());
            updateNavUI(tvNavHome);
        });

        navLeaderboard.setOnClickListener(v -> {
            loadFragment(new LeaderboardFragment());
            updateNavUI(tvNavLeaderboard);
        });

        navProfile.setOnClickListener(v -> {
            loadFragment(new ProfileFragment());
            updateNavUI(tvNavProfile);
        });

        navSettings.setOnClickListener(v -> {
            loadFragment(new SettingsFragment());
            updateNavUI(tvNavSettings);
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    private void updateNavUI(TextView selectedText) {
        // Hepsini gri ve normal yap
        tvNavHome.setTextColor(Color.parseColor("#64748B")); tvNavHome.setTypeface(null, Typeface.NORMAL);
        tvNavLeaderboard.setTextColor(Color.parseColor("#64748B")); tvNavLeaderboard.setTypeface(null, Typeface.NORMAL);
        tvNavProfile.setTextColor(Color.parseColor("#64748B")); tvNavProfile.setTypeface(null, Typeface.NORMAL);
        tvNavSettings.setTextColor(Color.parseColor("#64748B")); tvNavSettings.setTypeface(null, Typeface.NORMAL);

        // Sadece seçileni turuncu ve kalın (bold) yap
        selectedText.setTextColor(Color.parseColor("#EA580C"));
        selectedText.setTypeface(null, Typeface.BOLD);
    }
}