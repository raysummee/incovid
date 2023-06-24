package com.ventricles.incovid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.internal.$Gson$Preconditions;

import java.util.ArrayList;
import java.util.List;

import static com.ventricles.incovid.MainActivity.RequestCode;

public class ActivityExtras extends AppCompatActivity {
    CardView btnCovidSymptoms;
    CardView btnTollfreeNo;
    CardView btnEmailAuthority;
    CardView btnDonate;
    CardView btnTraining;
    CardView btnMigrantLabour;
    TextView redzoneRangedNumber;
    TextView redzoneRangedPlaces;
    ChipGroup chipRanged;
    boolean isLocationAvailable;

    private Location currentlocation;
    __repository rep;

    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extras);
        btnCovidSymptoms = findViewById(R.id.btn_covid_syntoms);
        btnEmailAuthority = findViewById(R.id.btnEmailAuthority);
        btnTollfreeNo = findViewById(R.id.btnTollFreeNo);
        btnDonate = findViewById(R.id.btnDonate);
        btnTraining = findViewById(R.id.btnTraining);
        redzoneRangedNumber = findViewById(R.id.redzoneRangedNumber);
        redzoneRangedPlaces = findViewById(R.id.redzoneRangedPlaces);
        chipRanged = findViewById(R.id.chipRanged);
        btnMigrantLabour = findViewById(R.id.btn_migrant_labour);
        isLocationAvailable = false;


        rep = new __repository(this);


        chipRanged.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                if (isLocationAvailable){
                    switch (checkedId){
                        case R.id.redzoneChip100km:
                            updateRedzone(currentlocation, 100);
                            break;
                        case R.id.redzoneChip200km:
                            updateRedzone(currentlocation, 200);
                            break;
                        case R.id.redzoneChip500km:
                            updateRedzone(currentlocation, 500);
                            break;
                        case R.id.redzoneChip1000km:
                            updateRedzone(currentlocation, 1000);
                            break;
                        case R.id.redzoneChip2000km:
                            updateRedzone(currentlocation, 2000);
                            break;
                        case R.id.redzoneChip3000km:
                            updateRedzone(currentlocation, 3000);
                            break;
                        default:
                            Toast.makeText(ActivityExtras.this, "Please select a distance in filter", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnCovidSymptoms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment covidInput = new covid_input();
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.layActiveExtras, covidInput, covidInput.toString())
                        .addToBackStack("xyz")
                        .commitAllowingStateLoss();
            }
        });

        btnTollfreeNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActivityExtras.this, ActivityTollFreeNo.class);
                startActivity(i);
            }
        });

        btnMigrantLabour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i  = new Intent(ActivityExtras.this, ActivityMigrantLabour.class);
                startActivity(i);
            }
        });

        btnDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse("https://www.pmcares.gov.in/en/web/contribution/donate_india");
                i.setData(uri);
                startActivity(Intent.createChooser(i, "Select a browser"));
            }
        });
        btnTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse("https://www.coursera.org/");
                i.setData(uri);
                startActivity(Intent.createChooser(i, "Select a browser"));
            }
        });

        btnEmailAuthority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SENDTO);
                Uri uri = Uri.parse("mailto:?to="+"ncov2019@gov.in");
                i.setData(uri);
                startActivity(Intent.createChooser(i,"Email"));
            }
        });


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        fetchLocation();

    }
    void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, RequestCode);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentlocation = location;
                    isLocationAvailable = true;
                    updateRedzone(location, 100);
                }
            }
        });
    }

    void updateRedzone(Location location, double distance){
        rep.getCasesDistrictInDistance(location.getLatitude(), location.getLongitude(), distance).observe(this, new Observer<List<modelCasesDistrict>>() {
            @Override
            public void onChanged(List<modelCasesDistrict> model_red_zones) {
                StringBuilder stringBuilder = new StringBuilder();
                int activeCases = 0;
                if (!model_red_zones.isEmpty()) {
                    int j;
                    if (model_red_zones.size() > 3)
                        j = 3;
                    else
                        j = model_red_zones.size();
                    for (int i = 0; i < j; i++) {
                        if(model_red_zones.get(i).getActiveCase()>0)
                            stringBuilder.append(model_red_zones.get(i).getDistrict()).append(": ").append(model_red_zones.get(i).getActiveCase()).append(", ");
                        else if(j<model_red_zones.size()-1)
                            ++j;
                    }
                    for (int i=0; i<model_red_zones.size();i++)
                        activeCases = activeCases + model_red_zones.get(i).getActiveCase();
                    stringBuilder.trimToSize();
                    redzoneRangedPlaces.setText(stringBuilder.toString());
                    redzoneRangedNumber.setText(String.valueOf(activeCases));
                }else{
                    redzoneRangedPlaces.setText(R.string.no_places_to_show);
                    redzoneRangedNumber.setText("0");
                }
            }

        });
    }
}