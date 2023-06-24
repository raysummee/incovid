package com.ventricles.incovid;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "CaseDistrict")
public class modelCasesDistrict {
    @PrimaryKey
    @ColumnInfo
    @NonNull
    int ID;

    @ColumnInfo
    @NonNull
    String district;

    @ColumnInfo
    @NonNull
    int activeCase;

    @ColumnInfo
    @NonNull
    double lat;

    @ColumnInfo
    @NonNull
    double lon;

    modelCasesDistrict(){}

    public void setActiveCase(int activeCase) {
        this.activeCase = activeCase;
    }

    public void setDistrict(@NonNull String district) {
        this.district = district;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    @NonNull
    public String getDistrict() {
        return district;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public int getActiveCase() {
        return activeCase;
    }

    public int getID() {
        return ID;
    }
}
