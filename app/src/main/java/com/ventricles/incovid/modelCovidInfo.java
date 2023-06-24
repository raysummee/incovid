package com.ventricles.incovid;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "covidInfo")
public class modelCovidInfo {
    @NonNull
    @ColumnInfo
    @PrimaryKey
    private int totalCase;

    @NonNull
    @ColumnInfo
    private int deathCase;

    public int getTotalCase() {
        return totalCase;
    }

    public void setTotalCase(int totalCase) {
        this.totalCase = totalCase;
    }

    public int getDeathCase(){
        return deathCase;
    }

    public void setDeathCase(int deathCase){
        this.deathCase = deathCase;
    }


    modelCovidInfo(){

    }
}
