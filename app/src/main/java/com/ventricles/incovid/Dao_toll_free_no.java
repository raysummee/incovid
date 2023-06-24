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
public interface Dao_toll_free_no {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(Model_toll_free_no model_toll_free_no);

    @Query("SELECT * FROM toll_free_no")
    public LiveData<List<Model_toll_free_no>> getAllTollfree();

    @Query("SELECT * FROM toll_free_no")
    public List<Model_toll_free_no> getStaticTollFreeNo();



    @Update
    public void update(Model_toll_free_no model_toll_free_no);

    @Delete
    void delete(Model_toll_free_no model_toll_free_no);
}
