package com.ventricles.incovid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActivityMigrantLabour extends AppCompatActivity implements LocationListener {
    EditText name;
    EditText phoneNo;
    Switch isMsf;
    Switch kisMsf;
    Button btnSubmit;
    MigrantApiService migrantApiService;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_migrant_labour);
        name = findViewById(R.id.editLabourName);
        phoneNo = findViewById(R.id.editLabourPhone);
        isMsf = findViewById(R.id.editLabourisMsf);
        kisMsf = findViewById(R.id.editLabourkisMsf);
        btnSubmit = findViewById(R.id.btnSubmit_labour);
        db = FirebaseFirestore.getInstance();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.getText().toString();
                if(!name.getText().toString().equals("")&&!phoneNo.getText().toString().equals("")&&(isMsf.isChecked()||kisMsf.isChecked())){
//                    HashMap<String, Object> userInput = new HashMap<>();
//                    Toast.makeText(ActivityMigrantLabour.this, R.string.submiting_wait, Toast.LENGTH_SHORT).show();
//                    userInput.put("name",name.getText().toString());
//                    userInput.put("phoneNo", phoneNo.getText().toString());
//                    userInput.put("isMsf", isMsf.isChecked());
//                    userInput.put("kisMsf", kisMsf.isChecked());
//                    db.collection("migrantLabour").document().set(userInput).addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            Toast.makeText(ActivityMigrantLabour.this, R.string.submitted_and_being_reviewed, Toast.LENGTH_SHORT).show();
//                            onBackPressed();
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(ActivityMigrantLabour.this, R.string.submission_failed, Toast.LENGTH_SHORT).show();
//                        }
//                    });

                    Toast.makeText(ActivityMigrantLabour.this, R.string.submiting_wait, Toast.LENGTH_SHORT).show();
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("https://hibernian-bill.000webhostapp.com")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    migrantApiService = retrofit.create(MigrantApiService.class);

                    sendPost();
                }else{
                    Toast.makeText(ActivityMigrantLabour.this, "Enter the form completely", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void sendPost() {
        try {
            Location loc = fnc_locationGet();
            Call<ResponseBody> call = migrantApiService.sendPosts(
                    "\""+name.getText().toString()+"\"",
                    "\""+phoneNo.getText().toString()+"\"",
                    isMsf.isChecked()?1:0,
                    kisMsf.isChecked()?1:0,
                    loc==null?0:loc.getLatitude(),
                    loc==null?0:loc.getLongitude()
            );
            assert call != null;
            Log.e("test", String.valueOf(call.request().url()));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.e("testomg", response.message());
                    if(response.isSuccessful()){
                        try {
                            Log.e("testomg", response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Log.e("testinh", "success");
                        if (response.code() == 201) {
                            Toast.makeText(ActivityMigrantLabour.this, "submitted and being reviewed", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        } else {
                            Toast.makeText(ActivityMigrantLabour.this, "failed! Please try again", Toast.LENGTH_SHORT).show();
                        }
                    }



                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("test", t.getLocalizedMessage());
                    Toast.makeText(ActivityMigrantLabour.this, t.toString(), Toast.LENGTH_LONG).show();
                }

            });
        }catch (Exception e){
            Log.e("error",e.getMessage());
        }
    }

    /**
     * Called when the location has changed.
     *
     * <p> There are no restrictions on the use of the supplied Location object.
     *
     * @param location The new location, as a Location object.
     */
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    /**
     * Called when the provider is enabled by the user.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     */
    @Override
    public void onProviderEnabled(String provider) {

    }

    /**
     * Called when the provider is disabled by the user. If requestLocationUpdates
     * is called on an already disabled provider, this method is called
     * immediately.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     */
    @Override
    public void onProviderDisabled(String provider) {

    }

    Location fnc_locationGet(){
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        assert locationManager != null;
        boolean isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!isGPSEnable && !isNetworkEnable) {

        } else {
            if (isNetworkEnable) {
                Location location = null;
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    onDestroy();
                    return null;
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this  );
                if (locationManager!=null){
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location!=null){
                        return location;
                    }
                }
            }
        }
        return null;
    }
}