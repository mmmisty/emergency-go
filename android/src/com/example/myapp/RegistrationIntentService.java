package com.example.myapp;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import android.support.v4.content.LocalBroadcastManager;

public class RegistrationIntentService extends IntentService {

    public RegistrationIntentService() {
        super("RegistrationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            InstanceID insId = InstanceID.getInstance(this);
            String token = insId.getToken(getString(R.string.product_id),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            sendRegistrationToServer(token);

            pref.edit().putBoolean("SENT_TOKEN", true).apply();
        }catch (Exception e) {
            e.printStackTrace();

            pref.edit().putBoolean("SENT_TOKEN", false).apply();
        }

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent("REGISTRATION_COMPLETE");
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
    }
}
