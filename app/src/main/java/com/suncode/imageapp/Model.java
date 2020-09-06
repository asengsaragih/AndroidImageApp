package com.suncode.imageapp;

import com.google.gson.annotations.SerializedName;

public class Model {
    private String id;
    private int width;
    private int height;

    @SerializedName("download_url")
    private String image;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getImage() {
        return image;
    }

    public String getId() {
        return id;
    }
}
