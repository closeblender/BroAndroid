package com.closestudios.bro.util;

import android.app.Application;
import android.content.Context;

import com.closestudios.bro.BroLocationService;

/**
 * Created by closestudios on 11/23/15.
 */
public class BroApplication extends Application {

    static BroHub broHub;
    static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        BroLocationService.tryStartLocationService(this);
        context = this;
    }

    public static BroHub getBroHub(String token) {
        if(broHub == null) {
            broHub = new BroHub(token);
        }
        broHub.updateToken(token);
        return broHub;
    }

    public static Context getContext() {
        return context;
    }

}
