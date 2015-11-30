package com.example.myapp;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import android.support.v4.content.LocalBroadcastManager;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

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
    public void onCreate() {
        super.onCreate();
//        onHandleIntent(null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int re = super.onStartCommand(intent, flags, startId);
//        onHandleIntent(intent);
        return re;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        System.out.println("------------onHandleIntent---------------");

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

            System.out.println("-------------Exception--------------");
            System.out.println(e.getMessage());
            pref.edit().putBoolean("SENT_TOKEN", false).apply();
        }

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent("REGISTRATION_COMPLETE");
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(String token) {
        // 52.10.102.202
        String url = "http://52.26.66.209:8080/register?" + "token=" + token + "&user=" + "me";
        try {
            String result = httpGet(url);
            System.out.println("Register result: " + result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String httpGet(String url) throws IOException {
//        URL obj = new URL(url);
//        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//        StringBuffer response = new StringBuffer();

        HttpGet get = new HttpGet(url);
        HttpClient client = new DefaultHttpClient();
        String responseString = null;

        try {
            HttpResponse response = client.execute(get);
            int statusCode = response.getStatusLine().getStatusCode();
//            System.out.println("--------------------response:" + response + "      status:" + status);
            if (statusCode == 200) {
                responseString = EntityUtils.toString(response.getEntity());
//                System.out.println("---------------responseString:" + responseString);
                return responseString;
            }

        }catch (Exception e){
            System.out.println("----------------");
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        return responseString;
    }
}
