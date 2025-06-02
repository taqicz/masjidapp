package com.example.masjidapp;

public class BeritaTrendingModel {
    private String category;
    private String title;

    // Constructor
    public BeritaTrendingModel(String category, String title) {
        this.category = category;
        this.title = title;
    }

    // Getter dan Setter
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
