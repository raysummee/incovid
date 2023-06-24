package com.ventricles.incovid;

import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.RoomDatabase;


@Database(entities = {
            Model_red_zone.class,
            ModelDetailedPlace.class,
            Model_toll_free_no.class,
            modelCovidInfo.class,
            modelCasesDistrict.class,
        },
        version = 1,exportSchema = false)
public abstract class ___database extends RoomDatabase {
    public abstract Dao_red_zone dao_red_zone();
    public abstract DaoModelDetailedPlace daoModelDetailedPlace();
    public abstract Dao_toll_free_no dao_toll_free_no();
    public abstract Dao_covidInfo dao_covidInfo();
    public abstract Dao_cases_districts dao_cases_districts();
}
