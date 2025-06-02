package com.example.masjidapp;

import java.io.Serializable;

public class EventModel implements Serializable {
    private String id;
    private String title;
    private String location;
    private String date;
    private String startTime;
    private String endTime;
    private String type;
    private String imageUri;
    private String description;

    public EventModel() {
        // Required for Firebase
    }

    public EventModel(String id, String title, String location, String date, String startTime, String endTime, String type, String imageUri, String description) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
        this.imageUri = imageUri;
        this.description = description;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getImageUri() { return imageUri; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
