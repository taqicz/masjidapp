package com.example.masjidapp.network;

import com.google.gson.annotations.SerializedName;

public class CloudinaryResponse {
    @SerializedName("secure_url")
    private String secureUrl;

    public String getSecureUrl() {
        return secureUrl;
    }
}