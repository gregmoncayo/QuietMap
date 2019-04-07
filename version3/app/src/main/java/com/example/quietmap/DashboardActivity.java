package com.example.quietmap;

import android.app.Dialog;
import android.widget.Button;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import android.util.Log;
//import com.google.android.maps.GeoPoint;
//import com.google.android.maps.MapView;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.ConnectionResult;
import android.view.View;

public class DashboardActivity extends AppCompatActivity {
    private static final String TAG = "DashboardActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        if(isServicesOK())
        {
            init();
        }
    }




    private void init()
    {
        Button btnBegin = (Button) findViewById(R.id.btnBegin);
        btnBegin.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(DashboardActivity.this, MapActivity.class);
                                            startActivity(intent);
                                        }
                                    }

        );
    }

    public boolean isServicesOK()
    {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(DashboardActivity.this);

        if(available == ConnectionResult.SUCCESS)
        {
            // EVERYTHING is fine and the users can make map requests
            Log.d(TAG, "isServicesOK: Google Play services is working");

            return true;
        }

        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available))
        {
            // a error occured but can be resolved
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            // Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(DashboardActivity.this, ERROR_DIALOG_REQUEST);
            //dialog.show();
            //return false;
        }

        else
        {
            Toast.makeText(this, "You can't make map request", Toast.LENGTH_SHORT).show();

        }

        return false;
    }
}
