package com.example.fd;

import com.google.gson.annotations.SerializedName;

public class PictureParams {
    @SerializedName("PictureName")
    private String pictureName;

    @SerializedName("Height")
    private String height;

    @SerializedName("Width")
    private String width;

    public PictureParams(String pictureName, String height, String width) {
        this.pictureName = pictureName;
        this.height = height;
        this.width = width;
    }

    public String pictureName(){
        return pictureName;
    }
    public String height(){
        return height;
    }
    public String width(){
        return width;
    }

    public void setHeight(String newhHeight) {
        this.height = newhHeight;
    }

    public void setWidth(String newWidth) {
        this.width = newWidth;
    }
}
