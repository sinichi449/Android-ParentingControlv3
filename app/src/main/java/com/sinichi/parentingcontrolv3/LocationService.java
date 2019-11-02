package com.sinichi.parentingcontrolv3;

import android.app.IntentService;
import android.content.Intent;
import android.location.LocationManager;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
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

    }
}
