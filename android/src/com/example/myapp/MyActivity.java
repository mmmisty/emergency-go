package com.example.myapp;

import android.app.Activity;
import android.content.*;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MyActivity extends Activity implements
        GoogleMap.OnMyLocationChangeListener {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private GoogleMap _gMap;
    private LocationManager _locationMgr;
    private String _provider;
    private Location _destination = null;

    public static final String RECEIVE_COORDINATE = "com.example.myapp.RECEIVE_COORDINATE";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        MapFragment map = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        _gMap = map.getMap();
        _gMap.setMyLocationEnabled(true);

        _gMap.setOnMyLocationChangeListener(this);

        System.out.println("------------------onCreate----------------------");
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        } else {
            System.out.println("Warning: checkPlayServices() is failed.");
        }

        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter filer = new IntentFilter();
        filer.addAction(RECEIVE_COORDINATE);
        bManager.registerReceiver(receivingCoordinate, filer);
    }

    @Override
    public void onMyLocationChange(Location lo) {
        if (lo == null) {
            return;
        }
        Location location = _locationMgr.getLastKnownLocation(_provider);
        updateWithNewLocation(location);
    }


    @Override
    protected void onStart() {
        super.onStart();
        initLocationProvider();
        Location begin = _locationMgr.getLastKnownLocation(_provider);

//        Location end = new Location("destination");
//        end.setLatitude(41.8916241);
//        end.setLongitude(-87.6094798);

        updateWithNewLocation(begin);

        if (_destination != null) {
            DrawPath(begin, _destination);
            DrawMark(_destination);
        }

//        System.out.println("------------------" + json);
    }

    @Override
    protected void onResume() {
        super.onResume();

        initLocationProvider();
        Location begin = _locationMgr.getLastKnownLocation(_provider);

        updateWithNewLocation(begin);

        if (_destination != null) {
            DrawPath(begin, _destination);
            DrawMark(_destination);
        }

    }

    private boolean initLocationProvider() {
        _locationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // wifi
        if (_locationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            _provider = LocationManager.NETWORK_PROVIDER;
            return true;
        }

        // GPS
        if (_locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            _provider = LocationManager.GPS_PROVIDER;
            return true;
        }

        return false;
    }


    private void updateWithNewLocation(Location location) {
        if (location != null) {
            cameraFocusOnMe(location.getLatitude(), location.getLongitude());
        }
    }

    private void cameraFocusOnMe(double lat, double lng){
        CameraPosition camPosition = new CameraPosition.Builder()
                .target(new LatLng(lat, lng))
                .zoom(15)
                .build();

        _gMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPosition));
    }

    private void DrawPath(Location begin, Location end) {
        try {
            List<List<HashMap<String, String>>> paths = new MapRoute().execute(GetString(begin), GetString(end)).get();
            if (paths != null) {
                ArrayList<LatLng> points = null;
                PolylineOptions lineOptions = null;
                // Traversing through all the routes
                for (int i = 0; i < paths.size(); i++) {
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = paths.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(5);

                    // Changing the color polyline according to the mode
                    lineOptions.color(Color.BLUE);
                }

                // Drawing polyline in the Google Map for the i-th route
                _gMap.addPolyline(lineOptions);
            }
        }catch (InterruptedException e) {
//            e.printStackTrace();
        } catch (ExecutionException e) {
//            e.printStackTrace();
        }
    }

    private String GetString(Location loc) {
        return Double.toString(loc.getLatitude()) + "," + Double.toString(loc.getLongitude());
    }

    private void DrawMark(Location point) {
        MarkerOptions m = new MarkerOptions();
        LatLng l = new LatLng(point.getLatitude(), point.getLongitude());
        m.position(l).title("Emergency!");
        _gMap.addMarker(m);
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

    private BroadcastReceiver receivingCoordinate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(RECEIVE_COORDINATE)) {
                Bundle data = intent.getExtras();
//                double lat = data.getDouble("latitude");
//                double lon = data.getDouble("longitude");
                String lat = data.getString("latitude");
                String lon = data.getString("longitude");
                System.out.println("-----------------lat:" + lat + ",lon:" + lon);

                Location begin = _locationMgr.getLastKnownLocation(_provider);

                _destination = new Location("destination");
                _destination.setLatitude(Double.parseDouble(lat));
                _destination.setLongitude(Double.parseDouble(lon));

                updateWithNewLocation(begin);
                DrawPath(begin, _destination);
                DrawMark(_destination);
            }
        }
    };

}
