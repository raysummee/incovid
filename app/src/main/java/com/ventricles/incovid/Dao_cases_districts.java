package com.ventricles.incovid;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface Dao_cases_districts {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void Insert(modelCasesDistrict modelCasesDistrict);

    @Delete
    void Delete(modelCasesDistrict modelCasesDistrict);
    @Query("SELECT * FROM CaseDistrict")
    public List<modelCasesDistrict> getStaticCases();

    @Query("SELECT * FROM CaseDistrict")
    public LiveData<List<modelCasesDistrict>> getActiveCases();


    @Query("SELECT * FROM CaseDistrict  WHERE ((Lat - :lat)*(Lat - :lat)) + ((Lon - :lon)*(Lon - :lon)) < (:distance * :distance / 6371) ORDER BY ((Lat - :lat)*(Lat - :lat)) + ((Lon - :lon)*(Lon - :lon)) ASC")
    LiveData<List<modelCasesDistrict>> getInDistancedActiveCases(double distance, double lat, double lon);
}
