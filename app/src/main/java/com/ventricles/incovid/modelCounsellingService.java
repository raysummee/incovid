package com.ventricles.incovid;

public class modelCounsellingService {
    int id;
    String org;
    String natureOfSupport;
    String contact;

    public modelCounsellingService(int id, String org, String natureOfSupport, String contact) {
        this.id = id;
        this.org = org;
        this.natureOfSupport = natureOfSupport;
        this.contact = contact;
    }
}
