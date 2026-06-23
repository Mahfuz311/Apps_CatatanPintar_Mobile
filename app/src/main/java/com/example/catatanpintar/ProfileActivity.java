package com.example.catatanpintar;

import android.content.Intent; // Tambahan penting untuk pindah halaman
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    ImageButton btnBackProfile;
    View btnSignOut;
    TextView tvProfileName, tvProfileEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 1. Menghubungkan ID komponen dari XML ke Java
        btnBackProfile = findViewById(R.id.btn_back_profile);
        // Logika Pindah ke Halaman Help & Support
        View btnHelpSupport = findViewById(R.id.btn_help_support);
        btnHelpSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Berpindah ke HelpSupportActivity menggunakan Intent
                android.content.Intent intent = new android.content.Intent(ProfileActivity.this, HelpSupportActivity.class);
                startActivity(intent);
            }
        });
        btnSignOut = findViewById(R.id.btn_sign_out);
        tvProfileName = findViewById(R.id.tv_profile_name);
        tvProfileEmail = findViewById(R.id.tv_profile_email);

        // Tambahan ID untuk Ikon Inisial
        TextView tvProfileInitial = findViewById(R.id.tv_profile_initial);

        // 2. Mengambil data dari SharedPreferences (Sesi Login)
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);

        String registeredName = sharedPreferences.getString("NAMA_USER", "Mahfuz Fauzi");
        String registeredEmail = sharedPreferences.getString("EMAIL_USER", "mahfuzfauzia678@gmail.com");

        // 3. Set teks ke layar sesuai data yang didapat
        tvProfileName.setText(registeredName);
        tvProfileEmail.setText(registeredEmail);

        // --- LOGIKA BARU: MENGUBAH IKON SESUAI HURUF PERTAMA NAMA ---
        if (!registeredName.isEmpty()) {
            // Ambil huruf pertama (index 0 sampai 1) dan ubah jadi huruf besar
            String initialLetter = registeredName.substring(0, 1).toUpperCase();
            tvProfileInitial.setText(initialLetter);
        }
        // -----------------------------------------------------------

        // 4. Logika Tombol Kembali
        btnBackProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 5. Logika Tombol Sign Out
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sessionPrefs = getSharedPreferences("UserSession", MODE_PRIVATE);
                SharedPreferences.Editor editor = sessionPrefs.edit();
                editor.clear();
                editor.apply();

                Toast.makeText(ProfileActivity.this, "Berhasil Sign Out!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}