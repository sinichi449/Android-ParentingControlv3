package com.sinichi.parentingcontrolv3.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinichi.parentingcontrolv3.R;
import com.sinichi.parentingcontrolv3.common.ChatAlt;
import com.sinichi.parentingcontrolv3.model.LocationModel;
import com.sinichi.parentingcontrolv3.util.Constant;
import com.sinichi.parentingcontrolv3.util.GpsUtil;
import com.sinichi.parentingcontrolv3.util.SetAppearance;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProvider;
    private SupportMapFragment mapFragment;
    private BottomNavigationView bottomNavigationView;
    private LatLng currentLocation;
    private boolean isGPS = false;
    private ChatAlt chatAlt;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference locationRef;
    private LocationModel locationModel;

    private void initComponents() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        mFusedLocationProvider = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
        mFirebaseAuth = FirebaseAuth.getInstance();
        chatAlt = new ChatAlt();
        chatAlt.buildGoogleApiClient(MapsActivity.this);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        locationRef = mFirebaseDatabaseReference
                .child(mFirebaseUser.getUid())
                .child(Constant.LOCATION_CHILD);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetAppearance.setExtendStatusBarWithView(this);
        SetAppearance.hideNavigationBar(this);
        setContentView(R.layout.activity_maps);
        turnOnGPS();
        initComponents();
        mapFragment.getMapAsync(this);
        SetAppearance.onBottomNavigationClick(this, this, bottomNavigationView, R.id.menu_map);
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    Constant.REQUEST_LOCATION);
            return false;
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        String username = getSharedPreferences(Constant.SHARED_PREFS, MODE_PRIVATE).getString(Constant.USERNAME, null);
        mMap = googleMap;
        if (username.equals(Constant.USER_ANAK)) {
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(true);
            animateToUserLocation();
            if (checkPermission()) {
                mMap.setMyLocationEnabled(true);
            }
        } else if (username.equals(Constant.USER_ORANG_TUA)) {
            mMap.getUiSettings().setCompassEnabled(true);
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    LocationModel locationModel = dataSnapshot.getValue(LocationModel.class);
                    if (locationModel != null) {
                        currentLocation = new LatLng(locationModel.getLatitude(), locationModel.getLongitude());
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(currentLocation)
                                .title("Lokasi Anak"))
                                .setSnippet(getAdress(MapsActivity.this,
                                        locationModel.getLatitude(), locationModel.getLongitude()));
                        float zoomLevel = 16.0f;
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoomLevel));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            locationRef.addValueEventListener(valueEventListener);
        }
    }

    private String getAdress(Context context, double lat, double lng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
//            add = add + "\n" + obj.getCountryName();
//            add = add + "\n" + obj.getCountryCode();
//            add = add + "\n" + obj.getAdminArea();
//            add = add + "\n" + obj.getPostalCode();
            add = add + "\n" + obj.getSubAdminArea();
            add = add + "\n" + obj.getLocality();
            add = add + "\n" + obj.getSubThoroughfare();
            return add;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            return null;
        }
    }

    private void animateToUserLocation() {
        mFusedLocationProvider.getLastLocation().addOnSuccessListener(MapsActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    float zoomLevel = 20.0f;
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoomLevel));
                    requestLocationUpdates();
                } else {
                    Snackbar.make(findViewById(R.id.constraint_layout_maps), "Current location not available, turn on GPS.",
                            Snackbar.LENGTH_LONG).show();
                    turnOnGPS();
                }
            }
        });
    }

    private void turnOnGPS() {
        new GpsUtil(MapsActivity.this).turnGPSOn(new GpsUtil.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                isGPS = isGPSEnable;
            }
        });
    }

    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();
        request.setInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        int permission = ContextCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationProvider.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        locationModel = new LocationModel(location.getLatitude(), location.getLongitude());
                        locationRef.setValue(locationModel);
                        Log.d("CurrentLocation", String.valueOf(locationModel.getLongitude()));
                    }
                }
            }, null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_OK) {
            if (requestCode == Constant.GPS_REQUEST) {
                isGPS = true;
            }
        }
    }
}


