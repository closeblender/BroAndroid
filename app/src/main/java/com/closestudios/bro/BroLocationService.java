package com.closestudios.bro;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.closestudios.bro.networking.BroLocation;
import com.closestudios.bro.networking.ServerApi;
import com.closestudios.bro.networking.ServerApiCalls;
import com.closestudios.bro.util.BroHub;
import com.closestudios.bro.util.BroPreferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by closestudios on 11/30/15.
 */
public class BroLocationService  extends Service implements LocationListener, ServerApiCalls.UpdateCallback {

    static String TAG = "BroLocationService";
    static int TIME_INTERVAL = 30000;

    LocationManager locationManager;
    Handler pingHandler;

    public static boolean tryStartLocationService(Context context) {
        // Register the listener with the Location Manager to receive location updates
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Enable App To Use Location Services", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if(BroPreferences.getPrefs(context).getSendLocationUpdates()) {
                Intent intent = new Intent(context, BroLocationService.class);
                context.startService(intent);
            } else {
                Toast.makeText(context, "Toggle Locations Updates Are Off", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    }

    public static void tryStopLocationService(Context context) {
        Intent intent = new Intent(context, BroLocationService.class);
        context.stopService(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Location Service Created");

        // Ping!
        pingHandler = new Handler();
        pingHandler.postDelayed(pingRunnable, TIME_INTERVAL);

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Register the listener with the Location Manager to receive location updates
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10 * 1000, 5, this); // 10 Seconds, 5 Meters

            // Get last known intials
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(location != null) {
                onLocationChanged(location);
            }

        } catch(SecurityException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "Location Service Started");


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            locationManager.removeUpdates(this);
        } catch(SecurityException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Location Service Destroyed");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location Pinged!");

        BroPreferences.getPrefs(this).setLocation(location);
        pingLocation();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    Runnable pingRunnable = new Runnable() {
        @Override
        public void run() {
            if(BroPreferences.getPrefs(BroLocationService.this).getSendLocationUpdates()) {
                pingLocation();
                pingHandler.postDelayed(pingRunnable, TIME_INTERVAL);
            }
        }
    };

    public void pingLocation() {
        // Ping Location!
        if(BroPreferences.getPrefs(this).hasToken() && BroPreferences.getPrefs(this).hasLocation()) {
            ServerApi.getApi().createNewRequest().onUpdateLocation(BroPreferences.getPrefs(this).getToken(), new BroLocation(BroPreferences.getPrefs(this).getLocationLat(), BroPreferences.getPrefs(this).getLocationLong()), this);
        }
    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onError(String error) {

    }
}
