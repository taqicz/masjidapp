package com.example.masjidapp;

public class BeritaArtikelModel {
    private String title;
    private String content;
    private String kategori;
    private String imageUrl; // Tambahkan properti untuk gambar

    // Constructor yang diperbarui untuk menyertakan gambar
    public BeritaArtikelModel(String title, String content, String kategori, String imageUrl) {
        this.title = title;
        this.content = content;
        this.kategori = kategori;
        this.imageUrl = imageUrl;
    }

    // Tambahkan constructor tanpa parameter gambar untuk kompatibilitas
    public BeritaArtikelModel(String title, String content, String kategori) {
        this.title = title;
        this.content = content;
        this.kategori = kategori;
        this.imageUrl = ""; // Default value
    }

    // Getter dan Setter yang sudah ada
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

    // Getter dan Setter untuk imageUrl
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}