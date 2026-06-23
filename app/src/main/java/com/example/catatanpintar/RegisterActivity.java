package com.example.catatanpintar;

import android.os.Build; // Tambahan untuk mengecek versi Android
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText etFullName, etEmail, etPassword, etConfirmPassword;
    Button btnRegister;
    TextView tvSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // --- MATIKAN PRIVASI GOOGLE AUTOFILL (Untuk Kebutuhan Rekam Layar) ---
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
        }
        // ---------------------------------------------------------------------

        // Menghubungkan ID
        etFullName = findViewById(R.id.et_fullname);
        etEmail = findViewById(R.id.et_email_reg);
        etPassword = findViewById(R.id.et_password_reg);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        tvSignIn = findViewById(R.id.tv_signin);

        // 1. Logika kembali ke halaman Login saat "Sign In" diklik
        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // finish() berfungsi menutup halaman saat ini dan otomatis kembali ke layar sebelumnya
                finish();
            }
        });

        // 2. Logika validasi saat tombol "Create Account" diklik
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = etFullName.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();

                // Cek jika ada kolom yang kosong
                if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show();
                }
                // Cek jika password dan konfirmasi password tidak sama
                else if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Password tidak cocok!", Toast.LENGTH_SHORT).show();
                }
                // Jika semuanya aman (Penyimpanan Data)
                else {
                    // Menyimpan data menggunakan SharedPreferences
                    android.content.SharedPreferences sharedPreferences = getSharedPreferences("AkunPintar", MODE_PRIVATE);
                    android.content.SharedPreferences.Editor editor = sharedPreferences.edit();

                    // --- BAGIAN YANG DIPERBAIKI ---
                    // Mengubah inputNama menjadi fullName sesuai deklarasi di atas
                    editor.putString("namaTerdaftar", fullName);
                    editor.putString("emailTerdaftar", email);
                    editor.putString("passwordTerdaftar", password);
                    editor.apply(); // Simpan perubahan

                    Toast.makeText(RegisterActivity.this, "Pendaftaran Berhasil! Silakan Login.", Toast.LENGTH_SHORT).show();

                    // Otomatis kembali ke halaman Login setelah sukses mendaftar
                    finish();
                }
            }
        });
    }
}