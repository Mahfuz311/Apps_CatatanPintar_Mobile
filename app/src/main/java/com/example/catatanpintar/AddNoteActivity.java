package com.example.catatanpintar;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

// Import khusus untuk Groq AI
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

public class AddNoteActivity extends AppCompatActivity {

    ImageButton btnBack, btnDelete;
    TextView tvHeaderTitle;
    EditText etTitle, etContent;
    DatabaseHelper dbHelper;

    boolean isEditMode = false;
    int noteIdToEdit = -1;

    // Mesin Internet untuk Groq
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        // Menghubungkan ID
        btnBack = findViewById(R.id.btn_back);
        btnDelete = findViewById(R.id.btn_delete);
        tvHeaderTitle = findViewById(R.id.tv_header_title);
        etTitle = findViewById(R.id.et_note_title);
        etContent = findViewById(R.id.et_note_content);
        View btnSummarize = findViewById(R.id.btn_summarize);
        View btnSave = findViewById(R.id.btn_save_note);

        dbHelper = new DatabaseHelper(this);

        // Cek Mode Edit
        Intent intent = getIntent();
        if (intent.hasExtra("NOTE_ID")) {
            isEditMode = true;
            btnDelete.setVisibility(View.VISIBLE);
            tvHeaderTitle.setText("Edit Note");
            noteIdToEdit = intent.getIntExtra("NOTE_ID", -1);
            etTitle.setText(intent.getStringExtra("NOTE_TITLE"));
            etContent.setText(intent.getStringExtra("NOTE_CONTENT"));
        }
        if (intent.hasExtra("SCANNED_TEXT")) {
            String hasilScan = intent.getStringExtra("SCANNED_TEXT");
            etTitle.setText("Hasil Scan Dokumen"); // Judul otomatis
            etContent.setText(hasilScan); // Isi catatan diisi dari foto
            Toast.makeText(this, "Teks berhasil disalin dari foto!", Toast.LENGTH_SHORT).show();
        }


        btnSummarize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textToSummarize = etContent.getText().toString().trim();

                // Groq butuh sedikit konteks agar tidak bingung
                if (textToSummarize.length() < 10) {
                    Toast.makeText(AddNoteActivity.this, "Teks terlalu pendek!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Munculkan Bottom Sheet
                com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog =
                        new com.google.android.material.bottomsheet.BottomSheetDialog(AddNoteActivity.this);
                View bottomSheetView = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_summary, null);
                bottomSheetDialog.setContentView(bottomSheetView);

                TextView tvResult = bottomSheetView.findViewById(R.id.tv_summary_result);
                View btnCopy = bottomSheetView.findViewById(R.id.btn_copy_summary);

                tvResult.setText("Sedang meringkas dengan Groq AI...");
                bottomSheetDialog.show();

                // 1. Membungkus Pesan ke Format JSON Groq
                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("model", "llama-3.1-8b-instant");
                    JSONArray messages = new JSONArray();
                    JSONObject message = new JSONObject();
                    message.put("role", "user");
                    message.put("content", "Ringkas teks berikut dengan poin-poin singkat dalam Bahasa Indonesia: " + textToSummarize);
                    messages.put(message);
                    jsonBody.put("messages", messages);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // 2. Mengirim Request ke Server Groq (Perbaikan di MediaType)
                RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.parse("application/json; charset=utf-8"));
                Request request = new Request.Builder()
                        .url("https://api.groq.com/openai/v1/chat/completions")
                        .header("Authorization", "API KEY GROK") // API Key Groq milikmu
                        .post(body)
                        .build();

                // 3. Menunggu Balasan Groq
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        runOnUiThread(() -> tvResult.setText("Gagal menyambung ke internet: " + e.getMessage()));
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            try {
                                String responseData = response.body().string();
                                JSONObject jsonObject = new JSONObject(responseData);
                                String resultText = jsonObject.getJSONArray("choices")
                                        .getJSONObject(0)
                                        .getJSONObject("message")
                                        .getString("content");

                                runOnUiThread(() -> tvResult.setText(resultText));
                            } catch (Exception e) {
                                runOnUiThread(() -> tvResult.setText("Gagal membaca balasan Groq."));
                            }
                        } else {
                            // JIKA ERROR 400 MUNCUL LAGI, BACA ALASAN ASLINYA:
                            String errorBody = "Tidak ada detail";
                            if (response.body() != null) {
                                errorBody = response.body().string();
                            }
                            final String finalError = errorBody;
                            runOnUiThread(() -> {
                                try {
                                    // Ekstrak pesan spesifik dari Groq
                                    JSONObject errObj = new JSONObject(finalError);
                                    String msg = errObj.getJSONObject("error").getString("message");
                                    tvResult.setText("Ditolak Groq: " + msg);
                                } catch (Exception e) {
                                    // Jika format error aneh, tampilkan semuanya
                                    tvResult.setText("Error " + response.code() + ": " + finalError);
                                }
                            });
                        }
                    }
                });

                // Logika Tombol Copy
                btnCopy.setOnClickListener(view -> {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Summary", tvResult.getText().toString());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(AddNoteActivity.this, "Ringkasan disalin!", Toast.LENGTH_SHORT).show();
                });

                bottomSheetView.findViewById(R.id.btn_close_sheet).setOnClickListener(view -> bottomSheetDialog.dismiss());
            }
        });

        // Logika Save
        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String content = etContent.getText().toString().trim();
            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(AddNoteActivity.this, "Judul dan isi tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            } else {
                if (isEditMode) {
                    dbHelper.updateNote(noteIdToEdit, title, content, "Edited just now");
                    Toast.makeText(AddNoteActivity.this, "Catatan Diperbarui!", Toast.LENGTH_SHORT).show();
                } else {
                    dbHelper.addNote(title, content, "Just now");
                    Toast.makeText(AddNoteActivity.this, "Catatan Disimpan!", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });

        // Logika Hapus
        btnDelete.setOnClickListener(v -> new AlertDialog.Builder(AddNoteActivity.this)
                .setTitle("Hapus Catatan")
                .setMessage("Apakah kamu yakin ingin menghapus catatan ini?")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    dbHelper.deleteNote(noteIdToEdit);
                    Toast.makeText(AddNoteActivity.this, "Catatan Dihapus!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Batal", null)
                .show());

        // Tombol Kembali
        btnBack.setOnClickListener(v -> finish());
    }
}