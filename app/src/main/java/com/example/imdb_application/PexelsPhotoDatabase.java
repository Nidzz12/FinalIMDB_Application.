package com.example.imdb_application;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Pexels.class}, version = 1)
public abstract class PexelsPhotoDatabase extends RoomDatabase {
    private static PexelsPhotoDatabase instance;

    public abstract PexelsDao pexelsPhotoDao();

    public static synchronized PexelsPhotoDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            PexelsPhotoDatabase.class, "pexels_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
