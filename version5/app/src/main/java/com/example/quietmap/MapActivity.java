// courtesy Wilgins Mistilien
package com.example.quietmap;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.Marker;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.GoogleMap.OnCircleClickListener;
import com.google.android.gms.maps.model.Circle;
import android.graphics.Color;

import android.location.LocationListener;
import com.google.android.gms.location.GeofencingClient;
import android.location.LocationProvider;
import android.location.Criteria;
import android.location.LocationManager;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import android.app.PendingIntent;
import android.content.Intent;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;

import android.media.AudioManager;
import android.content.Context;
import android.os.Build;
import android.app.NotificationManager;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_Z = 15f;
    private String temp = "";



    //widgets
    private AutoCompleteTextView searchText;
    private ImageView gps;
    private ImageView add;
    private ImageView remove;
    private ImageView expand;
    private ImageView shrink;
    private ImageView history;

    //variables
    private Boolean LocationPermissionGranted = false;
    private GoogleMap Map;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private Marker marker;// = Map.addMarker(null);
    private boolean removeOn = false;
    private boolean addOn = false;
    private boolean expandOn = false;
    private boolean shrinkOn = false;
    private int black = Color.parseColor("#000000");
    private int red = Color.parseColor("#ff0000");
    private int green = Color.parseColor("#00ff00");
    private int purple = Color.parseColor("#800080");
    private int yellow = Color.parseColor("#ffff00");
    private int strokeColor = green;


    private LocationManager mgr;
    private String best;
    private double myLocationLatitude;
    private  double myLocationLongitude; // https://stackoverflow.com/questions/11251023/how-to-get-latitude-and-longitude-in-android

    private GeofencingClient geofencingClient;
    private ArrayList<Geofence> geofenceList = new ArrayList<>();
    private ArrayList<Circle> circ = new ArrayList<>();
    private PendingIntent geofencePendingIntent;


    AudioManager audioManager;// = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

    String keyLat = "Lat";
    String keyLng = "Lng";
    String keyRadius = "Radius";
    String keystrokeColor = "strokeColor";
    private ArrayList<Double> latList = new ArrayList<>();
    private ArrayList<Double> lngList = new ArrayList<>();
    private ArrayList<Double> radiusList = new ArrayList<>();
    private ArrayList<Integer> strokeColorList = new ArrayList<>();


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(MapActivity.this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Map = googleMap;
        Log.d(TAG, "OnMapReady: Map is ready");

        // Ask's user for location permission
        if (LocationPermissionGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Map.setMyLocationEnabled(true); // Goes to your location when first opened
            Map.getUiSettings().setMyLocationButtonEnabled(false); // get rid of default center button


            init();

        }


        // creates circle objects from previous uses of the app
        if(latList.size() != 0)
            for (int i = 0; i < latList.size(); i++) {
                CircleOptions options = new CircleOptions()
                        .center(new LatLng(latList.get(i), lngList.get(i)))
                        .radius(radiusList.get(i))
                        .strokeWidth(10)
                        .strokeColor(strokeColorList.get(i))
                        .fillColor(Color.argb(128, 255, 0, 0))
                        .clickable(true);


                circ.add(Map.addCircle(options)); // adds those circles to the map


                temp = Double.toString(latList.get(i)) + "/" + Double.toString(lngList.get(i)); // using those circle's lat/lng we create a unique id for the geofences placed beneath them
                geofenceList.add(new Geofence.Builder()
                        .setRequestId(temp)
                        .setCircularRegion(
                                latList.get(i),
                                lngList.get(i),
                                Float.parseFloat(Double.toString(radiusList.get(i)))  // wouldn't let me do (float) radiusList.get(i)
                        )
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                Geofence.GEOFENCE_TRANSITION_EXIT)
                        .build());
            }

        circle(Map, 0, 0); // Placed here because above circles would not respond to touch unless a circle method was called
    }


    @Override
    protected void onCreate(@ Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //shref = getSharedPreferences("MyPref", Context.MODE_PRIVATE);


        // initializes the variables to the needed circle data of the previous uses of the app
        TinyDB latDB = new TinyDB(MapActivity.this);
        TinyDB lngDB = new TinyDB(MapActivity.this);
        TinyDB radiusDB = new TinyDB(MapActivity.this);
        TinyDB strokeColorDB = new TinyDB(MapActivity.this);


        setContentView(R.layout.activity_map); // switch

        getLocationPermission();

        searchText = (AutoCompleteTextView) findViewById(R.id.inputSearch);

        gps = (ImageView) findViewById(R.id.gps_icon);

        add = (ImageView) findViewById(R.id.add_btn);

        remove = (ImageView) findViewById(R.id.remove_btn);

        expand = (ImageView) findViewById(R.id.expand_icon);

        shrink = (ImageView) findViewById(R.id.shrink_icon);

        history  = (ImageView) findViewById(R.id.ic_history);

        mgr = (LocationManager) getSystemService(LOCATION_SERVICE);

        dumpProviders();

        Criteria criteria = new Criteria();

        best = mgr.getBestProvider(criteria, true);
        Location location = mgr.getLastKnownLocation(best);
        dumpLocation(location);


        geofencingClient = LocationServices.getGeofencingClient(MapActivity.this);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !notificationManager.isNotificationPolicyAccessGranted()) {

            Intent intent = new Intent(
                    android.provider.Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

            startActivity(intent);
        }



        //initializes are lists with the data retrieved from previous uses
        latList = latDB.getListDouble(keyLat);
        lngList =lngDB.getListDouble(keyLng);
        radiusList = radiusDB.getListDouble(keyRadius);
        strokeColorList = strokeColorDB.getListInt(keystrokeColor);

    }




    @Override
    public void onLocationChanged(Location location) {
        dumpLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    @Override
    protected void onPause() {

        super.onPause();
        mgr.removeUpdates(this);


        // Not needed
        TinyDB latDB = new TinyDB((MapActivity.this));
        TinyDB lngDB = new TinyDB((MapActivity.this));
        TinyDB radiusDB = new TinyDB((MapActivity.this));
        TinyDB strokecolorDB = new TinyDB((MapActivity.this));


        latDB.putListDouble(keyLat, latList);
        lngDB.putListDouble(keyLng, lngList);
        radiusDB.putListDouble(keyRadius, radiusList);
        strokecolorDB.putListInt(keystrokeColor, strokeColorList);

    }

    @Override
    protected void onResume() {

        super.onResume();
        mgr.requestLocationUpdates(best, 15000, 1, this);
    }



    private void dumpLocation(Location locale) {

        if (locale == null) {

            myLocationLatitude = 0.0;
            myLocationLongitude = 0.0;
        } else {

            myLocationLatitude = locale.getLatitude();    // needed to determine whether a circle will trigger when touched if user is within quiet zone
            myLocationLongitude = locale.getLongitude();
        }
    }

    private void dumpProviders() {

        List<String> providers = mgr.getAllProviders();
        for (String p : providers) {

            dumpProviders(p);
        }
    }

    private void dumpProviders(String s) {

        LocationProvider info = mgr.getProvider(s);
        StringBuilder builder = new StringBuilder();
        builder.append("name: ").append(info.getName());
    }



    private void init()   // function that reinitializes the map when the user searches for a location
    {
        Log.d(TAG, "init: initializing");


        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {

            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent)
            {
                if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == keyEvent.ACTION_DOWN || keyEvent.getAction() == keyEvent.KEYCODE_ENTER)
                {

                    // execute our method for searching
                    geoLocate();
                }

                return false;
            }
        });


        // moves map to user's location
        gps.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });



        // allows for the addition of new quiet zone
        add.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
                Log.d(TAG, "onClick: clicked add btn");

                if(addOn == true) {
                    addOn = false;
                    add.setColorFilter(black);

                    Map.setOnMapClickListener(null);
                }
                else {
                    addOn = true;
                    add.setColorFilter(green);

                    Map.setOnMapClickListener(new OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng touch) {
                            Log.d(TAG,"Map clicked [" + touch.latitude + " / " + touch.longitude + "]");
                            //Then passing LatLng to circle activity
                            circle(Map, touch.latitude, touch.longitude);
                        }
                    });

                    removeOn = false;
                    remove.setColorFilter(black);

                    expandOn = false;
                    expand.setColorFilter(black);

                    shrinkOn = false;
                    shrink.setColorFilter(black);
                }
            }
        });


        // allows for the emoval of quiet zones
        remove.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
                Log.d(TAG, "onClick: clicked remove btn");

                if(removeOn == true) {
                    removeOn = false;
                    remove.setColorFilter(black);
                }
                else {
                    removeOn = true;
                    remove.setColorFilter(red);

                    addOn = false;
                    add.setColorFilter(black);
                    Map.setOnMapClickListener(null);

                    expandOn = false;
                    expand.setColorFilter(black);

                    shrinkOn = false;
                    shrink.setColorFilter(black);
                }
            }
        });



        //allows the the expansion of quiet zones
        expand.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
                Log.d(TAG, "onClick: clicked expand icon");

                if(expandOn == true) {
                    expandOn = false;
                    expand.setColorFilter(black);
                }
                else {
                    expandOn = true;
                    expand.setColorFilter(purple);

                    shrinkOn = false;
                    shrink.setColorFilter(black);

                    removeOn = false;
                    remove.setColorFilter(black);

                    addOn = false;
                    add.setColorFilter(black);
                    Map.setOnMapClickListener(null);
                }
            }
        });


        //allows the the reduction of quiet zones
        shrink.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
                Log.d(TAG, "onClick: clicked shrink icon");

                if(shrinkOn == true) {
                    shrinkOn = false;
                    shrink.setColorFilter(black);
                }
                else {
                    shrinkOn = true;
                    shrink.setColorFilter(purple);

                    expandOn = false;
                    expand.setColorFilter(black);

                    removeOn = false;
                    remove.setColorFilter(black);

                    addOn = false;
                    add.setColorFilter(black);
                    Map.setOnMapClickListener(null);
                }
            }
        });


        // work in progress
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
             {


             }

        });

       // hideAway();
    }


    private void geoLocate()  // allows user sto type in address and move map to that location
    {
        Log.d(TAG, "geoLocate: Geolocating");

        String searchString = searchText.getText().toString();
        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();

        try
        {
            list = geocoder.getFromLocationName(searchString, 3);
        }

        catch(IOException e)  // try in case we don't get a result
        {
            Log.d(TAG, "geoLocate: IOException: " + e.getMessage());

        }


        if(list.size() > 0) // if more than 0 hits, sets the first hit in our search to address
        {
            Address address = list.get(0);
            Log.d(TAG, "geoLocate: Found at location: " + address.toString());


            moveCam(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_Z, address.getAddressLine(0));
        }
    }


    private void getDeviceLocation()   // gets
    {
        Log.d(TAG, "getDeviceLocation: getting the device's current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try
        {
            if(LocationPermissionGranted)
            {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task)
                    {
                        if(task.isSuccessful())
                        {
                            Log.d(TAG, "onComplete: found location");
                            Location currentLocation = (Location) task.getResult();

                            moveCam(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_Z, "My Location");
                        }

                        else
                        {
                            Log.d(TAG, "onComplete: current location not found");
                            Toast.makeText(MapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        catch(SecurityException e)
        {
            Log.d(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());

        }



    }

    private void moveCam(LatLng latitudeLongitude, float zoom, String title)
    {
        Log.d(TAG, "moveCam: moving camera to: lat: " + latitudeLongitude.latitude + ", lng: " + latitudeLongitude.longitude);
        Map.moveCamera(CameraUpdateFactory.newLatLngZoom(latitudeLongitude, zoom)); // moves map  camera to recieved latitudeLongitude

        if(title.equals("My Location")) // doesn't put marker on user's location
        {

        }
        else
        {
            if(marker != null)
                marker.remove();


            MarkerOptions options = new MarkerOptions() // create marker with set name and location
                    .position(latitudeLongitude)
                    .title(title);

            marker = Map.addMarker(options); // put said marker on the map
        }

        //hideAway();
    }

    private void initMap()
    {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapActivity.this);

    }



    // creates circles at passed in lat/lng on the map
    private void circle(GoogleMap googleMap, double Lat, double Lng)
    {

        CircleOptions options = new CircleOptions()
                .center(new LatLng(Lat, Lng))
                .radius(30)
                .strokeWidth(10)
                .strokeColor(Color.GREEN)
                .fillColor(Color.argb(128, 255, 0, 0))
                .clickable(true);

        temp = Double.toString(Lat) + "/" + Double.toString(Lng);   // unique string to pair each circle with their underlying geofence


        geofenceList.add(new Geofence.Builder()
                .setRequestId(temp)
                .setCircularRegion(
                        Lat,
                        Lng,
                        30
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());

        circ.add(Map.addCircle(options));
        // geofenceList.




        // adds circle data to the lists that need to be saved
        latList.add(circ.get(circ.size() - 1).getCenter().latitude);
        lngList.add(circ.get(circ.size() - 1).getCenter().longitude);
        radiusList.add(circ.get(circ.size() - 1).getRadius());
        strokeColorList.add(circ.get(circ.size() - 1).getStrokeColor());

        // create storage location for each of those data sets
        TinyDB latDB = new TinyDB((MapActivity.this));
        TinyDB lngDB = new TinyDB((MapActivity.this));
        TinyDB radiusDB = new TinyDB((MapActivity.this));
        TinyDB strokecolorDB = new TinyDB((MapActivity.this));

        // stores the data into those db using a unique key and the list of specified type
        latDB.putListDouble(keyLat, latList);
        lngDB.putListDouble(keyLng, lngList);
        radiusDB.putListDouble(keyRadius, radiusList);
        strokecolorDB.putListInt(keystrokeColor, strokeColorList);


        geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener(MapActivity.this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Geofences sucessfully added");
                    }
                })
                .addOnFailureListener(MapActivity.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, " Failed to add geofences");
                    }
                });


        googleMap.setOnCircleClickListener(new OnCircleClickListener() {

            @Override
            public void onCircleClick(Circle circle) {
                addOn = false;
                add.setColorFilter(black);

               Map.setOnMapClickListener(null); // just prevents any actions on the map from firing when circles are touched


                // changes circles circumference color when clicked
                if(removeOn == false && expandOn == false && shrinkOn == false) {

                    if(circle.getStrokeColor() == green)
                        strokeColor = yellow;
                    else if(circle.getStrokeColor() == yellow)
                        strokeColor = red;
                    else if(circle.getStrokeColor() == red)
                        strokeColor = green;

                    circle.setStrokeColor(strokeColor);

                    // updates the corresponding index's color also
                    for(int i = 0; i < circ.size(); i++)
                        if(circle.getCenter().longitude == circ.get(i).getCenter().longitude && circle.getCenter().latitude == circ.get(i).getCenter().latitude)
                        {
                            strokeColorList.set(i, strokeColor);
                        }
                }



                else if(removeOn == true)
                {

                    String temp = Double.toString(circle.getCenter().latitude) + "/" + Double.toString(circle.getCenter().longitude);


                    // uses the touched circle's string coverted lat/lng to remove corresponding geofence
                    for(int i = 0; i < geofenceList.size(); i++)
                        if(temp.equals(geofenceList.get(i).getRequestId()))
                        { geofenceList.remove(i); Toast.makeText(MapActivity.this, "Removed!", Toast.LENGTH_SHORT).show();}


                        //remove circle object at with corresponding lat/lng
                    for(int i = 0; i < circ.size(); i++)
                        if(circle.getCenter().longitude == circ.get(i).getCenter().longitude && circle.getCenter().latitude == circ.get(i).getCenter().latitude)
                            circ.get(i).remove();


                        // remove data sets at with corresponding lat/lng
                        for(int i = 0; i < latList.size(); i++)
                            if(circle.getCenter().latitude == latList.get(i) && circle.getCenter().longitude == lngList.get(i))
                            {
                                latList.remove(i);
                                lngList.remove(i);
                                radiusList.remove(i);
                                strokeColorList.remove(i);
                            }


                    circle.remove();
                }
                else if(expandOn == true)
                {
                    String temp = Double.toString(circle.getCenter().latitude) + "/" + Double.toString(circle.getCenter().longitude);

                    for(int i = 0; i < geofenceList.size(); i++)
                        if(temp.equals(geofenceList.get(i).getRequestId()))
                        { geofenceList.remove(i); Toast.makeText(MapActivity.this, "Grew", Toast.LENGTH_SHORT).show();}

                                circle.setRadius(circle.getRadius() + 10);

                    for(int i = 0; i < circ.size(); i++)
                        if(circle.getCenter().longitude == circ.get(i).getCenter().longitude && circle.getCenter().latitude == circ.get(i).getCenter().latitude)
                            circ.get(i).setRadius(circle.getRadius());

                    geofenceList.add(new Geofence.Builder()
                            // Set the request ID of the geofence. This is a string to identify this
                            // geofence.
                            .setRequestId(temp)

                            .setCircularRegion(
                                    circle.getCenter().latitude,
                                    circle.getCenter().longitude,
                                    (float) circle.getRadius()
                            )
                            .setExpirationDuration(Geofence.NEVER_EXPIRE)
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                    Geofence.GEOFENCE_TRANSITION_EXIT)
                            .build());

                    for(int i = 0; i < circ.size(); i++)
                        if(circle.getCenter().longitude == circ.get(i).getCenter().longitude && circle.getCenter().latitude == circ.get(i).getCenter().latitude)
                        {
                            radiusList.set(i, circle.getRadius());
                        }


                }

                else if(shrinkOn == true && circle.getRadius()!= 10)
                {
                    String temp = Double.toString(circle.getCenter().latitude) + "/" + Double.toString(circle.getCenter().longitude);

                    for(int i = 0; i < geofenceList.size(); i++)
                        if(temp.equals(geofenceList.get(i).getRequestId()))
                        { geofenceList.remove(i); Toast.makeText(MapActivity.this, "Shrank", Toast.LENGTH_SHORT).show();}

                    circle.setRadius(circle.getRadius() - 10);

                    for(int i = 0; i < circ.size(); i++)
                        if(circle.getCenter().longitude == circ.get(i).getCenter().longitude && circle.getCenter().latitude == circ.get(i).getCenter().latitude)
                            circ.get(i).setRadius(circle.getRadius());

                    geofenceList.add(new Geofence.Builder()
                            // Set the request ID of the geofence. This is a string to identify this
                            // geofence.
                            .setRequestId(temp)

                            .setCircularRegion(
                                    circle.getCenter().latitude,
                                    circle.getCenter().longitude,
                                    (float) circle.getRadius()
                            )
                            .setExpirationDuration(Geofence.NEVER_EXPIRE)
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                    Geofence.GEOFENCE_TRANSITION_EXIT)
                            .build());


                    for(int i = 0; i < circ.size(); i++)
                        if(circle.getCenter().longitude == circ.get(i).getCenter().longitude && circle.getCenter().latitude == circ.get(i).getCenter().latitude)
                        {
                            radiusList.set(i, circle.getRadius());
                        }
                }



                    // Not needed
                /*Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                                                   @Override
                                                   public void onComplete(@NonNull Task task) {
                                                       if (task.isSuccessful()) {
                                                           Log.d(TAG, "onComplete: found location");
                                                           Location currentLocation = (Location) task.getResult();
                                                           myLocationLatitude = currentLocation.getLatitude();
                                                           myLocationLongitude = currentLocation.getLongitude();
                                                       }
                                                   }
                                               });*/


                float[] distance = new float[1];

                // gives us the distance between the user's location and the center of a circle..
                Location.distanceBetween(myLocationLatitude, myLocationLongitude, circle.getCenter().latitude,circle.getCenter().longitude,distance);


                //and if its less than the circle radius the following will trigger depending on the circle's strokeColor
                if ( distance[0] <= circle.getRadius() && strokeColor == yellow && removeOn == false && expandOn == false && shrinkOn == false)
                {
                    Toast.makeText(MapActivity.this, "VIBRATE", Toast.LENGTH_LONG).show();
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                }
                else if(distance[0] <= circle.getRadius() && strokeColor == red && removeOn == false && expandOn == false && shrinkOn == false)
                {
                    Toast.makeText(MapActivity.this, "SILENT", Toast.LENGTH_LONG).show();
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                }

                else if(distance[0] <= circle.getRadius() && strokeColor == green && removeOn == false && expandOn == false && shrinkOn == false)
                {
                    Toast.makeText(MapActivity.this, "NORMAL", Toast.LENGTH_LONG).show();
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                }

            }
        });
    }



    // builds the geofence
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofences(geofenceList);
        return builder.build();
    }


    // sends intent triggered by when when user enters or leaves quiet zone
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }

        Intent intent = new Intent(MapActivity.this, GeofenceTransitionsIntentService.class); /// remember to fix up dashboard intent
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        geofencePendingIntent = PendingIntent.getService(MapActivity.this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }




    private void getLocationPermission()   // first ask user for permission to access their location
    {
        Log.d(TAG, "getLocationPermission:geting location permissions");
        String[] permission = {FINE_LOCATION, COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(MapActivity.this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            if(ContextCompat.checkSelfPermission(MapActivity.this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                LocationPermissionGranted = true;
                initMap();
            }

            else
            {
                ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }

        else
        {
            ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Log.d(TAG, "onRequestPermissionsResult: called");
        LocationPermissionGranted = false;

        switch(requestCode)
        {
            case LOCATION_PERMISSION_REQUEST_CODE:
            {
                if(grantResults.length > 0)
                {
                    for(int i = 0; i < grantResults.length; i++)
                    {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED)
                        {
                            LocationPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }

                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    LocationPermissionGranted = true;
                    // initialize our map
                    initMap();
                }
            }
        }
    }



   /* private void hideAway()  // suppose to hide key pad when location is entered
    {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }*/
}
