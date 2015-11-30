package com.example.myapp;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import com.google.android.gms.gcm.GcmListenerService;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class MyGcmListenerService extends GcmListenerService {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Bundle notification = (Bundle) data.get("notification");
//        Bundle customData = (Bundle) data.get("data");

        System.out.println(">>>>>> Message Received: " + data.toString());

        if (notification != null) {

            Intent intent = new Intent(MyActivity.RECEIVE_COORDINATE);
            intent.putExtra("latitude", data.getString("latitude"));
            intent.putExtra("longitude", data.getString("longitude"));

            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

            String title = notification.getString("title");
            String message = notification.getString("body") + " lat:" + data.getString("latitude") + " lon:" + data.getString("longitude");
            sendNotification(title, message);
//            double lat = notification.getDouble("latitude");
//            double lon = notification.getDouble("longitude");
        }
    }

    private void sendNotification(String title, String message) {
        int requestID = (int) System.currentTimeMillis();

        Intent intent = new Intent(getApplicationContext(), MyActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//        stackBuilder.addParentStack(MyActivity.class);
//        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestID /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT /*PendingIntent.FLAG_ONE_SHOT*/);
//        PendingIntent pendingIntent = stackBuilder.getPendingIntent(requestID, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.cast_ic_notification_0)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1 /* ID of notification */, notificationBuilder.build());
    }


}
