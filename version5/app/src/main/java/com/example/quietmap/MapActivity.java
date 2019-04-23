// courtesy Wilgins Mistilien
package com.example.quietmap;

import android.Manifest;
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
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
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
///
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.GoogleMap.OnCircleClickListener;
import com.google.android.gms.maps.model.Circle;
import android.graphics.Color;

import android.location.LocationListener;
import com.google.android.gms.location.GeofencingClient;
import android.location.LocationProvider;
import android.location.Criteria;
import android.location.LocationManager;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
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

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, OnMapLongClickListener {

    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_Z = 15f;
    String string;


    //public final Marker addMarker (MarkerOptions options);

    //widgets
    private AutoCompleteTextView searchText;
    private ImageView gps;
    private ImageView add;
    private ImageView remove;
    private ImageView expand;
    private ImageView shrink;


    //variables
    private Boolean LocationPermissionGranted = false;
    private GoogleMap Map;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;

    private double circleLat;
    private double circleLng;
    private Marker marker;// = Map.addMarker(null);
    private boolean removeOn = false;
    private boolean addOn = false;
    private boolean expandOn = false;
    private boolean shrinkOn = false;
    int black = Color.parseColor("#000000");
    int red = Color.parseColor("#ff0000");
    int green = Color.parseColor("#00ff00");
    int purple = Color.parseColor("#800080");
    int yellow = Color.parseColor("#ffff00");
    public int strokeColor = green;


    double currentLatitudE, currentLongitudE;


    private LocationManager mgr;
    private String best;
    private double myLocationLatitude;
    private  double myLocationLongitude; // https://stackoverflow.com/questions/11251023/how-to-get-latitude-and-longitude-in-android

    private GeofencingClient geofencingClient;
    private ArrayList<Geofence> geofenceList = new ArrayList<>();
    private PendingIntent geofencePendingIntent;

    //private Vector<Circle>
    AudioManager audioManager;// = (AudioManager) getSystemService(Context.AUDIO_SERVICE);




    public void AddColor(String  string)
    {
        Intent intent = new Intent(MapActivity.this, GeofenceTransitionsIntentService.class);
        intent.putExtra("color", string);
        startActivity(intent);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Map = googleMap;
        Log.d(TAG, "OnMapReady: map is ready");

        if (LocationPermissionGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Map.setMyLocationEnabled(true); // Goes to your location when first opened
            Map.getUiSettings().setMyLocationButtonEnabled(false); // get rid of default center button


            init();

        }

    }


    //Map.setOnMapLongClickListener(this);
    //...
    @Override
    public void onMapLongClick(LatLng point) {
        // reverse geocode point
    }


    @Override
    protected void onCreate(@ Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        searchText = (AutoCompleteTextView) findViewById(R.id.inputSearch);

        getLocationPermission();

        gps = (ImageView) findViewById(R.id.gps_icon);

        add = (ImageView) findViewById(R.id.add_btn);

        remove = (ImageView) findViewById(R.id.remove_btn);

        expand = (ImageView) findViewById(R.id.expand_icon);

        shrink = (ImageView) findViewById(R.id.shrink_icon);



        mgr = (LocationManager) getSystemService(LOCATION_SERVICE);

        dumpProviders();

        Criteria criteria = new Criteria();

        best = mgr.getBestProvider(criteria, true);
        Log.d("best provider", best);

        Location location = mgr.getLastKnownLocation(best);
        dumpLocation(location);


        geofencingClient = LocationServices.getGeofencingClient(this);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // ask for help
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !notificationManager.isNotificationPolicyAccessGranted()) {

            Intent intent = new Intent(
                    android.provider.Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

            startActivity(intent);
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        dumpLocation(location);

        //Toast.makeText(MapActivity.this, myLocationLatitude + " WORKS " + myLocationLongitude + "", Toast.LENGTH_LONG).show();


       /* float[] distance = new float[1];

        Circle circle;
        Location.distanceBetween(myLocationLatitude, myLocationLongitude, circle.getCenter().latitude,circle.getCenter().longitude,distance);

        if ( distance[0] <= circle.getRadius() && strokeColor == yellow)
        {
            Toast.makeText(MapActivity.this, "Yellow Works!!!", Toast.LENGTH_LONG).show();
        }
        else if(distance[0] <= circle.getRadius() && strokeColor == red)
        {
            Toast.makeText(MapActivity.this, "Red Works!!!", Toast.LENGTH_LONG).show();
        }*/
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
    }

    @Override
    protected void onResume() {

        super.onResume();
        mgr.requestLocationUpdates(best, 15000, 1, this);
    }



    private void dumpLocation(Location l) {

        if (l == null) {

            myLocationLatitude = 0.0;
            myLocationLongitude = 0.0;
        } else {

            myLocationLatitude = l.getLatitude();
            myLocationLongitude = l.getLongitude();
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



   /* private boolean arePointsNear( Location checkPoint, Location centerPoint, double m) {
        double ky = 40000 / 360;
        double kx = Math.cos(Math.PI * centerPoint.lat / 180.0) * ky;
        double dx = Math.abs(centerPoint.lng - checkPoint.lng) * kx;
        double dy = Math.abs(centerPoint.lat - checkPoint.lat) * ky;
        return Math.sqrt(dx * dx + dy * dy) <= m;
    }*/

    private void init()
    {
        Log.d(TAG, "init: initializing");

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

        gps.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });


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

        hideAway();
    }


    private void geoLocate()
    {
        Log.d(TAG, "geoLocate: geolocating");

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
            Log.d(TAG, "geoLocate: found at location: " + address.toString());

            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();

            circleLat = address.getLatitude();
            circleLng = address.getLongitude();

            moveCam(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_Z, address.getAddressLine(0));
        }
    }


    private void getDeviceLocation()
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

                            circleLat = currentLocation.getLatitude(); // dont need
                            circleLng = currentLocation.getLongitude();
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

        if(title.equals("My Location")) // does nothing
        {

        }
        else
        {
            if(marker != null)
                marker.remove();


            MarkerOptions options = new MarkerOptions() // create marker with set name and location
                    .position(latitudeLongitude)
                    .title(title);

            marker = Map.addMarker(options); // put said marker on the map       customize marker
        }

        hideAway();
    }

    private void initMap()
    {   // android map id in activity_map.xml
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapActivity.this);
        //Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();

    }



    private void circle(GoogleMap googleMap, double Lat, double Lng)
    {
        GoogleMap mMap = googleMap;
        //geofencingClient = LocationServices.getGeofencingClient(MapActivity.this);


        geofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId("geo")

                .setCircularRegion(
                        Lat,
                        Lng,
                        30
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());


        CircleOptions options = new CircleOptions()
                .center(new LatLng(Lat, Lng))
                .radius(30)
                .strokeWidth(10)
                .strokeColor(Color.GREEN)
                .fillColor(Color.argb(128, 255, 0, 0))
                .clickable(true);


        geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener(MapActivity.this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Geofences added
                        Toast.makeText(MapActivity.this, "Woo Hoo", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(MapActivity.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to add geofences
                        Toast.makeText(MapActivity.this, "Noooooooo!", Toast.LENGTH_SHORT).show();
                    }
                });


        mMap.addCircle(options);

       // quietLat.add(Lat);
       // quietLng.add(Lng);

            googleMap.setOnCircleClickListener(new OnCircleClickListener() {

                @Override
                public void onCircleClick(Circle circle) {
                    // Flip the r, g and b components of the circle's
                    // stroke color.

                    if(removeOn == false && expandOn == false && shrinkOn == false) {

                        if(strokeColor == green)
                            strokeColor = yellow;
                        else if(strokeColor == yellow)
                            strokeColor = red;
                        else if(strokeColor == red)
                            strokeColor = green;

                        circle.setStrokeColor(strokeColor);
                    }



                    else if(removeOn == true)
                        circle.remove();

                    else if(expandOn == true)
                        circle.setRadius(circle.getRadius() + 10);

                    else if(shrinkOn == true && circle.getRadius()!= 10)
                        circle.setRadius(circle.getRadius() - 10);


                    /*String color = "";

                    if(strokeColor == green)
                        color = "green";
                    else if(strokeColor == yellow)
                        color = "yellow";
                    else if(strokeColor == red)
                        color = "red";

                    Intent intent = new Intent(MapActivity.this, GeofenceTransitionsIntentService.class);
                    intent.putExtra("color", color);
                    startActivity(intent);*/

                   float[] distance = new float[1];

                    Location.distanceBetween(myLocationLatitude, myLocationLongitude, circle.getCenter().latitude,circle.getCenter().longitude,distance);

                    if ( distance[0] <= circle.getRadius() && strokeColor == yellow)
                    {
                        Toast.makeText(MapActivity.this, "Yellow Works!!!", Toast.LENGTH_LONG).show();
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    }
                    else if(distance[0] <= circle.getRadius() && strokeColor == red)
                    {
                        Toast.makeText(MapActivity.this, "Red Works!!!", Toast.LENGTH_LONG).show();
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    }

                }
            });
    }




    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }


    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class); /// remember to fix up dashboard intent
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        geofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }




    private void getLocationPermission()   // first ask user for permission to access their location
    {
        Log.d(TAG, "getLocationPermission:geting location permissions");
        String[] permission = {FINE_LOCATION, COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
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



    private void hideAway()  // suppose to hide key pad when location is entered
    {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    // find out how we are gooing to check if phone is in area check every


}

