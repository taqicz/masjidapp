package com.example.masjidapp;

import java.util.ArrayList;

public class SharedBukuData {

    private static final ArrayList<BukuModel> bukuList = new ArrayList<>();

    static {
        bukuList.add(new BukuModel(1, R.drawable.buku1, "Tafsir Al-Misbah oleh Quraish Shihab", "2005"));
        bukuList.add(new BukuModel(2, R.drawable.buku2, "Fiqh Wanita - Syaikh Shalih Al-Fauzan", "2010"));
        bukuList.add(new BukuModel(3, R.drawable.buku3, "Sirah Nabawiyah - Ibnu Hisyam", "1998"));
    }

    public static ArrayList<BukuModel> getBukuList() {
        return bukuList;
    }

    public static void addBuku(BukuModel buku) {
        bukuList.add(buku);
    }

    public static void updateBuku(int id, String newDeskripsi) {
        for (BukuModel b : bukuList) {
            if (b.getId() == id) {
                b.setDeskripsi(newDeskripsi);
                break;
            }
        }
    }

    public static void deleteBuku(int id) {
        for (int i = 0; i < bukuList.size(); i++) {
            if (bukuList.get(i).getId() == id) {
                bukuList.remove(i);
                break;
            }
        }
    }

    public static int generateNewId() {
        int maxId = 0;
        for (BukuModel b : bukuList) {
            if (b.getId() > maxId) {
                maxId = b.getId();
            }
        }
        return maxId + 1;
    }
}
