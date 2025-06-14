package com.example.masjidapp;

public class BukuModel {
    private String id;
    private String gambarUrl;
    private String deskripsi;
    private String tahun;

    public BukuModel() {}

    public BukuModel(String id, String gambarUrl, String deskripsi, String tahun) {
        this.id = id;
        this.gambarUrl = gambarUrl;
        this.deskripsi = deskripsi;
        this.tahun = tahun;
    }

    public String getId() {
        return id;
    }

    public String getGambarUrl() {
        return gambarUrl;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public String getTahun() {
        return tahun;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public void setGambarUrl(String gambarUrl) {
        this.gambarUrl = gambarUrl;
    }
}