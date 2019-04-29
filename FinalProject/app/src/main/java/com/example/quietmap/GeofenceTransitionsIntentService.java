// courtesy Wilgins Mistilien
//
package com.example.quietmap;



import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationManagerCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import android.os.ResultReceiver;

import android.media.AudioManager;
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
        audioManagerRinger = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            return;
        }

        int geoFenceTransition = geofencingEvent.getGeofenceTransition();

        // triggers when user enters
        if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {

            // List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();



            // sends user notification
            createNotificationChannel();

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(GeofenceTransitionsIntentService.this);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(GeofenceTransitionsIntentService.this, "Q")
                    .setSmallIcon(R.drawable.ic_quiet)
                    .setContentTitle("You are in a quiet zone")
                    .setContentText("Your phone's ringer has been set to vibrate")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);


            // sends user notification with above setting

            notificationManager.notify(1, builder.build());


            // sets phone to vibrate

            audioManagerRinger.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        }

        // triggers when user exits
        else if(geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            // List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            createNotificationChannel();

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(GeofenceTransitionsIntentService.this);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(GeofenceTransitionsIntentService.this, "Q")
                    .setSmallIcon(R.drawable.ic_loud)
                    .setContentTitle("You have left a quiet zone")
                    .setContentText("Your phone's ringer has been set to normal")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);


            // sends user notification with above setting
            notificationManager.notify(1, builder.build());


            // sets phone to sound
            audioManagerRinger.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        }

        else if(geoFenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {

        }
    }



    private void createNotificationChannel() {
        // Creates the NotificationChannel, but only on devices with API 26+ because the NotificationChannel class is new and not in the support library
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
