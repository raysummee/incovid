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
public interface Dao_red_zone {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long insert(Model_red_zone model_red_zone);

    @Query("SELECT * FROM redzone")
    public LiveData<List<Model_red_zone>> getAllRedzone();


    @Query("SELECT * FROM redzone ORDER BY ((Lat - :lat)*(Lat - :lat)) + ((Lon - :lon)*(Lon - :lon)) ASC")
    public LiveData<List<Model_red_zone>> getRedzoneNearby(double lat, double lon);

    @Query("SELECT * FROM redzone WHERE ((Lat - :lat)*(Lat - :lat)) + ((Lon - :lon)*(Lon - :lon)) < (:distance * :distance / 6371) ORDER BY ((Lat - :lat)*(Lat - :lat)) + ((Lon - :lon)*(Lon - :lon)) ASC")
    public LiveData<List<Model_red_zone>> getRedzoneNearbyRange(double lat, double lon, double distance);


    @Query("SELECT * FROM redzone WHERE ID = :ID")
    public LiveData<Model_red_zone> getForIDRedzone(int ID);

    @Query("SELECT * FROM redzone")
    public LiveData<Model_red_zone> getForAllRedzone();

    @Query("SELECT * FROM redzone")
    public List<Model_red_zone> getStaticRedzone();


    @Query("SELECT * FROM redzone WHERE ((Lat - :lat)*(Lat - :lat)) + ((Lon - :lon)*(Lon - :lon)) < (:distance * :distance / 6371) LIMIT 1")
    public List<Model_red_zone> getStaticSingleRedzoneInRange(double lat, double lon, double distance);

    @Update
    public void update(Model_red_zone model_red_zone);

    @Delete
    void delete(Model_red_zone model_red_zone);

}
