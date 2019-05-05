package team7.voluntime.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team7.voluntime.Domains.Event;
import team7.voluntime.Domains.Volunteer;
import team7.voluntime.Fragments.Charities.CharityViewEventsFragment;
import team7.voluntime.R;
import team7.voluntime.Utilities.Utilities;
import team7.voluntime.Utilities.VolunteerListAdapter;

public class EventDetailsActivity extends AppCompatActivity {

    private String[] coords;
    private FirebaseDatabase mDatabase;
    private DatabaseReference volunteersReference;
    private DatabaseReference eventVolunteersReference;
    private ListView pendingVolunteersLV;
    private ListView registeredVolunteersLV;
    private Event event;

    @BindView(R.id.eventDetailsTitleTV)
    TextView titleTV;
    @BindView(R.id.eventDetailsDescriptionTV)
    TextView descriptionTV;
    @BindView(R.id.eventDetailsCategoryTV)
    TextView categoryTV;
    @BindView(R.id.eventDetailsMinimumTV)
    TextView minimumTV;
    @BindView(R.id.eventDetailsMaximumTV)
    TextView maximumTV;
    @BindView(R.id.eventDetailsLocationTV)
    TextView locationTV;
    @BindView(R.id.eventDetailsDateTV)
    TextView dateTV;
    @BindView(R.id.eventDetailsCreatedTimeTV)
    TextView createdTimeTV;
    @BindView(R.id.eventDetailsMapIV)
    ImageView mapIV;
    @BindView(R.id.eventDetailsPendingVolunteersLabelTV)
    TextView pendingVolunteersLabelTV;
    @BindView(R.id.eventDetailsPendingVolunteersTV)
    TextView pendingVolunteersTV;
    @BindView(R.id.eventDetailsRegisteredVolunteersLabelTV)
    TextView registeredVolunteersLabelTV;
    @BindView(R.id.eventDetailsRegisteredVolunteersTV)
    TextView registeredVolunteersTV;

    private final static String TAG = "EventDetails";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        event = (Event) intent.getParcelableExtra("event");
        pendingVolunteersLV = (ListView) findViewById(R.id.eventPendingVolunteersLV);
        registeredVolunteersLV = (ListView) findViewById(R.id.eventRegisteredVolunteersLV);

        coords = event.getLocation().split(" ");

        titleTV.setText(event.getTitle());
        descriptionTV.setText(event.getDescription());
        categoryTV.setText(event.getCategory());
        minimumTV.setText(event.getMinimum() + "");
        maximumTV.setText(event.getMaximum() + "");
        dateTV.setText(event.getDate());
        createdTimeTV.setText(event.getCreatedTime());
        String address = "Location Unavailable";
        try {
             address = Utilities.getLocation(
                    this,
                    Double.parseDouble(coords[0]),
                    Double.parseDouble(coords[1]))
                    .get(0).getAddressLine(0);
             mapIV.setVisibility(View.VISIBLE);
        } catch(IndexOutOfBoundsException e) {
            Log.e(TAG, "An error occurred when passing location coords: " + event.getLocation());
            Log.e(TAG, e.toString());
        }
        locationTV.setText(address);


        if (intent.getStringExtra("parentActivity").equals(VolunteerDetailsActivity.class.toString())) {
            pendingVolunteersLabelTV.setVisibility(View.INVISIBLE);
            pendingVolunteersTV.setVisibility(View.INVISIBLE);
            registeredVolunteersLabelTV.setVisibility(View.INVISIBLE);
            registeredVolunteersTV.setVisibility(View.INVISIBLE);
        } else if (intent.getStringExtra("parentActivity").equals(CharityViewEventsFragment.class.toString())) {
            mDatabase = FirebaseDatabase.getInstance();
            volunteersReference = mDatabase.getReference().child("Volunteers");
            eventVolunteersReference = mDatabase.getReference().child("Events").child(event.getId()).child("Volunteers");
            setVolunteers();
        };

    }

    private void setVolunteers() {
        final ArrayList<Volunteer> pendingVolunteersList = new ArrayList<>();
        final ArrayList<Volunteer> registeredVolunteersList = new ArrayList<>();
        final VolunteerListAdapter pendingVolunteersAdapter = new VolunteerListAdapter(this,  R.layout.adapter_view_pending_volunteer_layout, pendingVolunteersList, this);
        final VolunteerListAdapter registeredVolunteersAdapter = new VolunteerListAdapter(this,  R.layout.adapter_view_registered_volunteer_layout, registeredVolunteersList, this);
        pendingVolunteersLV.setAdapter(pendingVolunteersAdapter);
        registeredVolunteersLV.setAdapter(registeredVolunteersAdapter);
        eventVolunteersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pendingVolunteersList.clear();
                registeredVolunteersList.clear();

                // These is needed to stop application crashing when either list is updated and becomes empty
                pendingVolunteersAdapter.notifyDataSetChanged();
                registeredVolunteersAdapter.notifyDataSetChanged();

                for (final DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.exists()) {
                        volunteersReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(child.getKey())) {
                                    Volunteer tempVolunteer = dataSnapshot.child(child.getKey()).child("Profile").getValue(Volunteer.class);
                                    tempVolunteer.setId(child.getKey());
                                    if (child.getValue().toString().equals("pending")) {
                                        if (pendingVolunteersTV.getVisibility() != View.INVISIBLE) {
                                            pendingVolunteersTV.setVisibility(View.INVISIBLE);
                                        }
                                        pendingVolunteersList.add(tempVolunteer);
                                        pendingVolunteersLV.invalidateViews();
                                        Utilities.setDynamicHeight(pendingVolunteersLV);
                                        if (registeredVolunteersList.isEmpty()) {
                                            registeredVolunteersTV.setVisibility(View.VISIBLE);
                                        }

                                    } else if (child.getValue().toString().equals("registered")) {
                                        if (registeredVolunteersTV.getVisibility() != View.INVISIBLE) {
                                            registeredVolunteersTV.setVisibility(View.INVISIBLE);
                                        }
                                        registeredVolunteersList.add(tempVolunteer);
                                        registeredVolunteersLV.invalidateViews();
                                        Utilities.setDynamicHeight(registeredVolunteersLV);
                                        if (pendingVolunteersList.isEmpty()) {
                                            pendingVolunteersTV.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }

                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public String getEventId() {
        return event.getId();
    }

    public DatabaseReference getDatabaseReference() {
        return mDatabase.getReference();
    }

    @OnClick(R.id.eventDetailsMapIV)
    public void mapOnClick() {
        Intent intent = new Intent(this, LocationActivity.class);
        intent.putExtra("latitude", Double.parseDouble(coords[0]));
        intent.putExtra("longitude", Double.parseDouble(coords[1]));
        intent.putExtra("address", locationTV.getText().toString());
        startActivity(intent);
    }

    @OnClick(R.id.eventDetailsBackTV)
    public void backButtonOnClick() {
        finish();
    }

}
