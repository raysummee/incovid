package com.ventricles.incovid;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "toll_free_no")
public class Model_toll_free_no {
    @NonNull
    @ColumnInfo(name = "Call_name")
    private String Call_name;

    @PrimaryKey
    @NonNull
    @ColumnInfo(name  ="Call_no")
    private String Call_no;

    @Ignore
    public Model_toll_free_no(String call_name, String call_no) {
        Call_name = call_name;
        Call_no = call_no;
    }

    public Model_toll_free_no() {
    }

    public String getCall_name() {
        return Call_name;
    }

    public String getCall_no() {
        return Call_no;
    }



    public void setCall_name(String call_name) {
        Call_name = call_name;
    }

    public void setCall_no(String call_no) {
        Call_no = call_no;
    }
}
