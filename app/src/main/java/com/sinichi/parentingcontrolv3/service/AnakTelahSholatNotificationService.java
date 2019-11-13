package com.sinichi.parentingcontrolv3.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinichi.parentingcontrolv3.R;
import com.sinichi.parentingcontrolv3.activity.MainActivity;
import com.sinichi.parentingcontrolv3.model.DataModel;
import com.sinichi.parentingcontrolv3.util.Constant;

public class AnakTelahSholatNotificationService extends Service {
    private Context context;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference kegiatanRef;
    private SharedPreferences sharedPrefs;

    @Override
    public void onCreate() {
        super.onCreate();
        context = AnakTelahSholatNotificationService.this;

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        kegiatanRef = mDatabaseReference.child(mFirebaseUser.getUid()).child(Constant.KEGIATAN_REF_CHILD);
        kegiatanRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataModel dataModel = new DataModel();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    dataModel = snapshot.getValue(DataModel.class);
                    dataModel.setId(snapshot.getKey());
                }

                boolean isSubuh = dataModel.isSholatSubuh();
                boolean isDhuhr = dataModel.isSholatDhuhr();
                boolean isAshar = dataModel.isSholatAshar();
                boolean isMaghrib = dataModel.isSholatMaghrib();
                boolean isIsya = dataModel.isSholatIsya();

                sharedPrefs = getSharedPreferences(Constant.SHARED_PREFS, MODE_PRIVATE);
                boolean isSubuhClicked = sharedPrefs.getBoolean("subuh_sudah_diklik", false);
                boolean isDhuhrClicked = sharedPrefs.getBoolean("dhuhr_sudah_diklik", false);
                boolean isAsharClicked = sharedPrefs.getBoolean("ashar_sudah_diklik", false);
                boolean isMaghribClicked = sharedPrefs.getBoolean("maghrib_sudah_diklik", false);
                boolean isIsyaClicked = sharedPrefs.getBoolean("isya_sudah_diklik", false);

                if (isSubuh) {
                    createNotification("subuh", 101);
                }
                if (isDhuhr) {
                    createNotification("dhuhr", 102);
                }
                if (isAshar) {
                    createNotification("ashar", 103);
                }
                if (isMaghrib) {
                    createNotification("maghrib", 104);
                }
                if (isIsya) {
                    createNotification("isya", 105);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createNotification(String sholat, int id) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "telah_sholat_channel");
        Intent intent = new Intent(context, MainActivity.class);
        String extra = sholat + "_sudah_diklik";
        intent.putExtra(extra, true);
        intent.putExtra("launch_dialog", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 26, intent, PendingIntent.FLAG_ONE_SHOT);
        builder.setContentTitle("Anak Anda telah melakukan sholat " + sholat)
                .setContentText("Klik disini untuk melihat status Anak")
                .setSmallIcon(R.drawable.ic_notifications_active_black_24dp)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = getString(R.string.channel_name);
            String description = "Anak sholat notification";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            channel.setShowBadge(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(id, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
