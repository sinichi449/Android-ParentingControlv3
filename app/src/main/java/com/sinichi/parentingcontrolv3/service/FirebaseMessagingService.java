package com.sinichi.parentingcontrolv3.service;

import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.enums.PNPushType;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.push.PNPushAddChannelResult;
import com.sinichi.parentingcontrolv3.activity.ChatActivity;
import com.sinichi.parentingcontrolv3.util.Constant;

import java.util.Arrays;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e(Constant.TAG, "New Token : " + s);
        sendRegistrationToPubNub(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e(Constant.TAG, "From : " + remoteMessage.getFrom());
        if (remoteMessage.getData().size() > 0) {
            // TODO: Message data payload = remoteMessage.getData()

        }
        if (remoteMessage.getNotification() != null) {
            // TODO: Message notification body = remoteMessage.getNotification().getBody()

        }
    }

    private void sendRegistrationToPubNub(String token) {
        ChatActivity.pubnub.addPushNotificationsOnChannels()
                .pushType(PNPushType.GCM)
                .channels(Arrays.asList("HelloPush", "TestPushChannel"))
                .deviceId(token)
                .async(new PNCallback<PNPushAddChannelResult>() {
                    @Override
                    public void onResponse(PNPushAddChannelResult result, PNStatus status) {
                        Log.e(Constant.TAG, "PUB Status code : " + status.getStatusCode());
                    }
                });
    }


}
