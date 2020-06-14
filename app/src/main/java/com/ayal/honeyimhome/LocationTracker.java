package com.ayal.honeyimhome;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;

public class LocationTracker {

    Context context;
    LocationInfo locationInfo;

    public LocationTracker(Context context) {
        this.context = context;
        this.locationInfo = new LocationInfo();
    }


    public void startTracking() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    2000,
                    10, locationListener);
        }
        Intent startedIntent = new Intent("started");
        context.sendBroadcast(startedIntent);
    }

    public void stopTracking() {
        Intent stoppedIntent = new Intent("stopped");
        context.sendBroadcast(stoppedIntent);
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            double accuracy = location.getAccuracy();
            locationInfo.setAccuracy(String.valueOf(accuracy));
            locationInfo.setLatitude(String.valueOf(location.getLatitude()));
            locationInfo.setLongitude(String.valueOf(location.getLongitude()));


            if (accuracy < 50) {
                MainActivity.putHomeButton();
            }
            else{
                MainActivity.removeHomeButton();
            }
            Intent locationIntent = new Intent("new_location");
            context.sendBroadcast(locationIntent);
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

        //lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);

    };


    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }
}
