package com.sinichi.parentingcontrolv3.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.sinichi.parentingcontrolv3.R;
import com.sinichi.parentingcontrolv3.activity.NotificationOnClick;

public class AlarmNotificationReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, createNotificationChannel(context));
//        Intent intent1 = new Intent(context, MainActivity.class);
        Intent intent1 = new Intent(context, NotificationOnClick.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notifications_active_black_24dp)
                .setContentTitle("Waktunya sholat " + intent.getStringExtra(Constant.INTENT_WAKTU_SHOLAT))
                .setContentIntent(pendingIntent)
                .setContentText("Klik notifikasi ini apabila sudah sholat " + intent.getStringExtra(Constant.INTENT_WAKTU_SHOLAT))
                .setDefaults(Notification.DEFAULT_LIGHTS |
                        Notification.DEFAULT_SOUND)
                .setContentInfo("Info")
                .setOngoing(true);
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    public static String createNotificationChannel(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // The id of the channel.
            String channelId = "Channel_id";

            // The user-visible name of the channel.
            CharSequence channelName = "Application_name";
            // The user-visible description of the channel.
            String channelDescription = "Application_name Alert";
            int channelImportance = NotificationManager.IMPORTANCE_DEFAULT;
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
