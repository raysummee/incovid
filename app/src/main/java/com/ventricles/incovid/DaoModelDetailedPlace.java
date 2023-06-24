package com.ventricles.incovid;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DaoModelDetailedPlace {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long insert(ModelDetailedPlace modelDetailedPlace);

    @Query("SELECT * FROM History ORDER BY creation DESC")
    public LiveData<List<ModelDetailedPlace>> getAllHistory();

    @Query("SELECT * FROM History WHERE lat = :lat AND lon = :lon")
    public List<ModelDetailedPlace> getStaticLatLonHistory(double lat, double lon);

    @Update
    public void update(ModelDetailedPlace modelDetailedPlace);

    @Delete
    void delete(ModelDetailedPlace modelDetailedPlace);
}
