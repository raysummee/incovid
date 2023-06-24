package com.ventricles.incovid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class DoctorAppointmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_appointment);
        RecyclerView recyclerView = findViewById(R.id.recyclerDoctorAppointment);
        List<modelDoctorAppointment> modelDoctorAppointments = new ArrayList<>();
        modelDoctorAppointments.add(new modelDoctorAppointment(
                1,
                "Dr. TS Kler",
                "Heart",
                "Available for online appointments and for emergency consultancy please contact PS Dr Alka Kaur"
        ));
        modelDoctorAppointments.add(new modelDoctorAppointment(
                2,
                "Dr. PK Mishra",
                "Skin",
                "Available for online appointments and for emergency consultancy please contact PS Dr Alka Kaur + 91 9875154655"
        ));
        modelDoctorAppointments.add(new modelDoctorAppointment(
                3,
                "Dr. Alka Kaur",
                "Arteries",
                "Available for online appointments and for emergency consultancy please contact PS Dr Alka Kaur +91 9875845620"
        ));

        AdapterDoctorAppointment adapterDoctorAppointment = new AdapterDoctorAppointment(this, modelDoctorAppointments);
        recyclerView.setAdapter(adapterDoctorAppointment);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
    }
}