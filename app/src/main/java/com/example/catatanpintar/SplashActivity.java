package com.example.catatanpintar;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class SplashActivity extends AppCompatActivity {

    ImageView ivIcon;
    TextView tvTitle;
    TextView tvDots;

    Handler dotHandler = new Handler();
    int dotIndex = 0;

    // Variabel untuk menyimpan animasi agar bisa dimatikan nanti
    ObjectAnimator floatingAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ivIcon = findViewById(R.id.iv_icon);
        tvTitle = findViewById(R.id.tv_title);
        tvDots = findViewById(R.id.tv_dots);

        // 1. Jalankan Animasi Mengambang (Hanya untuk Logo Awal)
        startFloatingAnimation();

        // 2. Jalankan Animasi Titik-Titik
        startDotsAnimation();

        // 3. Urutan Transisi Halaman (Timeline)
        Handler timelineHandler = new Handler();

        // Ke "Deteksi Lokasi" pada detik ke-1.5
        timelineHandler.postDelayed(() -> changeContentWithFade(R.drawable.ic_pin, "Deteksi Lokasi", true), 1500);

        // Ke "Bendera & Nama Negara" pada detik ke-3.5
        timelineHandler.postDelayed(() -> {
            String countryCode = Locale.getDefault().getCountry();
            if (countryCode.equals("ID")) {
                changeContentWithFade(R.drawable.bendera_id, "INDONESIA", true);
            } else {
                changeContentWithFade(R.drawable.bendera_us, "UNITED STATES", true);
            }
        }, 3500);

        // Ke "Sapaan (HALO/HELLO)" pada detik ke-5.0
        timelineHandler.postDelayed(() -> {
            String countryCode = Locale.getDefault().getCountry();
            if (countryCode.equals("ID")) {
                changeTextWithFade("HALO");
            } else {
                changeTextWithFade("HELLO");
            }
        }, 5000);

        // Pindah ke LoginActivity
        timelineHandler.postDelayed(() -> {
            dotHandler.removeCallbacksAndMessages(null);
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class); // <-- Ubah di sini
            startActivity(intent);
            finish();
        }, 6500);
    }

    // --- FUNGSI ANIMASI ---

    private void startFloatingAnimation() {
        // Simpan animasinya ke dalam variabel floatingAnimator
        floatingAnimator = ObjectAnimator.ofFloat(ivIcon, "translationY", 0f, -15f, 0f);
        floatingAnimator.setDuration(1500);
        floatingAnimator.setRepeatCount(ValueAnimator.INFINITE);
        floatingAnimator.start();
    }

    private void startDotsAnimation() {
        Runnable dotRunnable = new Runnable() {
            @Override
            public void run() {
                SpannableString dots = new SpannableString("• • •");
                int colorActive = Color.parseColor("#8A2BE2");
                int colorInactive = Color.parseColor("#66FFFFFF");

                dots.setSpan(new ForegroundColorSpan(dotIndex == 0 ? colorActive : colorInactive), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                dots.setSpan(new ForegroundColorSpan(dotIndex == 1 ? colorActive : colorInactive), 2, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                dots.setSpan(new ForegroundColorSpan(dotIndex == 2 ? colorActive : colorInactive), 4, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                tvDots.setText(dots);
                dotIndex = (dotIndex + 1) % 3;

                dotHandler.postDelayed(this, 400);
            }
        };
        dotHandler.post(dotRunnable);
    }

    private void changeContentWithFade(int imageRes, String text, boolean isSmallSize) {
        ivIcon.animate().alpha(0f).setDuration(250).withEndAction(() -> {
            ivIcon.setImageResource(imageRes);
            tvTitle.setText(text);

            // LOGIKA PENGECILAN GAMBAR & MENGHENTIKAN ANIMASI
            ViewGroup.LayoutParams params = ivIcon.getLayoutParams();
            float density = getResources().getDisplayMetrics().density;

            if (isSmallSize) {
                // Hentikan animasi mengambang saat berubah jadi gambar kecil (pin/bendera)
                if (floatingAnimator != null && floatingAnimator.isRunning()) {
                    floatingAnimator.cancel();
                    ivIcon.setTranslationY(0f); // Kembalikan posisi Y ke normal agar tidak tersangkut di atas
                }

                params.width = (int) (100 * density);
                params.height = (int) (100 * density);

                // Kembalikan margin top ke normal (16dp) agar teks tidak terlalu nempel dengan pin/bendera
                ViewGroup.MarginLayoutParams textParams = (ViewGroup.MarginLayoutParams) tvTitle.getLayoutParams();
                textParams.topMargin = (int) (16 * density);
                tvTitle.setLayoutParams(textParams);
            }
            ivIcon.setLayoutParams(params);

            ivIcon.animate().alpha(1f).setDuration(250).start();
            tvTitle.animate().alpha(1f).setDuration(250).start();
        }).start();
    }

    private void changeTextWithFade(String text) {
        tvTitle.animate().alpha(0f).setDuration(250).withEndAction(() -> {
            tvTitle.setText(text);
            tvTitle.animate().alpha(1f).setDuration(250).start();
        }).start();
    }
}