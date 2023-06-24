package com.ventricles.incovid;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.system.Os;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service implements LocationListener {
    final static String default_notofication_channel_id = "default";
    public final static String NOTIFICATION_CHANNEL_ID = "10001";
    TimerTask timerTask;
    Timer timer;
    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    double lat;
    double lon;
    LocationManager locationManager;
    Location location;
    AsyncTask asyncTask;
    List<Model_red_zone> model_red_zones_check;
    double distance_check;
    SharedPreferences preferences;



    final Handler handler = new Handler();


    public void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        fnc_getlocation();
                    }
                });
            }
        };
        timer.schedule(timerTask, 5000, 60 * 1000);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (locationManager!=null) {
            locationManager.removeUpdates(this);
            locationManager=null;
        }
        if (location!=null) {
            location.reset();
            location=null;
        }
        if (asyncTask!=null){
            asyncTask.cancel(true);
            asyncTask=null;
        }
        if (model_red_zones_check!=null)
            model_red_zones_check=null;

        preferences.edit().putBoolean("running",false).apply();
        Toast.makeText(this, R.string.notification_service_stopped, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onCreate() {
        super.onCreate();
        model_red_zones_check = new ArrayList<>();
        preferences = getSharedPreferences("notiification",MODE_PRIVATE);
        preferences.edit().putBoolean("running",true).apply(); }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendNotification(String title, String content, int notifyID) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), default_notofication_channel_id);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setSmallIcon(R.mipmap.ic_launcher_foreground);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importantance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "COVID-19", importantance);
            builder.setChannelId(NOTIFICATION_CHANNEL_ID);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
        }
        assert notificationManager != null;
        notificationManager.notify(notifyID, builder.build());

    }

    private void remove_notification(int notifyID){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.cancel(notifyID);
    }

    @Override
    public void onLocationChanged(Location location) {

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
    @SuppressLint("StaticFieldLeak")
    private void check_if_in_redzone(double lat, double lon){
        if (asyncTask!=null){
            asyncTask.cancel(true);
            asyncTask=null;
        }
       asyncTask = new AsyncTask<Double, Void, Void>(){
            List<Model_red_zone> model_red_zones;
            double distance=0;
           @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (model_red_zones.size()>0){
                    if (!model_red_zones_check.isEmpty()) {
                        if (model_red_zones_check.get(0).getLat() != model_red_zones.get(0).getLat()
                                && model_red_zones_check.get(0).getLon() != model_red_zones.get(0).getLon() && distance_check != distance) {
                            distance_check = distance;
                            model_red_zones_check = model_red_zones;
                            remove_notification(1112);
                            sendNotification(getString(R.string.youre_near_a_redzone), getString(R.string.stay_away_from) + model_red_zones.get(0).getPlaceName() + " " + String.format(Locale.getDefault(), "%.2f", distance) + getString(R.string.km_away),1112);
                        }
                    }else {
                        distance_check = distance;
                        model_red_zones_check = model_red_zones;
                        remove_notification(1112);
                        sendNotification(getString(R.string.youre_near_a_redzone), getString(R.string.stay_away_from) + model_red_zones.get(0).getPlaceName() + " " + String.format(Locale.getDefault(), "%.2f", distance) + getString(R.string.km_away), 1112);
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

    private void fnc_getlocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        assert locationManager != null;
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!isGPSEnable && !isNetworkEnable) {

        } else {
            if (isNetworkEnable) {
                location = null;
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    onDestroy();
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this  );
                if (locationManager!=null){
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location!=null){
                        check_if_in_redzone(location.getLatitude(), location.getLongitude());
                    }
                }
            }
        }
    }
}
