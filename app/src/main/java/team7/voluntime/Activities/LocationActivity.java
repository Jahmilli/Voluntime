package team7.voluntime.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

import team7.voluntime.R;
import team7.voluntime.Utilities.MapModels.LocationDefaults;
import team7.voluntime.Utilities.Utilities;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Toolbar toolbar;
    private static final int DEFAULT_ZOOM = 15;

    private AlertDialog.Builder helpAlertBuilder;
    private AlertDialog helpAlert;
    private GoogleMap mGoogleMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Button setEventLocationBtn;

    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private LatLng mDefaultLocation = new LatLng(-33.86, 151.2);
    private LatLng latLng;

    // Only used when viewing existing event
    private double latitude = 0;
    private double longitude = 0;
    private String address = "";

    private final String TAG = "LocationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        toolbar = findViewById(R.id.locationToolbar);
        toolbar.setTitle("Event Location");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        setEventLocationBtn = findViewById(R.id.setEventLocationBtn);

        createHelpDialog();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("latitude", 0);
        longitude = getIntent().getDoubleExtra("longitude", 0);
        address = getIntent().getStringExtra("address");


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.getUiSettings().setRotateGesturesEnabled(false);
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();

        if (latitude > 0 || longitude > 0) {
            Log.d(TAG, "Setting event location, lat is: " + latitude + " long is: " + longitude);
            LatLng eventLocation = new LatLng(latitude, longitude);
            mGoogleMap.addMarker(new MarkerOptions().position(eventLocation).title(address)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLocation, 15));
        } else {
            // Setting a click event handler for the map
            Log.d(TAG, "Setting event handler, lat is: " + latitude + " long is: " + longitude);
            setEventLocation();

        }


    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation == null) {
                                mGoogleMap.moveCamera(CameraUpdateFactory
                                        .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                            } else {
                                LatLng userLoc = new LatLng(
                                        mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude());
                                mGoogleMap.moveCamera(CameraUpdateFactory
                                        .newLatLngZoom(userLoc, 10));
                            }

                        } else {
                            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LocationDefaults.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mGoogleMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mGoogleMap.setMyLocationEnabled(false);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    public void setEventLocation() {
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                // Creating a marker
                final MarkerOptions markerOptions = new MarkerOptions();

                // Setting the position for the marker
                markerOptions.position(latLng);

                final List<Address> addresses = Utilities.getLocation(getApplicationContext(), latLng.latitude, latLng.longitude);
                // Setting the title for the marker.
                // This will be displayed on taping the marker
                markerOptions.title(addresses.get(0).getAddressLine(0));

                // Clears the previously touched position
                mGoogleMap.clear();

                // Animating to the touched position
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                Marker marker = mGoogleMap.addMarker(markerOptions);
                marker.showInfoWindow();
                // Placing a marker on the touched position
                setEventLocationBtn.setVisibility(View.VISIBLE);
                setEventLocationBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setEventLocationBtn.setVisibility(View.GONE);
                        // Adds the gps coordinates and address to the create events page
                        Intent intent = new Intent(LocationActivity.this, CreateEventActivity.class);
                        intent.putExtra("longitude", markerOptions.getPosition().longitude);
                        intent.putExtra("latitude", markerOptions.getPosition().latitude);
                        intent.putExtra("address", addresses.get(0).getAddressLine(0));
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });

            }
        });
    }
    
    public void createHelpDialog() {
        helpAlertBuilder = new AlertDialog.Builder(this);
        //TODO: Remove this when viewing events
        helpAlertBuilder.setMessage("Select a location for where your event will be run!");
        helpAlertBuilder.setCancelable(true);
        helpAlertBuilder.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        helpAlert = helpAlertBuilder.create();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_location, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.location_help:
                helpAlert.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LocationDefaults.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }

        }
        updateLocationUI();
    }
}
