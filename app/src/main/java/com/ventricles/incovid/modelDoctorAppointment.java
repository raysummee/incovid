package com.ventricles.incovid;

public class modelDoctorAppointment {
    int id;
    String name;
    String specialisation;
    String contact;

    public modelDoctorAppointment(int id, String name, String specialisation, String contact) {
        this.id = id;
        this.name = name;
        this.specialisation = specialisation;
        this.contact = contact;
    }
}
