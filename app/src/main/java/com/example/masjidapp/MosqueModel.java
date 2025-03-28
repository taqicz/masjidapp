package com.example.masjidapp;

public class MosqueModel {
    private String name;
    private String address;
    private float rating;
    private String distance;
    private String imageUrl;


    public MosqueModel(String name, String address, float rating, String distance, String imageUrl) {
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.distance = distance;
        this.imageUrl = imageUrl;
    }

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
    public String getImageUrl() { return imageUrl; }

}

