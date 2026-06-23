package com.example.catatanpintar;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences; // Tambahan untuk memori sesi
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView; // Tambahan untuk TextView
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageButton btnAddNote, btnScanOcr;
    TextView ivProfile; // DIUBAH: Dari View menjadi TextView agar teksnya bisa diganti
    EditText etSearch;
    RecyclerView rvNotes;
    LinearLayout emptyStateLayout;
    DatabaseHelper dbHelper;
    NoteAdapter noteAdapter;
    List<Note> allNotesList;

    // Persiapan Meminta Izin Kamera
    private final ActivityResultLauncher<String> requestCameraPermission =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) { openCamera(); }
                else { Toast.makeText(this, "Izin kamera ditolak!", Toast.LENGTH_SHORT).show(); }
            });

    // Persiapan Membuka Kamera & Menangkap Foto
    private final ActivityResultLauncher<Void> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), bitmap -> {
                if (bitmap != null) {
                    processImageWithMLKit(bitmap);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Menghubungkan ID
        btnAddNote = findViewById(R.id.btn_add_note);
        btnScanOcr = findViewById(R.id.btn_scan_ocr);
        ivProfile = findViewById(R.id.iv_profile_main);
        rvNotes = findViewById(R.id.rv_notes);
        emptyStateLayout = findViewById(R.id.empty_state_layout);
        etSearch = findViewById(R.id.et_search_main);

        // ==========================================
        // LOGIKA BARU: MENGUBAH IKON PROFIL SESUAI NAMA
        // ==========================================
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String registeredName = sharedPreferences.getString("NAMA_USER", "Guest");

        if (!registeredName.isEmpty()) {
            // Ambil huruf pertama dan jadikan huruf kapital
            String initialLetter = registeredName.substring(0, 1).toUpperCase();
            ivProfile.setText(initialLetter);
        }
        // ==========================================

        dbHelper = new DatabaseHelper(this);
        rvNotes.setLayoutManager(new LinearLayoutManager(this));

        // Logika Klik Foto Profil Untuk Pindah Halaman
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        // Tombol Tambah Catatan Manual
        btnAddNote.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddNoteActivity.class)));

        // Tombol Scan OCR (Kamera)
        btnScanOcr.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                requestCameraPermission.launch(Manifest.permission.CAMERA);
            }
        });

        // Sensor Pencarian
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { filterNotes(s.toString()); }
            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void openCamera() {
        takePictureLauncher.launch(null);
    }

    private void processImageWithMLKit(Bitmap bitmap) {
        Toast.makeText(this, "Sedang memindai teks...", Toast.LENGTH_SHORT).show();
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        recognizer.process(image)
                .addOnSuccessListener(visionText -> {
                    String resultText = visionText.getText();
                    if (resultText.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Tulisan tidak terbaca / Kosong", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                        intent.putExtra("SCANNED_TEXT", resultText);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Gagal scan: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void filterNotes(String text) {
        List<Note> filteredList = new ArrayList<>();
        for (Note note : allNotesList) {
            if (note.getTitle().toLowerCase().contains(text.toLowerCase()) ||
                    note.getContent().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(note);
            }
        }
        if (noteAdapter != null) { noteAdapter.setFilteredList(filteredList); }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();
    }

    private void loadNotes() {
        allNotesList = dbHelper.getAllNotes();
        if (allNotesList.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            rvNotes.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            rvNotes.setVisibility(View.VISIBLE);
            noteAdapter = new NoteAdapter(allNotesList);
            rvNotes.setAdapter(noteAdapter);
        }
    }
}