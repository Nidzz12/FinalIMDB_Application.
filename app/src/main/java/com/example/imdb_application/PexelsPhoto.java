package com.example.imdb_application;

import java.io.Serializable;

public class PexelsPhoto implements Serializable {
private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String photographer;
    private PexelsPhotoSrc src;
    private int width;
    private int height;
    private String url;

    public String getPhotographer() {
        return photographer;
    }

    public void setPhotographer(String photographer) {
        this.photographer = photographer;
    }

    public PexelsPhotoSrc getSrc() {
        return src;
    }

    public void setSrc(PexelsPhotoSrc src) {
        this.src = src;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
