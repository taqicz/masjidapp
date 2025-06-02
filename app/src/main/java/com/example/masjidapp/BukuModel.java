package com.example.masjidapp;

public class BukuModel {
    private int id;
    private int gambar;
    private String deskripsi;
    private String tahun;

    public BukuModel(int id, int gambar, String deskripsi, String tahun) {
        this.id = id;
        this.gambar = gambar;
        this.deskripsi = deskripsi;
        this.tahun = tahun;
    }

    public int getId() {
        return id;
    }

    public int getGambar() {
        return gambar;
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
}
