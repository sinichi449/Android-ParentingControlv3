package com.sinichi.parentingcontrolv3;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.sinichi.parentingcontrolv3.activity.NotificationOnClick;

public class NotificationSubuh extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context, createNotificationChannel(context));

        Intent intent1 = new Intent(context, NotificationOnClick.class);
        intent1.putExtra("notif_subuh", " Subuh");
        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, )
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
