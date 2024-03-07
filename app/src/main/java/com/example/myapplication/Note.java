package com.example.myapplication;
public class Note {
    private String id;
    private String date;
    private String text;

    public Note() {
        // Required empty public constructor for Firestore
    }

    public Note(String date, String text) {
        this.date = date;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
