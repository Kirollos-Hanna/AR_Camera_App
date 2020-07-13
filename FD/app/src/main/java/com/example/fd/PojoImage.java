package com.example.fd;

import com.google.gson.annotations.SerializedName;

public class PojoImage {
    @SerializedName("ImageString")
    private String imageString;

    public PojoImage(String imageString) {
        this.imageString = imageString;
    }

    public String imageString(){
        return imageString;
    }
}
