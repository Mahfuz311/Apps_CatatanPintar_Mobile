package com.example.catatanpintar;

public class Note {
    private int id;
    private String title;
    private String content;
    private String time;

    // Konstruktor
    public Note(int id, String title, String content, String time) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.time = time;
    }

    // Getters (Untuk mengambil nilai)
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getTime() { return time; }
}