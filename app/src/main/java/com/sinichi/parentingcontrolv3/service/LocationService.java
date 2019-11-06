package com.sinichi.parentingcontrolv3.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sinichi.parentingcontrolv3.R;
import com.sinichi.parentingcontrolv3.model.LocationModel;
import com.sinichi.parentingcontrolv3.util.Constant;

public class LocationService extends Service {
    private boolean isEnable;

    public LocationService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "location_channel");
        builder.setSmallIcon(R.drawable.ic_my_location_black_24dp)
                .setOngoing(true)
                .setContentTitle("Parenting Control Location")
                .setContentText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.")
                .setAutoCancel(false)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("location_channel", "service",
                    NotificationManager.IMPORTANCE_HIGH));
        }
        startForeground(1, builder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("Status", "Started Command");
//        goToGpsSetting();
        sendToDatabase();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("Status", "onBind");
        return null;
    }

//    private void goToGpsSetting() {
//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        boolean gpsSudahDiaktifkan = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        if (!gpsSudahDiaktifkan) {
//            Toast.makeText(this,"Mohon aktifkan GPS",
//                    Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//            Log.e("Status", "GPS Not enabled");
//        } else {
//            Log.e("Status", "GPS Enabled, ready to use");
//        }
//    }

    private void sendToDatabase() {
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference location_ref = db.child(mFirebaseUser.getUid()).child(Constant.LOCATION_CHILD);
        FusedLocationProviderClient mFusedLocation = new FusedLocationProviderClient(this);
        LocationRequest locationRequest = new LocationRequest()
                .setInterval(10000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mFusedLocation.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                LocationModel model = new LocationModel(location.getLatitude(), location.getLongitude());
                Log.e("Status", String.valueOf(model.getLatitude()));
                Log.e("Status", String.valueOf(model.getLongitude()));
                location_ref.setValue(model);
            }
        }, null);
    }

}
