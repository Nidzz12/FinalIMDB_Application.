package com.example.imdb_application;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EventDao {
    @Insert
    void insertEvent(Event event);

    @Query("SELECT * FROM events")
    List<Event> getAllEvents();

    @Delete
    void delete(Event event);
}
