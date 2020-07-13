package com.example.fd;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitClient {
    @GET("/")
    Call<ArrayList> getImages(@Query("imageName") String imageName);


    @Headers("Content-Type: application/json")
    @GET("getParams")
    Call<String> getParameters(@Query("pictureName") String pictureName);

    @Headers("Content-Type: application/json")
    @POST("upload/4image.png")
    Call<String> uploadPicture(@Body PojoImage imageString);

    @Headers("Content-Type: application/json")
    @POST("saveParams")
    Call<String> saveParameters(@Body PictureParams params);
}
