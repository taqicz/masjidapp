package com.example.masjidapp;

import com.google.firebase.database.Exclude;

public class BeritaArtikelModel {
    private String id;
    private String title;
    private String content;
    private String kategori;
    private String imageUrl;
    private String authorUid;
    private String authorName;
    private long timestamp;

    public BeritaArtikelModel() {
    }

    public BeritaArtikelModel(String title, String content, String kategori, String imageUrl, String authorUid, String authorName) {
        this.title = title;
        this.content = content;
        this.kategori = kategori;
        this.imageUrl = imageUrl;
        this.authorUid = authorUid;
        this.authorName = authorName;
    }


    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAuthorUid() {
        return authorUid;
    }
    public void setAuthorUid(String authorUid) {
        this.authorUid = authorUid;
    }
    public String getAuthorName() {
        return authorName;
    }
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}