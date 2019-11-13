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
import android.util.Log;

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
import com.sinichi.parentingcontrolv3.model.ChatModel;
import com.sinichi.parentingcontrolv3.model.TextChatModel;
import com.sinichi.parentingcontrolv3.util.Constant;

import java.util.Calendar;

public class ChatNotificationReceiver extends Service {
    private String username;
    private DatabaseReference mDatabaseReference;
    private FirebaseUser mFirebaseUser;
    private SharedPreferences sharedPrefs;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPrefs = getSharedPreferences(Constant.SHARED_PREFS, MODE_PRIVATE);
        username = sharedPrefs.getString(Constant.USERNAME, null);
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabaseReference.child(mFirebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(Constant.MESSAGES_CHILD)) {
                    listenChatChild();
                } else {
                    newChatChild();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(Constant.TAG, databaseError.getMessage());
            }
        });


    }

    private String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        String jamStr, menitStr;
        int jam = calendar.get(Calendar.HOUR_OF_DAY);
        if (jam < 10) {
            jamStr = "0" + jam;
        } else {
            jamStr = String.valueOf(jam);
        }

        int menit = calendar.get(Calendar.MINUTE);
        if (menit < 10) {
            menitStr = "0" + menit;
        } else {
            menitStr = String.valueOf(menit);
        }
        return jamStr + ":" + menitStr;
    }

    private void newChatChild() {
        boolean isInitialMessageShowed = sharedPrefs.getBoolean("initial_messages", false);
        if (!isInitialMessageShowed) {
            String userOrangTua = Constant.USER_ORANG_TUA;
            String userAnak = Constant.USER_ANAK;
            String reverseUsername;
            ChatModel chatModel;
            if (username.equals(userOrangTua)) {
                reverseUsername = userAnak;
                chatModel = new ChatModel("Hai! disini adalah tempat untuk melakukan pesan singkat kepada Orangtua/Anak, cukup ketikkan pesan yang Anda inginkan pada kolomnya dan klik send.\nSelamat menggunakan!",
                        reverseUsername, null, null, getCurrentTime());
            } else {
                reverseUsername = userOrangTua;
                chatModel = new ChatModel("Hai! disini adalah tempat untuk melakukan pesan singkat kepada Orangtua/Anak, cukup ketikkan pesan yang Anda inginkan pada kolomnya dan klik send.\nSelamat menggunakan!",
                        reverseUsername, null, null, getCurrentTime());
            }
            mDatabaseReference.child(mFirebaseUser.getUid()).child(Constant.MESSAGES_CHILD).push().setValue(chatModel);
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putBoolean("initial_message", true);
            editor.apply();
        }
    }


    private void listenChatChild() {
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
        String initialMessage = "Hai! disini adalah tempat untuk melakukan pesan singkat kepada Orangtua/Anak, cukup ketikkan pesan yang Anda inginkan pada kolomnya dan klik send.\nSelamat menggunakan!";
        if (!message.equals(initialMessage)) {
            Context context = ChatNotificationReceiver.this;
            Intent intent = new Intent(this, ChatActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 25, intent, PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "message_channel");
            builder.setSmallIcon(R.drawable.ic_question_answer_black_24dp)
                    .setContentTitle(username)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setDefaults(NotificationCompat.DEFAULT_ALL);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager.createNotificationChannel(new NotificationChannel("message_channel", "message", NotificationManager.IMPORTANCE_DEFAULT));
            }
            notificationManager.notify(11, builder.build());
        }
    }
}
