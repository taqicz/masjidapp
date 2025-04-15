package com.example.masjidapp;

public class ModelDonasi {
    private String nama;
    private String jumlah;

    public ModelDonasi(String nama, String jumlah) {
        this.nama = nama;
        this.jumlah = jumlah;
    }

    public String getNama() {
        return nama;
    }

    public String getJumlah() {
        return jumlah;
    }
}
