package com.franmontiel.fcmnotificationhandler.sample;

import com.franmontiel.fcmnotificationhandler.RemoteMessageNotifier;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Francisco J. Montiel on 2/12/16.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        new RemoteMessageNotifier(getApplicationContext()).notify(remoteMessage);
    }
}
