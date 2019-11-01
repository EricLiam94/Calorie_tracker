package com.example.calorie_tracker.StepDB;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface stepDAO {
    @Query("SELECT * FROM steps")
    List<Steps> getAll();
    @Insert
    void insertAll(Steps... s);
    @Insert
    long insert(Steps s);
    @Delete
    void delete(Steps s);
    @Update(onConflict = REPLACE)
    public void updateUsers(Steps... s);
    @Query("DELETE FROM steps")
    void deleteAll();
    @Query("Select sum(s.steps) from Steps s")
    int sumSteps();

}
