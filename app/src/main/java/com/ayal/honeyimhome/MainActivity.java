package com.ayal.honeyimhome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_PERMISSION_LOCATION = 99;
    private LocationTracker locationTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationTracker = new LocationTracker();

        final Button locationButton = findViewById(R.id.location_button);
        final MainActivity activity = this;

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (locationButton.getText() == "start tracking location") {
                    boolean hasLocationPermission =
                            ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) ==
                                    PackageManager.PERMISSION_GRANTED;
                    // if there is permission start tracking
                    if (hasLocationPermission) {
                        locationTracker.startTracking();
                    // if no permission ask for permission
                    } else {
                        ActivityCompat.requestPermissions(
                                activity,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_PERMISSION_LOCATION);
                    }
                }
                else{

                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            locationTracker.startTracking(); // todo check
        } else {
            // the user has denied our request! =-O todo check
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // reached here? means we asked the user for this permission more than once,
                // and they still refuse. This would be a good time to open up a dialog
                // explaining why we need this permission
            }
        }


    }


}
