package com.example.catatanpintar; // Pastikan ini sesuai dengan nama package-mu

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Nama database dan versinya
    private static final String DATABASE_NAME = "CatatanPintar.db";
    private static final int DATABASE_VERSION = 1;

    // Nama tabel dan kolom-kolomnya
    private static final String TABLE_NOTES = "notes";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_TIME = "time";

    // Konstruktor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Perintah ini dijalankan PERTAMA KALI saat aplikasi baru diinstal
    // untuk membuat "tabel" kerangka catatannya
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NOTES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_CONTENT + " TEXT, " +
                COLUMN_TIME + " TEXT)";
        db.execSQL(createTable);
    }

    // Perintah ini dijalankan kalau kita ingin meng-update struktur database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }

    // ==========================================
    // METHOD UNTUK MENYIMPAN CATATAN BARU
    // ==========================================
    public void addNote(String title, String content, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Memasukkan data ke masing-masing kolom
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_CONTENT, content);
        values.put(COLUMN_TIME, time);

        // Simpan ke tabel!
        db.insert(TABLE_NOTES, null, values);
        db.close(); // Tutup brankas setelah selesai
    }

    // ==========================================
    // METHOD UNTUK MENGAMBIL SEMUA CATATAN
    // ==========================================
    public java.util.List<Note> getAllNotes() {
        java.util.List<Note> noteList = new java.util.ArrayList<>();

        // Membuka database untuk dibaca
        SQLiteDatabase db = this.getReadableDatabase();

        // Mengambil semua data dari tabel notes, diurutkan berdasarkan ID dari yang terbesar/terbaru (DESC)
        android.database.Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTES + " ORDER BY " + COLUMN_ID + " DESC", null);

        // Jika ada datanya, kita masukkan satu per satu ke dalam list
        if (cursor.moveToFirst()) {
            do {
                // Mengambil nilai dari masing-masing kolom
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME));

                // Membungkusnya jadi objek Note dan menambahkannya ke list
                noteList.add(new Note(id, title, content, time));
            } while (cursor.moveToNext());
        }

        // Jangan lupa tutup cursor dan database-nya
        cursor.close();
        db.close();

        return noteList;
    }

    // ==========================================
    // METHOD UNTUK MEMPERBARUI CATATAN (EDIT)
    // ==========================================
    public void updateNote(int id, String title, String content, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_CONTENT, content);
        values.put(COLUMN_TIME, time);

        // Memperbarui data yang memiliki ID tertentu
        db.update(TABLE_NOTES, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // ==========================================
    // METHOD UNTUK MENGHAPUS CATATAN (DELETE)
    // ==========================================
    public void deleteNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Menghapus baris data yang ID-nya cocok
        db.delete(TABLE_NOTES, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}