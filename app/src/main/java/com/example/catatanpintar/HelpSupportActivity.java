package com.example.catatanpintar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class HelpSupportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_support);

        ImageButton btnBack = findViewById(R.id.btn_back_help);
        TextView tvHelpEmail = findViewById(R.id.tv_help_email);
        TextView tvHelpFooter = findViewById(R.id.tv_help_footer);

        // Menjadikan email kamu sebagai kontak support permanen
        String developerEmail = "mahfuzfauzia678@gmail.com";

        // Memasang teks ke layar
        tvHelpEmail.setText(developerEmail);
        tvHelpFooter.setText("Catatan Pintar v1.0.0 - " + developerEmail);

        // Fungsi Tombol Kembali
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Kembali ke Profil
            }
        });
    }
}