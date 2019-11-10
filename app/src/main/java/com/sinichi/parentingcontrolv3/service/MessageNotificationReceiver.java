package com.sinichi.parentingcontrolv3.service;

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
import com.sinichi.parentingcontrolv3.activity.ChatActivity;
import com.sinichi.parentingcontrolv3.model.TextChatModel;
import com.sinichi.parentingcontrolv3.util.Constant;

public class MessageNotificationReceiver extends Service {
    private String username;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sharedPreferences = getSharedPreferences(Constant.SHARED_PREFS, MODE_PRIVATE);
        username = sharedPreferences.getString(Constant.USERNAME, null);
        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference messageRef = mDatabaseReference.child(mFirebaseUser.getUid()).child(Constant.MESSAGES_CHILD);
        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TextChatModel textChatModel = new TextChatModel();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    textChatModel = snapshot.getValue(TextChatModel.class);
                    if (textChatModel != null) {
                        textChatModel.setId(snapshot.getKey());
                    }
                }


                if (!textChatModel.getName().equals(username)) {
                    createNotification(textChatModel.getName(), textChatModel.getText());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotification(String username, String message) {
        Context context = MessageNotificationReceiver.this;
        Intent intent = new Intent(this, ChatActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 25, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "message_channel");
        builder.setSmallIcon(R.drawable.ic_question_answer_black_24dp)
                .setContentTitle(username)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("message_channel", "message", NotificationManager.IMPORTANCE_DEFAULT));
        }
        notificationManager.notify(11, builder.build());
    }
}
