package com.ayal.honeyimhome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_PERMISSION_LOCATION = 99;
    private LocationTracker locationTracker;

    private static Button homeLocationButton;
    private TextView latitudeView;
    private TextView longitudeView;
    private TextView accuracyView;
    private TextView homeLocationView;
    private BroadcastReceiver broadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homeLocationView = findViewById(R.id.home_location);


        homeLocationButton = findViewById(R.id.set_home_location_button);
        homeLocationButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                LocationInfo location = locationTracker.locationInfo;
                SharedPreferencesManager.saveHomeToPreferences(MainActivity.this, location);
                Button clearHome = findViewById(R.id.clear_button);

                LocationInfo homeLocationInfo = locationTracker.locationInfo;
                homeLocationView.setText("your home location is defined as " + homeLocationInfo.getLatitude() + " "
                        + homeLocationInfo.getLongitude());
                homeLocationView.setVisibility(View.VISIBLE);
                clearHome.setVisibility(View.VISIBLE);
            }
        });
        locationTracker = new LocationTracker(this);

        latitudeView = findViewById(R.id.latitude_text);
        longitudeView = findViewById(R.id.longitude_text);
        accuracyView = findViewById(R.id.accuracy_text);


        final Button locationButton = findViewById(R.id.location_button);

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (locationButton.getText().equals("start tracking location")) {
                    boolean hasLocationPermission =
                            ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                                    PackageManager.PERMISSION_GRANTED;
                    Log.e("location", "permission status: " + hasLocationPermission);

                    // if there is permission start tracking
                    if (hasLocationPermission) {
                        locationButton.setText("stop tracking");
                        locationTracker.startTracking();
                        // if no permission ask for permission
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_PERMISSION_LOCATION);
                    }
                    // if stop tracking is pressed
                } else {
                    Button locationButton = findViewById(R.id.location_button);
                    locationButton.setText("start tracking location");
                    locationTracker.stopTracking();
                    homeLocationButton.setVisibility(View.INVISIBLE);

                }
            }
        });

        broadcastReceiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter("new_location"); //todo check
        filter.addAction("started");
        filter.addAction("stopped");
        this.registerReceiver(broadcastReceiver, filter);

        checkHomeLocation();
    }

    public static void putHomeButton() {
        homeLocationButton.setVisibility(View.VISIBLE);
    }

    public static void removeHomeButton() {
        homeLocationButton.setVisibility(View.INVISIBLE);
    }

    private void checkHomeLocation() {
        // if there is home location
        LocationInfo homeLocationInfo = SharedPreferencesManager.getHomeLocationFromPreferences(MainActivity.this);
        if (homeLocationInfo != null) {
            String latitude = homeLocationInfo.getLatitude();
            String longitude = homeLocationInfo.getLongitude();
            homeLocationView.setText("your home location is defined as " + latitude + " " + longitude);
            homeLocationView.setVisibility(View.VISIBLE);
            Button clearHome = findViewById(R.id.clear_button);
            clearHome.setVisibility(View.VISIBLE);

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("request", "code: " + requestCode);

        if (requestCode == REQUEST_CODE_PERMISSION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Button locationButton = findViewById(R.id.location_button);
                locationButton.setText("stop tracking");

                locationTracker.startTracking(); // todo check

                // if permission not granted
            } else {
                // the user has denied our request! =-O todo check
                //add snackBar message
                View view = findViewById(R.id.main_layout_id);
                String message = "we need this permission or we can't operate";
                int duration = Snackbar.LENGTH_LONG;
                Snackbar.make(view, message, duration).show();
//                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {


                // reached here? means we asked the user for this permission more than once,
                // and they still refuse. This would be a good time to open up a dialog
                // explaining why we need this permission
//                }
            }
        }


    }


    public void onClickClearHome(View view) {
        TextView homeLocation = findViewById(R.id.home_location);
        homeLocation.setVisibility(View.INVISIBLE);
        Button clearHome = findViewById(R.id.clear_button);
        clearHome.setVisibility(View.INVISIBLE);
        SharedPreferencesManager.saveHomeToPreferences(this, null);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
//        locationTracker.stopTracking(); todo check
    }


    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
//            if (action.equals("new_location")) {
//
//            }
            if (action.equals("stopped")) {
                latitudeView.setVisibility(View.INVISIBLE);
                longitudeView.setVisibility(View.INVISIBLE);
                accuracyView.setVisibility(View.INVISIBLE);
            }
            if (action.equals("new_location") || action.equals("started")) {
                LocationInfo location = locationTracker.locationInfo;
                if (location != null && location.getLatitude() != null) {
                    Log.e("a", location.getLatitude() + "lat");
                    Log.e("a", location.getLongitude() + "long");
                    latitudeView.setText("latitude: " + location.getLatitude());
                    longitudeView.setText("longitude: " + location.getLongitude());
                    accuracyView.setText("accuracy:" + location.getAccuracy());

                    // make visible
                    latitudeView.setVisibility(View.VISIBLE);
                    longitudeView.setVisibility(View.VISIBLE);
                    accuracyView.setVisibility(View.VISIBLE);
                }
//                if (location.getAccuracy() < 50) {
//                    setHomeButton.setVisibility(View.VISIBLE);
//                }
            }
        }


    }
}
