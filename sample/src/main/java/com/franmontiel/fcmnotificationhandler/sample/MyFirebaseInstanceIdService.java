package com.franmontiel.fcmnotificationhandler.sample;

import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Francisco J. Montiel on 2/12/16.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        Log.d("FCM", "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

//        sendRegistrationToServer(refreshedToken);
    }

}
