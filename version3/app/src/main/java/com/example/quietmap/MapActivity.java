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

import com.example.quietmap.PlaceAutocompleteAdapter;

//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.GoogleMap;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

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

    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_Z = 15f;

    //widgets
    private AutoCompleteTextView searchText;
    private ImageView gps;


    //variables
    private Boolean LocationPermissionGranted = false;
    private GoogleMap Map;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;

    @Override
    protected void onCreate(@ Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        searchText = (AutoCompleteTextView) findViewById(R.id.inputSearch);

        getLocationPermission();

        gps = (ImageView) findViewById(R.id.gps_icon);
    }

    private void init()
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
        gps.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
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
            MarkerOptions options = new MarkerOptions() // create marker with set name and location
                    .position(latitudeLongitude)
                    .title(title);

            Map.addMarker(options); // put said marker on the map       customize marker
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
