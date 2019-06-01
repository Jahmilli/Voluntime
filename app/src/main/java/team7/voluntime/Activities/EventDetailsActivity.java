package team7.voluntime.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.HashMap;
import java.util.Map;

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

    // Not used in past events
    ArrayList<Volunteer> pendingVolunteersList;
    ArrayList<Volunteer> registeredVolunteersList;
    private ListView pendingVolunteersLV;
    private ListView registeredVolunteersLV;

    // Only used for past events
    ArrayList<Volunteer> attendedVolunteersList;
    private ListView attendedVolunteersLV;

    private Event event;
    private boolean isPastEvent;

    @BindView(R.id.eventDetailsTitleTV)
    TextView titleTV;
    @BindView(R.id.eventDetailsCancelEventIV)
    ImageView cancelEventIV;
    @BindView(R.id.eventDetailsEditEventIV)
    ImageView editEventIV;
    @BindView(R.id.eventDetailsConcludeEventBtn)
    Button concludeEventBtn;
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
    @BindView(R.id.eventDetailsStartTimeTV)
    TextView startTimeTV;
    @BindView(R.id.eventDetailsEndTimeTV)
    TextView endTimeTV;
    @BindView(R.id.eventDetailsMapIV)
    ImageView mapIV;
    @BindView(R.id.eventDetailsPendingVolunteersLL)
    LinearLayout pendingVolunteersLL;
    @BindView(R.id.eventDetailsPendingVolunteersTV)
    TextView pendingVolunteersTV;
    @BindView(R.id.eventDetailsRegisteredVolunteersLL)
    LinearLayout registeredVolunteersLL;
    @BindView(R.id.eventDetailsRegisteredVolunteersTV)
    TextView registeredVolunteersTV;
    @BindView(R.id.eventDetailsAttendedVolunteersLL)
    LinearLayout attendedVolunteersLL;
    @BindView(R.id.eventDetailsAttendedVolunteersTV)
    TextView attendedVolunteersTV;

    private final static String TAG = "EventDetails";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        ButterKnife.bind(this);
        mDatabase = FirebaseDatabase.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        Intent intent = getIntent();
        event = intent.getParcelableExtra("event");
        isPastEvent = intent.getBooleanExtra("isPastEvent", false); // Assume all events are upcoming events unless specified

        pendingVolunteersLV = findViewById(R.id.eventPendingVolunteersLV);
        registeredVolunteersLV = findViewById(R.id.eventRegisteredVolunteersLV);
        attendedVolunteersLV = findViewById(R.id.eventAttendedVolunteersLV);

        coords = event.getLocation().split(" ");
        mDatabase.getReference().child("Events").child(event.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    event = dataSnapshot.getValue(Event.class);
                    event.setId(dataSnapshot.getKey());
                    titleTV.setText(event.getTitle());
                    descriptionTV.setText(event.getDescription());
                    categoryTV.setText(event.getCategory());
                    minimumTV.setText(event.getMinimum() + "");
                    maximumTV.setText(event.getMaximum() + "");
                    dateTV.setText(event.getDate());
                    startTimeTV.setText(event.getStartTime());
                    endTimeTV.setText(event.getEndTime());
                    String address = "Location Unavailable";
                    try {
                        address = Utilities.getLocation(
                                EventDetailsActivity.this,
                                Double.parseDouble(coords[0]),
                                Double.parseDouble(coords[1]))
                                .get(0).getAddressLine(0);
                        mapIV.setVisibility(View.VISIBLE);
                    } catch(IndexOutOfBoundsException e) {
                        Log.e(TAG, "An error occurred when passing location coords: " + event.getLocation());
                        Log.e(TAG, e.toString());
                    }
                    locationTV.setText(address);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (intent.getStringExtra("parentActivity").equals(CharityViewEventsFragment.class.toString())) {
            // TODO: Added in event status need to probably check how this interacts with volunteer page. Check for null etc
            if (intent.getStringExtra("eventStatus") != null && intent.getStringExtra("eventStatus").equals("previous")) {

            }

            volunteersReference = mDatabase.getReference().child("Volunteers");
            // TODO: Added in event status need to probably check how this interacts with volunteer page. Check for null etc
            eventVolunteersReference = mDatabase.getReference().child("Events").child(event.getId()).child("Volunteers");

            if (isPastEvent) {
                attendedVolunteersLL.setVisibility(View.VISIBLE);
                setAttendedVolunteers();
            } else {
                try {
                    Date eventDate = format.parse(event.getDate());
                    Date currentDate = format.parse(Utilities.getCurrentDate());
                    if (eventDate.compareTo(currentDate) <= 0) {
                        concludeEventBtn.setVisibility(View.VISIBLE);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                editEventIV.setVisibility(View.VISIBLE);
                cancelEventIV.setVisibility(View.VISIBLE);
                registeredVolunteersLL.setVisibility(View.VISIBLE);
                pendingVolunteersLL.setVisibility(View.VISIBLE);
                setVolunteers();
            }
        }
    }

    // Used when viewing past events. This displays users that attended the event.
    private void setAttendedVolunteers() {
        attendedVolunteersList = new ArrayList<>();
        final VolunteerListAdapter attendedVolunteersAdapter = new VolunteerListAdapter(this,  R.layout.adapter_view_attended_volunteer_layout, attendedVolunteersList, this);
        attendedVolunteersLV.setAdapter(attendedVolunteersAdapter);
        eventVolunteersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // These is needed to stop application crashing when either list is updated and becomes empty
                    attendedVolunteersAdapter.notifyDataSetChanged();

                    for (final DataSnapshot attendedVolunteer : dataSnapshot.getChildren()) {
                        if (attendedVolunteer.exists() && attendedVolunteer.getValue().equals("previous") && !attendedVolunteersList.contains(attendedVolunteer)) {
                            volunteersReference.child(attendedVolunteer.getKey()).child("Profile").addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        if (attendedVolunteersTV.getVisibility() != View.GONE) {
                                            attendedVolunteersTV.setVisibility(View.GONE);
                                        }
                                        Volunteer tempVolunteer = dataSnapshot.getValue(Volunteer.class);
                                        tempVolunteer.setId(attendedVolunteer.getKey());
                                        attendedVolunteersList.add(tempVolunteer);
                                        attendedVolunteersLV.invalidateViews();
                                        Utilities.setDynamicHeight(attendedVolunteersLV);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setVolunteers() {
        pendingVolunteersList = new ArrayList<>();
        registeredVolunteersList = new ArrayList<>();
        final VolunteerListAdapter pendingVolunteersAdapter = new VolunteerListAdapter(this,  R.layout.adapter_view_pending_volunteer_layout, pendingVolunteersList, this);
        final VolunteerListAdapter registeredVolunteersAdapter = new VolunteerListAdapter(this,  R.layout.adapter_view_registered_volunteer_layout, registeredVolunteersList, this);
        pendingVolunteersLV.setAdapter(pendingVolunteersAdapter);
        registeredVolunteersLV.setAdapter(registeredVolunteersAdapter);

        // Contains volunteer ids for the particular event
        eventVolunteersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // These is needed to stop application crashing when either list is updated and becomes empty
                    pendingVolunteersAdapter.notifyDataSetChanged();
                    registeredVolunteersAdapter.notifyDataSetChanged();

                    for (final DataSnapshot child : dataSnapshot.getChildren()) {
                        if (child.exists()) {
                            volunteersReference.child(child.getKey()).child("Profile").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        Volunteer tempVolunteer = dataSnapshot.getValue(Volunteer.class);
                                        tempVolunteer.setId(child.getKey());
                                        if (child.getValue().toString().equals(Constants.EVENT_PENDING) && !pendingVolunteersList.contains(tempVolunteer)) {
                                            if (pendingVolunteersTV.getVisibility() != View.GONE) {
                                                pendingVolunteersTV.setVisibility(View.GONE);
                                            }
                                            pendingVolunteersList.add(tempVolunteer);
                                            pendingVolunteersLV.invalidateViews();
                                            Utilities.setDynamicHeight(pendingVolunteersLV);
                                            if (registeredVolunteersList.isEmpty()) {
                                                registeredVolunteersTV.setVisibility(View.VISIBLE);
                                            }

                                        } else if (child.getValue().toString().equals(Constants.EVENT_REGISTERED) && !registeredVolunteersList.contains(tempVolunteer)) {
                                            if (registeredVolunteersTV.getVisibility() != View.GONE) {
                                                registeredVolunteersTV.setVisibility(View.GONE);
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

    public void removeVolunteerFromList(int index, boolean isPending) {
        if (isPending) {
            pendingVolunteersList.remove(index);
        } else {
            registeredVolunteersList.remove(index);
        }
    }

    // Sets the event to previous for the charity and all volunteers registered with that event.
    private void finishEvent(final String status) {
        mDatabase.getReference()
                .child("Charities")
                .child(event.getOrganisers())
                .child("Events")
                .child(event.getId())
                .setValue(status);

        eventVolunteersReference
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot volunteer : dataSnapshot.getChildren()) {
                                if (status.equals(Constants.EVENT_PREVIOUS)) {
                                    if (volunteer.getValue() != null && volunteer.getValue().toString().equals(Constants.EVENT_REGISTERED)) {
                                        eventVolunteersReference.child(volunteer.getKey()).setValue(status);
                                        volunteersReference.child(volunteer.getKey()).child("Events").child(event.getId()).setValue(status);
                                    }
                                } else if (status.equals(Constants.EVENT_CANCELLED)) {
                                    eventVolunteersReference.child(volunteer.getKey()).setValue(status);
                                    volunteersReference.child(volunteer.getKey()).child("Events").child(event.getId()).setValue(status);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public boolean getIsPastEvent() {
        return isPastEvent;
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

    @OnClick(R.id.eventDetailsConcludeEventBtn)
    public void concludeOnClick() {
        final android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Event Conclusion");
        builder.setMessage("Are you sure you wish to conclude the event?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (pendingVolunteersList.size() > 0) {
                    Toast.makeText(EventDetailsActivity.this, "Please add or remove the pending volunteers", Toast.LENGTH_SHORT).show();
                } else {
                    finishEvent(Constants.EVENT_PREVIOUS);
                    Toast.makeText(EventDetailsActivity.this, "Event has concluded, congratulations!", Toast.LENGTH_SHORT).show();
                    finish();
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

    @OnClick(R.id.eventDetailsCancelEventIV)
    public void cancelEventOnClick() {
        final android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Event Cancellation");
        builder.setMessage("Are you sure you wish to cancel the event?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finishEvent(Constants.EVENT_CANCELLED);
                Toast.makeText(EventDetailsActivity.this, "Your event has been cancelled", Toast.LENGTH_SHORT).show();
                finish();

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    @OnClick(R.id.eventDetailsEditEventIV)
    public void editEventOnClick() {
        Intent intent = new Intent(this, EditEventActivity.class);
        intent.putExtra("event", event);
        startActivity(intent);
    }

}
