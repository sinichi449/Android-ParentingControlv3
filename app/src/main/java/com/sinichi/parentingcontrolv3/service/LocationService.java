package com.sinichi.parentingcontrolv3.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.sinichi.parentingcontrolv3.R;
import com.sinichi.parentingcontrolv3.model.LocationModel;

public class LocationService extends IntentService {
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference lokasiRef;
    private boolean isGPS;
    private LocationManager mLocationManager;
    private LocationModel locationModel;


    public LocationService(String name) {
        super(name);
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "location_channel");
        builder.setSmallIcon(R.drawable.ic_my_location_black_24dp)
                .setOngoing(true)
                .setContentTitle("Parenting Control Location")
                .setContentText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.")
                .setAutoCancel(false)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    }
}
