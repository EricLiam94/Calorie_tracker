package com.example.calorie_tracker.StepDB;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Steps.class}, version = 2, exportSchema = false)
public abstract class StepsDataBase extends RoomDatabase {
    public abstract stepDAO stepDAO();
    private static volatile StepsDataBase INSTANCE;
    static StepsDataBase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (StepsDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE =
                            Room.databaseBuilder(context.getApplicationContext(),
                                    StepsDataBase.class, "Steps")
                                    .build();
                }
            }
        }
        return INSTANCE;
    }

}
