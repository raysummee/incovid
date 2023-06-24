package com.ventricles.incovid;

import androidx.annotation.NonNull;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import android.widget.LinearLayout;
import android.widget.ProgressBar;

import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;

import java.util.ArrayList;

import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String GOOGLE_API_KEY = "AIzaSyBSU9du7mJlCPOv7lprFZyZnDs9YyRhhkM";
    private GoogleMap mMap;
    private Location currentlocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    Marker __marker;
    List<Marker> __marker_list;
    List<Model_red_zone> ___redZoneList;
    static final int RequestCode = 1201;
    FirebaseFirestore db;
    List<Circle> redCircleList;
    EditText edit_search;
    CardView _card_button_section;
    float __dy_pointer;
    float _dy_pointer2;
    float __dy_origin_location;
    boolean expanded = false;
    boolean shouldExpand = false;
    ProgressBar pbar;
    AsyncTask asyncTask;
    AdapterRecyclerDetailedView adapterRecyclerDetailedView;
    AutocompleteSessionToken token;
    __repository __res;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_travel);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        token = AutocompleteSessionToken.newInstance();
        if (!CheckGooglePlayServices())
            finish();

        final RecyclerView mRecyclerView = findViewById(R.id.recycler_nearby_travel);
        RecyclerView mRecyclerViewDetailed = findViewById(R.id.recycler_detailed_view);
        edit_search = findViewById(R.id.edit_search_place_travel);
        pbar = findViewById(R.id.pbar_nearby);
        ImageButton btn_search_place = findViewById(R.id.btn_search_map_travel);
        _card_button_section = findViewById(R.id.card_button_section);
        __dy_origin_location = _card_button_section.getTranslationY();
        _card_button_section.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        _card_button_section.getLayoutTransition().disableTransitionType(LayoutTransition.CHANGING);
                        __dy_pointer = getResources().getDisplayMetrics().heightPixels - motionEvent.getRawY();
                        _dy_pointer2 = _card_button_section.getHeight();
                        Log.e("touch",motionEvent.getRawY()+"::"+motionEvent.getY()+"::"+__dy_pointer);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!expanded) {
                            if (getResources().getDisplayMetrics().heightPixels - motionEvent.getRawY() >= __dy_pointer) {
                                Log.e("touch", String.valueOf(getResources().getDisplayMetrics().heightPixels - (int) motionEvent.getRawY() + __dy_pointer));
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (getResources().getDisplayMetrics().heightPixels - (int) motionEvent.getRawY() - __dy_pointer + _dy_pointer2));
                                _card_button_section.setLayoutParams(layoutParams);
                               if (getResources().getDisplayMetrics().heightPixels - (int) motionEvent.getRawY() - __dy_pointer + _dy_pointer2 >= getResources().getDisplayMetrics().heightPixels/3f)
                                   shouldExpand = true;
                            }
                        }else{
                            if (getResources().getDisplayMetrics().heightPixels - motionEvent.getRawY() <= __dy_pointer && getResources().getDisplayMetrics().heightPixels - motionEvent.getRawY() >= 190*getResources().getDisplayMetrics().density - getResources().getDisplayMetrics().heightPixels + __dy_pointer) {
                                Log.e("touch", String.valueOf(getResources().getDisplayMetrics().heightPixels - (int) motionEvent.getRawY() + __dy_pointer));
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (getResources().getDisplayMetrics().heightPixels - (int) motionEvent.getRawY() - __dy_pointer + _dy_pointer2));
                                _card_button_section.setLayoutParams(layoutParams);
                                if (getResources().getDisplayMetrics().heightPixels - (int) motionEvent.getRawY() - __dy_pointer + _dy_pointer2 <= getResources().getDisplayMetrics().heightPixels - getResources().getDisplayMetrics().density*20){
                                    shouldExpand = false;
                                }

                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (shouldExpand){
                            _card_button_section.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                            _card_button_section.setLayoutParams(layoutParams);
                            expanded = true;
                        }else{
                            _card_button_section.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (getResources().getDisplayMetrics().density*190));
                            _card_button_section.setLayoutParams(layoutParams);
                            expanded = false;
                        }
                        break;




                }
                return true;
            }
        });
        btn_search_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (asyncTask!=null)
                    asyncTask.cancel(true);
                asyncTask=null;
                if (!TextUtils.isEmpty(edit_search.getText())) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    assert inputMethodManager != null;
                    inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(),0);
                    close_expanded_card();
                    search_place();
                }
            }
        });



        List<ModelNearbyButton> list = new ArrayList<>();
        List<ModelDetailedPlace> listForDetailed = new ArrayList<>();
        redCircleList = new ArrayList<>();
        __marker_list = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        __res = new __repository(this);
        edit_search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    open_expanded_card();
            }
        });




        list.add(new ModelNearbyButton(R.drawable.button_circle,R.drawable.ic_restaurant_white_24dp,"restaurant"));
        list.add(new ModelNearbyButton(R.drawable.button_circle_2,R.drawable.ic_grocceries_white_24dp,"grocery_or_supermarket"));
        list.add(new ModelNearbyButton(R.drawable.button_circle_3,R.drawable.ic_medic_white,"pharmacy"));
        list.add(new ModelNearbyButton(R.drawable.button_circle_4,R.drawable.ic_bank_white_24,"bank"));


        AdapterRecyclerNearby adapterRecyclerNearby = new AdapterRecyclerNearby(this,list);
        mRecyclerView.setAdapter(adapterRecyclerNearby);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        adapterRecyclerDetailedView = new AdapterRecyclerDetailedView(this,listForDetailed);
        mRecyclerViewDetailed.setAdapter(adapterRecyclerDetailedView);
        RecyclerView.LayoutManager mLayoutManagerVertical = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerViewDetailed.setLayoutManager(mLayoutManagerVertical);


        __res.getAllHistory().observe(this, new Observer<List<ModelDetailedPlace>>() {
            @Override
            public void onChanged(List<ModelDetailedPlace> list) {
                adapterRecyclerDetailedView.refreshList(list);
            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLocation();

    }

    public void __remove_red_zone(int pos){
        if (redCircleList.size()>pos)
        redCircleList.get(pos).remove();
    }


    @Override
    public void onBackPressed() {
        if (expanded)
        {
            close_expanded_card();
        }else {
            super.onBackPressed();
        }
    }

    public void close_expanded_card(){
        _card_button_section.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (getResources().getDisplayMetrics().density*190));
        _card_button_section.setLayoutParams(layoutParams);
        edit_search.clearFocus();
        expanded = false;
    }
    public void open_expanded_card(){
        _card_button_section.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        _card_button_section.setLayoutParams(layoutParams);
        expanded = true;
    }

    void fetchLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, RequestCode);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location!=null){
                    currentlocation = location;

                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    assert mapFragment != null;
                    mapFragment.getMapAsync(MapsActivity.this);
                }
            }
        });
    }

    public ProgressBar getPbar(){
        return pbar;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng myLocation = new LatLng(currentlocation.getLatitude(),currentlocation.getLongitude());

       // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation,15.0f));
        mMap.setMyLocationEnabled(true);
        red_zone_info();


    }

    public void __draw_circle_red_zone(int pos, LatLng point, double radius){
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(point);
        circleOptions.radius(radius);
        circleOptions.strokeColor(getResources().getColor(R.color.color_stroke_alpha_map));
        circleOptions.fillColor(getResources().getColor(R.color.color_circle_alpha_map));
        circleOptions.strokeWidth(2);
        redCircleList.add(pos, mMap.addCircle(circleOptions));
    }

    public Location getCurrentlocation(){
        return currentlocation;
    }

    public void __add_search_marker(LatLng point){
        if (__marker!=null)
            __marker.remove();
        remove_search_marker_bulk();
         __marker = mMap.addMarker(new MarkerOptions().position(point).title("Destination"));
         CameraUpdate __camUpdate = CameraUpdateFactory.newLatLngZoom(point,15);
         mMap.animateCamera(__camUpdate);
    }
    public void __add_search_marker_bulk(LatLng point, MarkerOptions options){

        __marker_list.add(mMap.addMarker(options));
        CameraUpdate __camUpdate = CameraUpdateFactory.newLatLngZoom(point,15);
        mMap.animateCamera(__camUpdate);
    }
    public void remove_search_marker_bulk(){
        if (__marker_list.size()>0) {
            for (int i = 0; i < __marker_list.size(); i++)
                __marker_list.get(i).remove();
        }
    }

    public void red_zone_info(){
        __repository _resposity = new __repository(this);
        _resposity.getRedzone().observe(this, new Observer<List<Model_red_zone>>() {
            @Override
            public void onChanged(List<Model_red_zone> model_red_zones) {
                for (int i=0;i<model_red_zones.size();i++){
                    __remove_red_zone(i);
                }
                for (int i=0;i<model_red_zones.size();i++){
                    __draw_circle_red_zone(i,
                            new LatLng(model_red_zones.get(i).getLat(),model_red_zones.get(i).getLon()),
                            model_red_zones.get(i).getRadius());
                }
            }
        });
    }

    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }



    @SuppressLint("StaticFieldLeak")
    void search_place(){
        Log.e("searching","started");
        asyncTask = new AsyncTask<Void,Void,Void>(){

            @Override
            protected void onCancelled(Void aVoid) {
                super.onCancelled(aVoid);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (addressList.size()>0){
                    Log.e("searching", "found" + addressList.get(0).toString());
                    __add_search_marker(new LatLng(addressList.get(0).getLatitude(), addressList.get(0).getLongitude()));
                    __res.insert_into_history(edit_search.getText().toString(),addressList.get(0).getLatitude(),addressList.get(0).getLongitude(),addressList.get(0).getAddressLine(0));
                }else{
                    Toast toast = Toast.makeText(MapsActivity.this,"No Result Found",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP,0, (int) (getResources().getDisplayMetrics().density*30));
                    toast.show();                }
            }

            String search;
            List<Address>  addressList;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                search = edit_search.getText().toString();
                if (search.isEmpty()){
                    this.cancel(true);
                }else {
                    Toast toast = Toast.makeText(MapsActivity.this,"Searching",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP,0, (int) (getResources().getDisplayMetrics().density*30));
                    toast.show();
                }
            }

            @Override
            protected Void doInBackground(Void... voids) {
                Geocoder geocoder = new Geocoder(MapsActivity.this);
                addressList = new ArrayList<>();
                try {
                        addressList = geocoder.getFromLocationName(search,7);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                return null;
            }
        }.execute();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case RequestCode:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    fetchLocation();
                }
        }
    }





}
