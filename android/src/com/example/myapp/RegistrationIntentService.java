package com.example.myapp;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import android.support.v4.content.LocalBroadcastManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class RegistrationIntentService extends IntentService {

    public RegistrationIntentService() {
        super("RegistrationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            InstanceID insId = InstanceID.getInstance(this);
            System.out.println("Instance ID is " + insId.getId());

            String token = insId.getToken(getString(R.string.product_id),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            System.out.println("Token is " + token);

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
        String url = "http://192.168.0.110/register?" + "token=" + token + "$user=" + "me";
        try {
            String result = httpGet(url);
            System.out.println("Register result: " + result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String httpGet(String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

//        int responseCode = con.getResponseCode();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }
}
