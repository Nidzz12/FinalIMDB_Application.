package com.example.imdb_application;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PexelsDao {

    @Insert
    void insert(Pexels photo);

    @Query("SELECT * FROM pexels_photos")
    List<Pexels> getAll();

    @Delete
    void delete(Pexels photo); // Add this method for deleting a Pexels entity

}
