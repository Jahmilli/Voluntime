package team7.voluntime.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team7.voluntime.Domains.Event;
import team7.voluntime.Domains.Volunteer;
import team7.voluntime.Fragments.Charities.CharityViewEventsFragment;
import team7.voluntime.R;
import team7.voluntime.Utilities.Constants;
import team7.voluntime.Utilities.Utilities;
import team7.voluntime.Utilities.VolunteerListAdapter;

public class EventDetailsActivity extends AppCompatActivity {

    private String[] coords;
    private FirebaseDatabase mDatabase;
    private DatabaseReference volunteersReference;
    private DatabaseReference eventVolunteersReference;
    ArrayList<Volunteer> pendingVolunteersList;
    ArrayList<Volunteer> registeredVolunteersList;
    private ListView pendingVolunteersLV;
    private ListView registeredVolunteersLV;
    private Event event;

    @BindView(R.id.eventDetailsTitleTV)
    TextView titleTV;
    @BindView(R.id.eventDetalsConcludeEventTV)
    TextView concludeEventTV;
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
        event = intent.getParcelableExtra("event");

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date eventDate = format.parse(event.getDate());
            Date currentDate = format.parse(Utilities.getCurrentDate());
            if (eventDate.compareTo(currentDate) <= 0) {
                concludeEventTV.setVisibility(View.VISIBLE);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        pendingVolunteersLV = findViewById(R.id.eventPendingVolunteersLV);
        registeredVolunteersLV = findViewById(R.id.eventRegisteredVolunteersLV);

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


        // Check if event details is being viewed as a charity or something else
        if (intent.getStringExtra("parentActivity").equals(CharityViewEventsFragment.class.toString())) {
            // TODO: Added in event status need to probably check how this interacts with volunteer page. Check for null etc
            if (intent.getStringExtra("eventStatus") != null && intent.getStringExtra("eventStatus").equals("previous")) {

            }
            mDatabase = FirebaseDatabase.getInstance();
            volunteersReference = mDatabase.getReference().child("Volunteers");
            eventVolunteersReference = mDatabase.getReference().child("Events").child(event.getId()).child("Volunteers");
            setVolunteers();
        } else {
            pendingVolunteersLabelTV.setVisibility(View.INVISIBLE);
            pendingVolunteersTV.setVisibility(View.INVISIBLE);
            registeredVolunteersLabelTV.setVisibility(View.INVISIBLE);
            registeredVolunteersTV.setVisibility(View.INVISIBLE);
        }

    }

    private void setVolunteers() {
        pendingVolunteersList = new ArrayList<>();
        registeredVolunteersList = new ArrayList<>();
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
                                    if (child.getValue().toString().equals(Constants.EVENT_PENDING)) {
                                        if (pendingVolunteersTV.getVisibility() != View.INVISIBLE) {
                                            pendingVolunteersTV.setVisibility(View.INVISIBLE);
                                        }
                                        pendingVolunteersList.add(tempVolunteer);
                                        pendingVolunteersLV.invalidateViews();
                                        Utilities.setDynamicHeight(pendingVolunteersLV);
                                        if (registeredVolunteersList.isEmpty()) {
                                            registeredVolunteersTV.setVisibility(View.VISIBLE);
                                        }

                                    } else if (child.getValue().toString().equals(Constants.EVENT_REGISTERED)) {
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


    public Event getEvent() {
        return event;
    }

    public DatabaseReference getDatabaseReference() {
        return mDatabase.getReference();
    }

    // Sets the event to previous for the charity and all volunteers registered with that event.
    private void concludeEvent() {
        mDatabase.getReference()
                .child("Charities")
                .child(event.getOrganisers())
                .child("Events")
                .child(event.getId())
                .setValue(Constants.EVENT_PREVIOUS);

        mDatabase.getReference().child("Volunteers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot volunteer : dataSnapshot.getChildren()) {
                        DataSnapshot snapshotValue = volunteer.child("Events").child(event.getId());
                        // Check if null AND check if the user was actually registered for the event
                        if (snapshotValue.getValue() != null && snapshotValue.getValue().toString().equals(Constants.EVENT_REGISTERED)) {
                            mDatabase.getReference()
                                    .child("Volunteers")
                                    .child(volunteer.getKey())
                                    .child("Events")
                                    .child(event.getId())
                                    .setValue(Constants.EVENT_PREVIOUS);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

    @OnClick(R.id.eventDetalsConcludeEventTV)
    public void concludeOnClick() {
        final android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Event Conclusion");
        builder.setMessage("Are you sure you wish to conclude the event?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (pendingVolunteersList.size() > 0) {
                    Toast.makeText(EventDetailsActivity.this, "Please add or remove the pending volunteers", Toast.LENGTH_SHORT).show();
                } else {
                    concludeEvent();
                    Toast.makeText(EventDetailsActivity.this, "Event has conclued, congratulations!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.show();
    }

}
