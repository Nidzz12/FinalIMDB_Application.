package com.example.imdb_application;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "movies")
public class Movie {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String year;
    private String runtime;
    private String actors;
    private String plot;
    private String posterUrl;
    private String rating;
    private String language;
    private String director;



    public Movie(String title, String year, String runtime, String actors, String plot, String posterUrl,String rating, String language, String director) {
        this.title = title;
        this.year = year;
        this.runtime = runtime;
        this.actors = actors;
        this.plot = plot;
        this.posterUrl = posterUrl;
        this.rating = rating;
        this.language = language;
        this.director = director;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }
    public String getRating() {
        return rating;
    }
public String getLanguage(){
        return language;
}
public String getDirector(){
        return director;
}

    public void setYear(String year) {
        this.year = year;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }
}
