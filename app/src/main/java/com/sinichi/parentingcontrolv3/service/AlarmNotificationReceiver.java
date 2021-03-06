package com.sinichi.parentingcontrolv3.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.sinichi.parentingcontrolv3.R;
import com.sinichi.parentingcontrolv3.activity.NotificationOnClick;
import com.sinichi.parentingcontrolv3.util.Constant;

public class AlarmNotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context, createNotificationChannel(context));
        String waktuSholat = intent.getStringExtra(Constant.INTENT_WAKTU_SHOLAT); // Get nama sholat dari MainActivity
        int notificationId = intent.getIntExtra(Constant.INTENT_NOTIFICATION_ID, 1); // Get notification id dari MainActivity
        int requestCode = intent.getIntExtra("request_code", 0);
        Log.e("PendingRequestCode", String.valueOf(requestCode));
        Intent intent1 = new Intent(context, NotificationOnClick.class);
        intent1.putExtra("notif_sholat", waktuSholat); // Passing data menuju NotificationOnClick bersisi nama waktu sholat
        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode, intent1, 0);
        builder.setSmallIcon(R.drawable.ic_notifications_active_black_24dp)
                .setContentTitle("Kamu belum " + waktuSholat + "?")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setContentText("Klik notifikasi ini HANYA JIKA sudah sholat " + waktuSholat)
                .setDefaults(Notification.DEFAULT_LIGHTS |
                        Notification.DEFAULT_SOUND)
                .setContentInfo("Info")
                .setOngoing(true)
                .setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, builder.build());
    }

    public static String createNotificationChannel(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // The id of the channel.
            String channelId = "Channel_id";

            // The user-visible name of the channel.
            CharSequence channelName = "Application_name";
            // The user-visible description of the channel.
            String channelDescription = "Application_name Alert";
            int channelImportance = NotificationManager.IMPORTANCE_LOW;
            boolean channelEnableVibrate = true;
            //            int channelLockscreenVisibility = Notification.;

            // Initializes NotificationChannel.
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, channelImportance);
            notificationChannel.setDescription(channelDescription);
            notificationChannel.enableVibration(channelEnableVibrate);
            // notificationChannel.setLockscreenVisibility(channelLockscreenVisibility);
            // Adds NotificationChannel to system. Attempting to create an existing notification
            // channel with its original values performs no operation, so it's safe to perform the
            // below sequence.
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);

            return channelId;
        } else {
            // Returns null for pre-O (26) devices.
            return null;
        }
    }
}
