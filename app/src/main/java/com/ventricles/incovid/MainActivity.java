package com.ventricles.incovid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.preference.Preference;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private Location currentlocation;
    static final int RequestCode = 1112;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LinearLayout travelBtn;
    private LinearLayout MoreBtn;
    CardView toolbar__card;
    CardView card_safe;
    boolean __nav__btn__selected;
    DrawerLayout drawer;
    RecyclerView mRecyclerView;
    List<Model_red_zone> __red_zone_list;
    customScroll scrollView;
    __repository _res;
    double distance=100;
    boolean map_loaded = false;
    AsyncTask asyncTask;
    TextView activeCaseValue;
    TextView totalRedZoneValue;
    TextView totalDeath;
    ProgressBar pbRedzone;
    TextView txt_safe;
    Button btnDoctorAppointment;
    Button btnCounsellingService;
    ImageView transImg;


    GoogleSignInClient mGoogleSignInClient;
    AdapterRecyclerQuickViews adapterRecyclerQuickViews;
    List<Circle> redCircleList;
    List<Circle> blueCircleList;
    SharedPreferences mPreference;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        _res.destroyDatabase();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawer = findViewById(R.id.drawer_layout);
        scrollView = findViewById(R.id.scroll_main);
        txt_safe = findViewById(R.id.txt_safe);
        card_safe = findViewById(R.id.card_title_desc);
        totalDeath = findViewById(R.id.deathTotalValue);
        totalRedZoneValue = findViewById(R.id.redzoneTotalValue);
        activeCaseValue = findViewById(R.id.activeCaseValue);
        travelBtn = findViewById(R.id.card_btn_travel);
        MoreBtn = findViewById(R.id.card_btn_more);
        pbRedzone = findViewById(R.id.progress_redzone);
        btnDoctorAppointment = findViewById(R.id.btnDoctorAppoinment);
        btnCounsellingService = findViewById(R.id.btnCounselingService);
        transImg = findViewById(R.id.transparentImagemap);

        SharedPreferences preferences = getSharedPreferences("notiification", MODE_PRIVATE);

        if (!preferences.getBoolean("running", false))
            startService(new Intent(this, NotificationService.class));


        redCircleList = new ArrayList<>();
        blueCircleList = new ArrayList<>();


        mPreference = getSharedPreferences("safeness", MODE_PRIVATE);


        _res = new __repository(this);
        _res.getcases();
        //_res.getzones();
        _res.covidInfo();
        _res.download_toll_free();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);


        ImageView img_user = findViewById(R.id.img_user_main);
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            Glide.with(this)
                    .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                    .transform(new CircleCrop())
                    .fallback(R.drawable.ic_avatar)
                    .error(R.drawable.ic_avatar)
                    .transition(GenericTransitionOptions.<Drawable>with(R.anim.anim_img_load_fade))
                    .into(img_user);


        final ImageButton btn_nav_toolbar = findViewById(R.id.btn_nav_toolbar);
        btn_nav_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                __nav__btn__selected = true;
                drawer.openDrawer(GravityCompat.START);

            }
        });


        travelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }
        });
        MoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ActivityExtras.class));
            }
        });

        btnDoctorAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DoctorAppointmentActivity.class));
            }
        });

        btnCounsellingService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CounsellingServiceActivity.class));
            }
        });


        NavigationView navigationView = findViewById(R.id.nav_view1);
        TextView nameUserNav = navigationView.getHeaderView(0).findViewById(R.id.nav_header_user_name);
        ImageView img_user_nav = navigationView.getHeaderView(0).findViewById(R.id.imageViewForUserProfile);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            nameUserNav.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            Glide.with(this)
                    .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                    .transform(new CircleCrop())
                    .fallback(R.drawable.ic_avatar)
                    .error(R.drawable.ic_avatar)
                    .transition(GenericTransitionOptions.<Drawable>with(R.anim.anim_img_load_fade))
                    .into(img_user_nav);
        } else
            nameUserNav.setText("User");
        nameUserNav.setSelected(true);
        CardView btnCardLogout = findViewById(R.id.logout_btn_nav);
        btnCardLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();

                mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        }
                    }
                });
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                // Handle navigation view item clicks here.
                int id = menuItem.getItemId();

                if (id == R.id.nav_settings) {
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                } else if (id == R.id.nav_refer) {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_TEXT, getString(R.string.installed_this_amazing_app_link_to_app));
                    startActivity(Intent.createChooser(i, getString(R.string.refer_a_friend)));

                } else if (id == R.id.nav_about) {
                    startActivity(new Intent(MainActivity.this, ActivityAbout.class));
                }

                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });


        mRecyclerView = findViewById(R.id.recycler_quick_btns);


        adapterRecyclerQuickViews = new AdapterRecyclerQuickViews(MainActivity.this, __red_zone_list);
        mRecyclerView.setAdapter(adapterRecyclerQuickViews);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        toolbar__card = findViewById(R.id.card_Tool_bar);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        fetchLocation();

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    public void __draw_circle_red_zone(int pos, LatLng point, double radius) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(point);
        circleOptions.radius(radius*2);
        circleOptions.strokeColor(getResources().getColor(R.color.color_stroke_alpha_map));
        circleOptions.fillColor(getResources().getColor(R.color.color_circle_alpha_map));
        circleOptions.strokeWidth(2);
        redCircleList.add(pos, mMap.addCircle(circleOptions));
    }

    public void __remove_red_zone(int pos) {
        if (redCircleList.size() > pos)
            redCircleList.get(pos).remove();
    }
    public void __draw_circle_active_zone(int pos, LatLng point, double radius) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(point);
        circleOptions.radius(radius*2);
        circleOptions.strokeColor(getResources().getColor(R.color.color_stroke_alpha_map));
        circleOptions.fillColor(getResources().getColor(R.color.color_circle_alpha_map));
        circleOptions.strokeWidth(2);
        blueCircleList.add(pos, mMap.addCircle(circleOptions));
    }

    public void __remove_active_zone(int pos) {
        if (blueCircleList.size() > pos)
            blueCircleList.get(pos).remove();
    }




    void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, RequestCode);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentlocation = location;

                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    assert mapFragment != null;
                    transImg.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            switch (event.getAction()){
                                case MotionEvent.ACTION_DOWN:
                                case MotionEvent.ACTION_MOVE:
                                    scrollView.requestDisallowInterceptTouchEvent(true);
                                    return false;
                                case MotionEvent.ACTION_UP:
                                    scrollView.requestDisallowInterceptTouchEvent(false);
                                    return false;
                                default:
                                    return true;
                            }
                        }
                    });
                    mapFragment.getMapAsync(MainActivity.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        LatLng latLng = new LatLng(currentlocation.getLatitude(),currentlocation.getLongitude());
        //mMap.addMarker(new MarkerOptions().position(latLng).title("Your Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 3.0f));
        map_loaded=true;

        red_zone_info();
        //blue_zone_info();
        get_covid_info();
        check_if_safe(currentlocation.getLatitude(),currentlocation.getLongitude());


    }
    public void get_covid_info(){
        _res.getCovidInfo().observe(this, new Observer<List<modelCovidInfo>>() {
            @Override
            public void onChanged(List<modelCovidInfo> modelCovidInfos) {
                Log.e("testing","modelCovidInfo changed");
                if(modelCovidInfos.size()!=0) {
                    activeCaseValue.setText(String.valueOf(modelCovidInfos.get(0).getTotalCase()));
                    totalDeath.setText(String.valueOf(modelCovidInfos.get(0).getDeathCase()));
                }
            }
        });
    }
    public void red_zone_info(){
        _res.getRedzoneNearby(currentlocation.getLatitude(), currentlocation.getLongitude()).observe(this, new Observer<List<Model_red_zone>>() {
            @Override
            public void onChanged(List<Model_red_zone> model_red_zones) {
                adapterRecyclerQuickViews.refresh_list(model_red_zones);
                Log.e("testing","read");
                for (int i=0;i<model_red_zones.size();i++){
                    __remove_red_zone(i);
                }
                for (int i=0;i<model_red_zones.size();i++){
                    __draw_circle_red_zone(i,
                            new LatLng(model_red_zones.get(i).getLat(),model_red_zones.get(i).getLon()),
                            model_red_zones.get(i).getRadius());
                }
                if(!model_red_zones.isEmpty()){
                    pbRedzone.setVisibility(View.GONE);
                }
            totalRedZoneValue.setText(String.valueOf(model_red_zones.size()));
            }
        });
    }

    public void blue_zone_info(){
        _res.getCasesDistrict().observe(this, new Observer<List<modelCasesDistrict>>() {
            @Override
            public void onChanged(List<modelCasesDistrict> modelCasesDistricts) {
                Log.e("testing","read1");
                for (int i=0;i<modelCasesDistricts.size();i++){
                    __remove_active_zone(i);
                }

                for (int i=0;i<modelCasesDistricts.size();i++){
                    int range = 2000;
                    range = (int) (Math.sqrt(modelCasesDistricts.get(i).activeCase*1000));
                    if(range>200000){
                        range = 200000;
                    }
                    __draw_circle_active_zone(i,
                            new LatLng(modelCasesDistricts.get(i).getLat(),modelCasesDistricts.get(i).getLon()),
                            range);
                }
            }
        });
    }



    public void __update_camera(LatLng point){
        CameraUpdate __camUpdate = CameraUpdateFactory.newLatLngZoom(point,15);
        mMap.animateCamera(__camUpdate);
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
    @SuppressLint("StaticFieldLeak")
    private void check_if_safe(double lat, double lon) {
        if (asyncTask != null) {
            asyncTask.cancel(true);
            asyncTask = null;
        }
        asyncTask = new AsyncTask<Double, Void, Void>(){
            List<Model_red_zone> model_red_zones;
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (distance < 0.3) {
                    SharedPreferences.Editor editor = mPreference.edit();
                    editor.putInt("safe", 0);
                    editor.apply();
                    txt_safe.setText(R.string.youre_not_safe);
                    card_safe.setCardBackgroundColor(Color.parseColor("#FFEF5350"));
                } else if (distance < 0.8) {
                    SharedPreferences.Editor editor = mPreference.edit();
                    if (!mPreference.contains("safe") || mPreference.getInt("safe", 10) != 0) {
                        editor.putInt("safe", 5);
                        editor.apply();
                        txt_safe.setText(R.string.partially_safe);
                        card_safe.setCardBackgroundColor(Color.parseColor("#FFFFCA28"));
                    }

                } else if (distance > 2) {
                    SharedPreferences.Editor editor = mPreference.edit();
                    if (!mPreference.contains("safe") || mPreference.getInt("safe", 10) != 0 || mPreference.getInt("safe", 10) != 5) {
                        editor.putInt("safe", 10);
                        editor.apply();
                        txt_safe.setText(R.string.youre_safe);
                        card_safe.setCardBackgroundColor(Color.parseColor("#90DF3D"));
                    }
                }

            }

            @Override
            protected Void doInBackground(Double... doubles) {
                __repository _res = new __repository(getApplicationContext());
                model_red_zones = _res.check_if_in_redzone(doubles[0],doubles[1]);
                if (model_red_zones.size()>0) {
                    distance = (
                            (
                                    Math.acos(
                                            Math.sin((doubles[0] * Math.PI / 180))
                                                    *
                                                    Math.sin((model_red_zones.get(0).getLat() * Math.PI / 180)) + Math.cos((doubles[0] * Math.PI / 180))
                                                    *
                                                    Math.cos((model_red_zones.get(0).getLat() * Math.PI / 180)) * Math.cos(((doubles[1] - model_red_zones.get(0).getLon()) * Math.PI / 180))
                                    )
                            ) * 180 / Math.PI
                    ) * 60 * 1.1515 * 1.609344;

                }
                return null;
            }
        }.execute(lat, lon);
    }

    @Override
    public void onLocationChanged(Location location) {
        check_if_safe(location.getLatitude(),location.getLongitude());
        red_zone_info();
    }

    /**
     * @param s
     * @param i
     * @param bundle
     * @deprecated
     */
    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
