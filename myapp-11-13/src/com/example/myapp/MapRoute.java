package com.example.myapp;

import android.location.Location;
import android.os.AsyncTask;
import com.google.android.gms.maps.model.LatLng;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mialiu on 11/12/15.
 */
public class MapRoute extends
        AsyncTask<String, Void, List<List<HashMap<String, String>>>> {
    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... params) {
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin="
                + params[0]
                + "&destination="
                + params[1]
                + "&mode=driving";

        HttpGet get = new HttpGet(url);
        HttpClient client = new DefaultHttpClient();
        List<List<HashMap<String, String>>> routes = null;

        try {
            HttpResponse response = client.execute(get);
            int statusCode = response.getStatusLine().getStatusCode();
//            System.out.println("--------------------response:" + response + "      status:" + status);
            if (statusCode == 200) {
                String responseString = EntityUtils.toString(response.getEntity());
//                System.out.println("---------------responseString:" + responseString);
                routes = parse(responseString);
            }

        } catch (ClientProtocolException e) {
//            e.printStackTrace();
        } catch (IOException e) {
//            e.printStackTrace();
        }
//        System.out.println("doInBackground:"+routes);
        return routes;
    }

    private String GetString(Location loc) {
        return Double.toString(loc.getLatitude()) + "," + Double.toString(loc.getLongitude());
    }

    private List<List<HashMap<String, String>>>  parse(String json) {
        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;

        try {
            jObject = new JSONObject(json);
            JSONParser parser = new JSONParser();
            routes = parser.parse(jObject);
//            System.out.println("do in background:" + routes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return routes;
    }
}
