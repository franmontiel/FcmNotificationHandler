package com.franmontiel.fcmnotificationhandler.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private TextView intentDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intentDetails = (TextView) findViewById(R.id.intentDetails);

//        String token = FirebaseInstanceId.getInstance().getToken();
        FirebaseMessaging.getInstance().subscribeToTopic("fcm_testing");

        showIntentDetails();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        showIntentDetails();
    }

    private void showIntentDetails() {
        Bundle extras = getIntent().getExtras();
        String intentDetailsLog = "";

        String action = getIntent().getAction();
        if (action != null) {
            intentDetailsLog +=
                    "INTENT ACTION\n" + " - " + action + "\n";
        }

        if (extras != null) {
            intentDetailsLog += "\nINTENT EXTRAS\n";
            for (String key : extras.keySet()) {
                intentDetailsLog += " - " + key + " : " + extras.get(key) + "\n";
            }
        }
        intentDetails.setText(intentDetailsLog);
    }
}
