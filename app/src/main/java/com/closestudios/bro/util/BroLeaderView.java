package com.closestudios.bro.util;

import android.content.Context;

import com.closestudios.bro.networking.Bro;

/**
 * Created by closestudios on 11/24/15.
 */
public class BroLeaderView extends BroViewBase {

    Bro bro;
    int place;

    public BroLeaderView(Bro bro, int place) {
        this.bro = bro;
        this.place = place;
    }

    @Override
    public String getHeader() {
        return place + ". " + bro.broName;
    }

    @Override
    public String getDetails(Context context) {
        return getTimeString();
    }

    @Override
    public Bro getBro() {
        return bro;
    }

    public String getTimeString() {
        int days = (int)Math.floor(bro.totalTimeSecs/(60*60*24));
        int hours = (int)Math.floor((bro.totalTimeSecs - days * 60*60*24)/(60*60));
        int minutes = (int)Math.floor((bro.totalTimeSecs - (days * 60*60*24) - (hours * 60 * 60))/(60));
        int seconds = (int)Math.floor((bro.totalTimeSecs - (days * 60*60*24) - (hours * 60 * 60) - (minutes * 60)));

        String output = "";

        if(days > 0) {
            output += days + "d ";
        }
        if(hours > 0) {
            output += hours + "h ";
        }
        if(minutes > 0) {
            output += minutes + "m ";
        }
        output += seconds + "s";

        return output;
    }

}
