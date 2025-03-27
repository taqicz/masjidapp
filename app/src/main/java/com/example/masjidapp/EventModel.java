package com.example.masjidapp;

public class EventModel {
    private String title;
    private String location;
    private String date;
    private String time;

    public EventModel(String title, String location, String date, String time) {
        this.title = title;
        this.location = location;
        this.date = date;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}

