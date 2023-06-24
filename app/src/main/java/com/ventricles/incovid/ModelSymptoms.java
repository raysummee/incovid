package com.ventricles.incovid;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelSymptoms {
    @SerializedName("name")
    @Expose
    String name;
    @SerializedName("last_place")
    @Expose
    String last_place;
    @SerializedName("symptoms")
    @Expose
    String symptoms;
    @SerializedName("status")
    @Expose
    String status;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLast_place() {
        return last_place;
    }

    public void setLast_place(String last_place) {
        this.last_place = last_place;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

//    @Override
//    public String toString() {
//        return "{" +"name=\'" + name +"\', last_place=\'" + last_place +"\', symptoms=\'" + symptoms + '\'' +", body='" + "body" + '\'' +'}';
//    }
}
