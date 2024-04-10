package com.example.imdb_application;

import java.util.List;

public class PexelsResponse {
    private int total_results;
    private List<PexelsPhoto> photos;

    public int getTotal_results() {
        return total_results;
    }

    public void setTotal_results(int total_results) {
        this.total_results = total_results;
    }

    public List<PexelsPhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PexelsPhoto> photos) {
        this.photos = photos;
    }
}
