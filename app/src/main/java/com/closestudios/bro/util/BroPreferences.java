package com.closestudios.bro.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

/**
 * Created by closestudios on 11/23/15.
 */
public class BroPreferences {

    static BroPreferences instance;
    SharedPreferences prefs;

    public BroPreferences(Context context) {
        prefs = context.getSharedPreferences("com.cclose.bros", Context.MODE_PRIVATE);
    }

    public static BroPreferences getPrefs(Context context) {
        if(instance == null)
            instance = new BroPreferences(context);
        return instance;
    }

    public void setToken(String token) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("token",token);
        edit.commit();
    }

    public boolean hasToken() {
        return prefs.contains("token");
    }

    public String getToken() {
        return prefs.getString("token", null);
    }


    public boolean hasLocation() {
        return prefs.contains("locationlat") && prefs.contains("locationlong");
    }

    public float getLocationLat() {
        return prefs.getFloat("locationlat", 0);
    }

    public float getLocationLong() {
        return prefs.getFloat("locationlong", 0);
    }

    public void setLocation(Location location) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putFloat("locationlat", (float)location.getLatitude());
        edit.putFloat("locationlong", (float)location.getLongitude());
        edit.apply();
    }

}
