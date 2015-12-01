package com.closestudios.bro.util;

import android.content.Context;

import com.closestudios.bro.networking.Bro;
import com.closestudios.bro.networking.BroLocation;

/**
 * Created by closestudios on 11/24/15.
 */
public class BroListView extends BroViewBase {
    Bro bro;
    public BroListView(Bro bro) {
        this.bro = bro;
    }

    @Override
    public String getHeader() {
        return bro.broName;
    }

    @Override
    public String getDetails(Context context) {

        if(BroPreferences.getPrefs(context).hasLocation()) {
            return bro.location.getDistance(new BroLocation(BroPreferences.getPrefs(context).getLocationLat(),BroPreferences.getPrefs(context).getLocationLong()));
        } else {
            return "";
        }


    }

    @Override
    public Bro getBro() {
        return bro;
    }
}
