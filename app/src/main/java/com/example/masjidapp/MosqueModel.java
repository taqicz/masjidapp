package com.example.masjidapp;

public class MosqueModel {
    private String name;
    private String address;
    private float rating;
    private String distance;

    private String description;
    private String establishedDate;
    private String chairman;
    private String imageUrl;

    public MosqueModel(String name, String address, float rating, String distance) {
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.distance = distance;
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

    public String getDescription(){
        return description;}
    public String getEstablishedDate(){
        return establishedDate;}
    public String getChairman(){
        return chairman;}
    public String getImageUrl(){
        return imageUrl;}
}

