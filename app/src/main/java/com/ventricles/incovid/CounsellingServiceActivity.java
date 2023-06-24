package com.ventricles.incovid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class CounsellingServiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counselling_service);
        RecyclerView recyclerView = findViewById(R.id.recyclerCounsellingService);
        List<modelCounsellingService> modelCounsellingServices = new ArrayList<>();
        modelCounsellingServices.add(
                new modelCounsellingService(
                        1,
                        "iCall - Tata institute of Social Sciences Mumbai",
                        "Counselling over phone/chat/email",
                        "9372048501/\n9920241248/\n8369799513/\nicall@tiss.edu"
                )
        );
        modelCounsellingServices.add(
                new modelCounsellingService(
                        2,
                        "Samaritans Mumbai",
                        "-7 days, 5 to 8pm",
                        "8422984528/\ntalk2samaritans@gmail.com"
                )
        );
        modelCounsellingServices.add(
                new modelCounsellingService(
                        3,
                        "Antara Senior Living",
                        "-Senior citizen\n-24x7 helpline\n15 Language",
                        "Contact: 8376804102"
                )
        );
        modelCounsellingServices.add(
                new modelCounsellingService(
                        4,
                        "MPower",
                        "",
                        "Contact: 1800120820050"
                )
        );
        modelCounsellingServices.add(
                new modelCounsellingService(
                        5,
                        "Vandrevala Foundattion",
                        "-24/7 helpline",
                        "7304599836/\n7304599837/\n2570600\nhelp@vandrevlafoundation.com"
                )
        );
        modelCounsellingServices.add(
                new modelCounsellingService(
                        6,
                        "7 Cups of Tea",
                        "-Online chat based platform for support",
                        "Website: www.7cups.com"
                )
        );
        modelCounsellingServices.add(
                new modelCounsellingService(
                        7,
                        "Indian Psychiatric Society",
                        "",
                        "Contact: 9874124224"
                )
        );

        AdapterCounsellingService adapterCounsellingService = new AdapterCounsellingService(this, modelCounsellingServices);
        recyclerView.setAdapter(adapterCounsellingService);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
    }
}