package com.closestudios.bro.util;

import android.content.Context;

import com.closestudios.bro.networking.Bro;

/**
 * Created by closestudios on 11/24/15.
 */
public abstract class BroViewBase {

    public abstract String getHeader();
    public abstract String getDetails(Context context);
    public abstract Bro getBro();

}
