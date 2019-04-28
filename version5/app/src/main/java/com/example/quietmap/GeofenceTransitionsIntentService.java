package com.example.quietmap;


// https://stackoverflow.com/questions/55062679/geofencetransitionsintentservice-has-no-default-constructor
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import android.os.ResultReceiver;

import java.util.ArrayList;
import java.util.List;

import android.media.AudioManager;
import android.view.View;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.app.NotificationManager;
import android.os.Build;
import android.app.NotificationChannel;

public class GeofenceTransitionsIntentService extends IntentService {

    protected static final String TAG = "GeofenceTransitionsIS";
    private ResultReceiver mResultReceiver;
    GeofencingEvent geofencingEvent;
    private Context context;
    AudioManager audioManagerNormal;
    AudioManager audioManagerRinger;
    AudioManager audioManagerSilent;
    AudioManager am;
    String data;

    int green = Color.parseColor("#00ff00");
    int yellow = Color.parseColor("#ffff00");
    int red = Color.parseColor("#ff0000");
    int color = green;

    public GeofenceTransitionsIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }





    @Override
    public void onCreate() {
        super.onCreate();
        //Intent i = getIntent();
        audioManagerNormal = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManagerRinger = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManagerSilent = (AudioManager) getSystemService(Context.AUDIO_SERVICE);



        // notificationId is a unique int for each notification that you must define



    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // int color = MapActivity.strokeColor


      /* Bundle extras = intent.getExtras();
        if(extras != null)
            data = extras.getString("color");


        if(data.equals("green"))
            color = green;
        else if(data.equals("yellow"))
            color = yellow;
        else if(data.equals("red"))
            color = red;*/


        geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            return;
        }

        int geoFenceTransition = geofencingEvent.getGeofenceTransition();

        if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {

            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
           // String geofenceTransitionDetails = getGeofenceTransitionDetails(this, geoFenceTransition, triggeringGeofences);


            createNotificationChannel();

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(GeofenceTransitionsIntentService.this);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(GeofenceTransitionsIntentService.this, "Q")
                    .setSmallIcon(R.drawable.ic_quiet)
                    .setContentTitle("You are in a quiet zone")
                    .setContentText("Your phone's ringer has been set to vibrate")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            notificationManager.notify(1, builder.build());

            audioManagerRinger.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        }


     /*   else if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER  && color == red) {
          Toast.makeText(this, "Entered", Toast.LENGTH_SHORT).show();

            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            // String geofenceTransitionDetails = getGeofenceTransitionDetails(this, geoFenceTransition, triggeringGeofences);

            audioManagerRinger.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        }*/


        else if(geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            //Toast.makeText(this, "Exited", Toast.LENGTH_SHORT).show();
            // Log the error.

            createNotificationChannel();

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(GeofenceTransitionsIntentService.this);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(GeofenceTransitionsIntentService.this, "Q")
                    .setSmallIcon(R.drawable.ic_loud)
                    .setContentTitle("You have left a quiet zone")
                    .setContentText("Your phone's ringer has been set to normal")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            notificationManager.notify(1, builder.build());

            audioManagerRinger.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        }

        else if(geoFenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {

        }
    }



    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "QuietMap";
            String description = "Sets the phone to vibrate or normal";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Q", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}