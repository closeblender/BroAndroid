package com.closestudios.bro.util;

import android.app.Application;

import com.closestudios.bro.BroLocationService;

/**
 * Created by closestudios on 11/23/15.
 */
public class BroApplication extends Application {

    static BroHub broHub;

    @Override
    public void onCreate() {
        super.onCreate();
        BroLocationService.tryStartLocationService(this);
    }

    public static BroHub getBroHub(String token) {
        if(broHub == null) {
            broHub = new BroHub(token);
        }
        return broHub;
    }

}
