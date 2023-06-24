package com.ventricles.incovid;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface Dao_covidInfo {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(modelCovidInfo modelCovidInfo);

    @Query("SELECT * FROM covidInfo LIMIT 1")
    LiveData<List<modelCovidInfo>> getCovidInfo();
}
