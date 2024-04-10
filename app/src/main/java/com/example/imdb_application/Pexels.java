package com.example.imdb_application;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pexels_photos")
public class Pexels {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String url;
    private int width;
    private int height;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public Pexels(int id, String url, int width, int height) {
        this.id = id;
        this.url = url;
        this.width = width;
        this.height = height;
    }
    public String toString() {
        return "Pexels{" +
                "id=" + id +
                ", url='" + url + '\'' +
                // Add other fields you want to include in the log
                '}';
    }
}
