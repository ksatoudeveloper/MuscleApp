package io.github.ksatoudeveloper.muscleapp.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Exercise {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public int weight;
}
