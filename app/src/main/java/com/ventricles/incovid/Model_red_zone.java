package com.ventricles.incovid;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(tableName = "redzone")
public class Model_red_zone {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "ID")
    private int ID;

    @NonNull
    @ColumnInfo(name = "PlaceName")
    private String PlaceName;

    @NonNull
    @ColumnInfo(name = "Lat")
    private double Lat;

    @NonNull
    @ColumnInfo(name = "Lon")
    private double Lon;


    @NonNull
    @ColumnInfo(name = "Radius")
    private double radius;



    @Ignore
    public Model_red_zone(int ID, String placeName, double lat, double lon, double radius) {
        this.PlaceName = placeName;
        this.Lat = lat;
        this.Lon = lon;
        this.radius = radius;
        this.ID = ID;
    }

    public Model_red_zone() {
    }

    public int getID() {
        return ID;
    }

    @NonNull
    public String getPlaceName() {
        return PlaceName;
    }

    public double getLat() {
        return Lat;
    }

    public double getLon() {
        return Lon;
    }


    public double getRadius() {
        return radius;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setPlaceName(@NonNull String placeName) {
        PlaceName = placeName;
    }

    public void setLat(double lat) {
        Lat = lat;
    }

    public void setLon(double lon) {
        Lon = lon;
    }


    public void setRadius(double radius) {
        this.radius = radius;
    }
}
