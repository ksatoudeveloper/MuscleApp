package io.github.ksatoudeveloper.muscleapp.data;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface ExerciseDao {

    @Insert
    long insert(Exercise exercise);

    @Delete
    void delete(Exercise exercise);

    @Query("SELECT * FROM Exercise ORDER BY id")
    List<Exercise> queryAll();
}
