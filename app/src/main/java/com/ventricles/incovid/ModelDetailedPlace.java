package com.ventricles.incovid;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "History")
public class ModelDetailedPlace {
    @PrimaryKey
    @ColumnInfo(name = "ID")
    int ID;
    @ColumnInfo(name = "creation")
    int creation;
    @ColumnInfo(name = "placeName")
    String placename;
    @ColumnInfo(name = "placeAddress")
    String placeAddress;
    @ColumnInfo(name = "lat")
    double lat;
    @ColumnInfo(name = "lon")
    double lon;



    @Ignore
    public ModelDetailedPlace(String placename, String placeAddress, double lat, double lon) {
        this.placename = placename;
        this.placeAddress = placeAddress;
        this.lat = lat;
        this.lon = lon;
    }


    public ModelDetailedPlace() {
    }

    public String getPlacename() {
        return placename;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public void setPlacename(String placename) {
        this.placename = placename;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getCreation() {
        return creation;
    }

    public void setCreation(int creation) {
        this.creation = creation;
    }
}
