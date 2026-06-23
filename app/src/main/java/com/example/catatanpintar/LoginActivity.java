package com.example.catatanpintar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build; // Tambahan untuk mengecek versi Android
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    TextView tvSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // --- MATIKAN PRIVASI GOOGLE AUTOFILL (Untuk Kebutuhan Rekam Layar) ---
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
        }
        // ---------------------------------------------------------------------

        // Menghubungkan ID
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvSignUp = findViewById(R.id.tv_signup);

        // Logika pindah ke halaman Register
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // Logika Validasi Login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailInput = etEmail.getText().toString();
                String passwordInput = etPassword.getText().toString();

                if (emailInput.isEmpty() || passwordInput.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Email dan Password tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences sharedPreferences = getSharedPreferences("AkunPintar", MODE_PRIVATE);

                    // Menarik data dari Register
                    String emailTersimpan = sharedPreferences.getString("emailTerdaftar", "");
                    String passwordTersimpan = sharedPreferences.getString("passwordTerdaftar", "");
                    String namaTersimpan = sharedPreferences.getString("namaTerdaftar", "Guest");

                    if (emailTersimpan.equals("")) {
                        Toast.makeText(LoginActivity.this, "Belum ada akun terdaftar. Silakan Sign Up.", Toast.LENGTH_SHORT).show();
                    }
                    else if (!emailInput.equals(emailTersimpan)) {
                        Toast.makeText(LoginActivity.this, "Email tidak terdaftar!", Toast.LENGTH_SHORT).show();
                    }
                    else if (!passwordInput.equals(passwordTersimpan)) {
                        Toast.makeText(LoginActivity.this, "Password salah!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "Login Berhasil!", Toast.LENGTH_SHORT).show();

                        // --- JEMBATAN KE PROFIL (SIMPAN SESI LOGIN) ---
                        SharedPreferences sessionPrefs = getSharedPreferences("UserSession", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sessionPrefs.edit();
                        editor.putString("NAMA_USER", namaTersimpan);
                        editor.putString("EMAIL_USER", emailTersimpan);
                        editor.apply();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
    }
}