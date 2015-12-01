package com.closestudios.bro.networking;

import android.location.Location;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by closestudios on 11/23/15.
 */
public class BroLocation {
    public double latitude;
    public double longitude;

    static float METERS_TO_MILES = 0.000621371f;

    public BroLocation() {

    }

    public BroLocation(double lat, double lon) {
        latitude = lat;
        longitude = lon;
    }

    public BroLocation(byte[] bytes) {
        ArrayList<byte[]> broBlocks = DataMessage.getBlocks(bytes);

        latitude = Double.parseDouble(new String(broBlocks.get(0)));
        latitude = Double.parseDouble(new String(broBlocks.get(1)));

    }

    public byte[] getBytes() throws IOException {
        ArrayList<byte[]> locationBlocks = new ArrayList<>();

        locationBlocks.add((latitude + "").getBytes());
        locationBlocks.add((longitude + "").getBytes());

        return DataMessage.createBlocks(locationBlocks);
    }

    public String getDistance(BroLocation location) {
        float distanceInMeters = getLocation().distanceTo(location.getLocation());

        float distanceInMiles = distanceInMeters * METERS_TO_MILES;

        DecimalFormat df = new DecimalFormat("0.##");

        return df.format(distanceInMiles) + " mi";
    }


    public Location getLocation() {
        Location l1 = new Location("");
        l1.setLatitude(latitude);
        l1.setLongitude(longitude);
        return l1;
    }
}
