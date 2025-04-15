package com.example.masjidapp;

public class MosqueModel {
    private String name;
    private String address;
    private float rating;
    private String distance;
    private String imageUrl;
    private String description;
    private String establishedDate;
    private String chairman;

    // Constructor lengkap
    public MosqueModel(String name, String address, float rating, String distance, String imageUrl,
                       String description, String establishedDate, String chairman) {
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.distance = distance;
        this.imageUrl = imageUrl;
        this.description = description;
        this.establishedDate = establishedDate;
        this.chairman = chairman;
    }

    // Constructor lama (jika belum pakai semua data)
    public MosqueModel(String name, String address, float rating, String distance, String imageUrl) {
        this(name, address, rating, distance, imageUrl, "", "", "");
    }

    // Getter
    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public float getRating() {
        return rating;
    }

    public String getDistance() {
        return distance;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getEstablishedDate() {
        return establishedDate;
    }

    public String getChairman() {
        return chairman;
    }
}
