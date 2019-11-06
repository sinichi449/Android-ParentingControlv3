package com.sinichi.parentingcontrolv3.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class GpsCheckReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsDimatikan = !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGpsDimatikan) {
            Log.e("Status", "GPS Still disabled");
            // Turn on GPS
            Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Toast.makeText(context, "GPS dimatikan, mohon aktifkan kembali",
                    Toast.LENGTH_SHORT).show();
        } else {
            Log.e("Status", "GPS Enabled, ready to rock");
        }
    }
}
