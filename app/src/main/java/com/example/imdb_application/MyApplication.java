package com.example.imdb_application;

import android.app.Application;

import androidx.room.Room;

import com.example.imdb_application.EventDatabase;
import com.example.imdb_application.PexelsPhotoDatabase;

public class MyApplication extends Application {

    private static MyApplication instance;
    private static EventDatabase eventDatabase;
    private PexelsPhotoDatabase pexelsPhotoDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        eventDatabase = Room.databaseBuilder(getApplicationContext(),
                        EventDatabase.class, "event-database")
                .build();
        pexelsPhotoDatabase = Room.databaseBuilder(getApplicationContext(),
                        PexelsPhotoDatabase.class, "pexels_photo_db")
                .build();
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public static EventDatabase getEventDatabase() {
        return eventDatabase;
    }

    public PexelsPhotoDatabase getPexelsPhotoDatabase() {
        return pexelsPhotoDatabase;
    }
}
