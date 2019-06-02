package team7.voluntime.Activities;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team7.voluntime.Domains.Event;
import team7.voluntime.Domains.Volunteer;
import team7.voluntime.R;
import team7.voluntime.Utilities.EventListAdapter;
import team7.voluntime.Utilities.Utilities;


public class VolunteerHistoryActivity extends AppCompatActivity {
    FirebaseUser mUser;
    private FirebaseDatabase database;
    private DatabaseReference volunteerReference;
    private DatabaseReference eventsReference;
    private Volunteer volunteer;

    ListView listOfPreviousEvents;

    @BindView(R.id.volunteerHistoryPreviousEventsTV)
    TextView previousEventsTV;

    private final static String TAG = "VolunteerHistory";


    public VolunteerHistoryActivity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_history);
        ButterKnife.bind(this);

        // Inflate the layout for this fragment
        ButterKnife.bind(this);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        volunteerReference = Utilities.getVolunteerReference(database, mUser.getUid());
        eventsReference = Utilities.getEventsReference(database);

        listOfPreviousEvents = (ListView) findViewById(R.id.volunteerHistoryPreviousEventsLV);

        final ArrayList<Event> previousEventsList = new ArrayList<>();

        EventListAdapter previousEventListAdapter = new EventListAdapter(
                this,
                R.layout.adapter_view_event_layout,
                previousEventsList,
                this);

        listOfPreviousEvents.setAdapter(previousEventListAdapter);

        // Attach a listener to read the data at our posts reference
        volunteerReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    volunteer = dataSnapshot.child("Profile").getValue(Volunteer.class);
                    volunteer.setId(mUser.getUid());
                    Log.d(TAG, volunteer.toString());

                    for (DataSnapshot event : dataSnapshot.child("Events").getChildren()) {
                        if (event.exists()) {
                            if (event.getValue().toString().equals("previous")) {
                                final String eventId = event.getKey();
                                Log.d(TAG, "Event id is " + eventId);
                                eventsReference.child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Event tempEvent = dataSnapshot.getValue(Event.class);
                                        tempEvent.setId(eventId);
                                        tempEvent.setPastEvent(true);

                                        // Will only display events that the charity has created (event.getOrganisers().equals(mUser.getUid())) {
                                        Log.d(TAG, tempEvent.toString());
                                        previousEventsList.add(tempEvent);
                                        if (previousEventsTV.getVisibility() != View.INVISIBLE) {
                                            previousEventsTV.setVisibility(View.INVISIBLE);
                                        }
                                        listOfPreviousEvents.invalidateViews();

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }

                        }
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "The read failed: " + databaseError.getCode());
            }
        });
    }

    // Used by EventListAdapter
    public Volunteer getVolunteer() {
        return volunteer;
    }

    @OnClick(R.id.volunteerHistoryBackTV)
    public void backOnClick() {
        finish();
    }
}
