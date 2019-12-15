package com.fsociety.nirapotta_v3.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.renderscript.RenderScript.Priority;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.fsociety.nirapotta_v3.CustomerCall;
import com.fsociety.nirapotta_v3.HomeActivity;
import com.fsociety.nirapotta_v3.Model.Data;
import com.fsociety.nirapotta_v3.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.Map;

import static android.support.v4.app.NotificationCompat.PRIORITY_MAX;


public class MyFirebaseMessaging extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //as im sending firebase message with lat long of user so must convert msg to latlong

        try {
//            String body = remoteMessage.getNotification().getBody();
//            JSONObject mBody = new JSONObject(body);
//
//            String lat = mBody.getString("latitude");
//            String lng = mBody.getString("longitude");
//            Log.d("LATLNG", lat + "     " + lng);

//            Log.d("NOTIFICATION", remoteMessage.getNotification().getTitle());
//            LatLng user_location = new Gson().fromJson(remoteMessage.getNotification().getBody(),LatLng.class);

            Map<String, String> mData = remoteMessage.getData();
            String lat = (mData.get("title"));
            String lng = (mData.get("detail"));

//            Log.d("NOTIFICATION", "onMessageReceived: "+detail);
//            Log.d("body",remoteMessage.getNotification().getBody());

//            PendingIntent contentIntent=PendingIntent.getActivity(getBaseContext(),0,new Intent(getBaseContext(),CustomerCall.class),PendingIntent.FLAG_ONE_SHOT);
//
//            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getBaseContext());
//            mBuilder.setAutoCancel(true)
//                    .setDefaults(Notification.DEFAULT_LIGHTS|R.raw.bomb)
//                    .setContentTitle("baaal")
//                    .setContentText(detail)
//                    .setSmallIcon(R.drawable.logss)
//                    .setPriority(PRIORITY_MAX)
//                    .setContentIntent(contentIntent);


//            NotificationManager manager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
            Log.d("NOTIFICATION", "onMessageReceived: ");
//            manager.notify(1,mBuilder.build());

            Intent intent = new Intent(getApplicationContext(), CustomerCall.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            intent.putExtra("title", (lat));
            intent.putExtra("detail", (lng));
            intent.putExtra("med", mData.get("med"));
            intent.putExtra("harrass", mData.get("harrass"));
            intent.putExtra("emergency", mData.get("emergency"));

            Log.d("check", "onMessageReceived: " + lat + " " + lng);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
