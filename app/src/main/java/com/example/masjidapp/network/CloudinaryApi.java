package com.example.masjidapp.network;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface CloudinaryApi {
    @Multipart
    @POST("v1_1/dxu91lnwm/image/upload")
    Call<CloudinaryResponse> uploadImage(
            @Part("upload_preset") RequestBody uploadPreset,
            @Part MultipartBody.Part file
    );
}