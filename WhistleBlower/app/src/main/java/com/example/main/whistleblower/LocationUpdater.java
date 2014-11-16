package com.example.main.whistleblower;

/**
 * Created by daniel on 11/16/14.
 */

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


public class LocationUpdater implements LocationListener {

    public static final String LOCATION_SERVICE = "LOCATION_UPDATE";
    public boolean LISTENING;
    public boolean LOCATION_FOUND;


    public LocationUpdater(){
        LOCATION_FOUND = false;
        LISTENING = false;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("LOCATION_SERVICE", "location changed");
        broadcastLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(LOCATION_SERVICE, "Status changed");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(LOCATION_SERVICE, provider+" enabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.e(LOCATION_SERVICE, provider+" disabled");
        LISTENING = false;
    }

    private void broadcastLocation(Location location){
        Log.d(LOCATION_SERVICE, "Broadcasting location!");
        Intent message = new Intent("LOCATION_UPDATE");
        message.putExtra("NEW_LOCATION", location);
        LocalBroadcastManager.getInstance(null).sendBroadcast(message);
    }
}
