package team7.voluntime.Activities;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import team7.voluntime.Domains.Event;
import team7.voluntime.R;
import team7.voluntime.Utilities.Utilities;

public class LocationMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int DEFAULT_ZOOM = 15;
    private final String TAG = "LocationMapActivity";
    private Toolbar toolbar;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private LatLng mDefaultLocation = new LatLng(-33.86, 151.2);
    private FirebaseUser mUser;
    private FirebaseDatabase database;
    private DatabaseReference eventsReference;
    private DatabaseReference charityReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_map);

        toolbar = findViewById(R.id.mapLocationToolbar);
        toolbar.setTitle("Events Map");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
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

    public void addMarker(Event e, String Id) {
        final Event event = e;
        database = FirebaseDatabase.getInstance();
        eventsReference = Utilities.getCharityReference(database, event.getOrganisers());
        eventsReference.child("Events").child(Id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.getValue().toString().equals("previous")) {
                    final String title = event.getTitle();
                    final String coords[] = event.getLocation().split(" ");
                    LatLng eventLoc = new LatLng(
                            Double.parseDouble(coords[0]),
                            Double.parseDouble(coords[1]));
                    mMap.addMarker(new MarkerOptions().position(eventLoc).title(title));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(eventLoc));
                    Toast.makeText(getBaseContext(), dataSnapshot.getValue().toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }



}
