package com.example.quietmap;


// https://stackoverflow.com/questions/55062679/geofencetransitionsintentservice-has-no-default-constructor
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
            Toast.makeText(this, "Event has error", Toast.LENGTH_SHORT).show();
            return;
        }

        int geoFenceTransition = geofencingEvent.getGeofenceTransition();

        if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
          Toast.makeText(this, "Entered", Toast.LENGTH_SHORT).show();

            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
           // String geofenceTransitionDetails = getGeofenceTransitionDetails(this, geoFenceTransition, triggeringGeofences);

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
            audioManagerRinger.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        }
    }

}