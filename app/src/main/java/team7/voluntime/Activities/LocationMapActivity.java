package team7.voluntime.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import team7.voluntime.Domains.Event;
import team7.voluntime.R;
import team7.voluntime.Utilities.MapModels.LocationDefaults;
import team7.voluntime.Utilities.Utilities;

public class LocationMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private final String TAG = "LocationMapActivity";
    private Toolbar toolbar;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private LatLng mDefaultLocation = new LatLng(-33.86, 151.2);
    private FirebaseDatabase database;
    private DatabaseReference eventsReference;
    private Button viewEventLocationBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_map);

        toolbar = findViewById(R.id.mapLocationToolbar);
        toolbar.setTitle("Events Map");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        viewEventLocationBtn = findViewById(R.id.viewEventLocationBtn);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();

        database = FirebaseDatabase.getInstance();
        eventsReference = Utilities.getEventsReference(database);
        eventsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String Id = child.getKey();
                    Event event = child.getValue(Event.class);
                    addMarker(event, Id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void addMarker(Event t, String Id) {
        final Event eventIn = t;


        database = FirebaseDatabase.getInstance();
        eventsReference = Utilities.getCharityReference(database, eventIn.getOrganisers());
        eventsReference.child("Events").child(Id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.getValue().toString().equals("previous")) {
                    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            viewEventLocationBtn.setVisibility(View.INVISIBLE);
                        }
                    });
                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        String title = eventIn.getTitle();
                        String coords[] = eventIn.getLocation().split(" ");
                        String date = eventIn.getDate();
                        LatLng eventLoc = new LatLng(
                                Double.parseDouble(coords[0]),
                                Double.parseDouble(coords[1]));
                        Marker marker = mMap.addMarker(new MarkerOptions().position(eventLoc).title(title).snippet(date)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                        @Override
                        public boolean onMarkerClick(final Marker marker) {
                            final Marker mark = marker;
                            database = FirebaseDatabase.getInstance();
                            eventsReference = Utilities.getEventsReference(database);
                            eventsReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                                        String Id = child.getKey();
                                        Event e = child.getValue(Event.class);

                                        final Event event = new Event(Id, e.getTitle(), e.getDescription(), e.getCategory(),
                                                e.getLocation(), e.getDate(), e.getStartTime(),
                                                e.getEndTime(), e.getCreatedTime(), e.getOrganisers(),
                                                e.getMinimum(), e.getMaximum(), e.getVolunteers());

                                        if (mark.getTitle().equals(e.getTitle()) && mark.getSnippet().equals(event.getDate())) {
                                            viewEventLocationBtn.setVisibility(View.VISIBLE);
                                            viewEventLocationBtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    viewEventLocationBtn.setVisibility(View.GONE);
                                                    Intent intent = new Intent(getBaseContext(), EventRegisterActivity.class);
                                                    intent.putExtra("event", event);
                                                    getBaseContext().startActivity(intent);
                                                    finish();
                                                }
                                            });
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                            return false;
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
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
                                mMap.moveCamera(CameraUpdateFactory
                                        .newLatLngZoom(mDefaultLocation, 10));
                                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                            } else {
                                LatLng userLoc = new LatLng(
                                        mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude());
                                mMap.moveCamera(CameraUpdateFactory
                                        .newLatLngZoom(userLoc, 10));
                            }

                        } else {
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
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
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
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
