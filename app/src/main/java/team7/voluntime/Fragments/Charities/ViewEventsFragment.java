package team7.voluntime.Fragments.Charities;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team7.voluntime.Activities.CreateEventActivity;
import team7.voluntime.Domains.Charity;
import team7.voluntime.Domains.Event;
import team7.voluntime.Domains.EventVolunteers;
import team7.voluntime.R;
import team7.voluntime.Utilities.EventListAdapter;
import team7.voluntime.Utilities.Utilities;


public class ViewEventsFragment extends Fragment {
    FirebaseUser mUser;
    private FirebaseDatabase database;
    private DatabaseReference charityReference;
    private DatabaseReference eventReference;
    private Charity charity;

    ListView listOfUpcomingEvents;
    ListView listOfPreviousEvents;

    @BindView(R.id.upcomingEventsTV)
    TextView upcomingEventsTV;

    @BindView(R.id.previousEventsTV)
    TextView previousEventsTV;

    @BindView(R.id.viewEventTitleTV)
    TextView viewEventTitleTV;

    @BindView(R.id.createEventIV)
    ImageView createEventIV;

    private final static String TAG = "ViewEventsFragment";


    public ViewEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//
        getActivity().setTitle("View Events");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_view_events, container, false);
        ButterKnife.bind(this, v);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        charityReference = Utilities.getCharityReference(database, mUser.getUid());
        eventReference = Utilities.getEventsReference(database);

        listOfUpcomingEvents = (ListView) v.findViewById(R.id.listOfUpcomingEventsLV);
        listOfPreviousEvents = (ListView) v.findViewById(R.id.listOfPreviousEventsLV);
        final ArrayList<Event> upcomingEventList = new ArrayList<>();
        final ArrayList<Event> previousEventList = new ArrayList<>();
        final HashMap<String, String> upcomingEvents = new HashMap<>();
        final HashMap<String, String> previousEvents = new HashMap<>();

        EventListAdapter upcomingEventListAdapter = new EventListAdapter(getActivity(), R.layout.adapter_view_event_layout, upcomingEventList, ViewEventsFragment.this);
        EventListAdapter previousEventListAdapter = new EventListAdapter(getActivity(), R.layout.adapter_view_event_layout, previousEventList, ViewEventsFragment.this);
        listOfUpcomingEvents.setAdapter(upcomingEventListAdapter);
        listOfPreviousEvents.setAdapter(previousEventListAdapter);

        // Attach a listener to read the data at our posts reference
        charityReference.child("Profile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                charity = dataSnapshot.getValue(Charity.class);
                charity.setId(mUser.getUid());
                Log.d(TAG, charity.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "The read failed: " + databaseError.getCode());
            }
        });

        charityReference.child("Events").child("Upcoming").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.exists()) {
                        if (child.getValue() != null) {
                            String id = child.getValue().toString();
                            upcomingEvents.put(id, ""); // Add the id which can then be searched later on
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        charityReference.child("Events").child("Previous").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.exists()) {
                        if (child.getValue() != null) {
                            String id = child.getValue().toString();
                            previousEvents.put(id, ""); // Add the id which can then be searched later on
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        eventReference
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        upcomingEventList.clear();
                        previousEventList.clear();
                        upcomingEventsTV.setVisibility(View.VISIBLE);
                        previousEventsTV.setVisibility(View.VISIBLE);

                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            if (child.exists()) {
                                String eventId = child.getKey();
                                Log.d(TAG, "Event id is " + eventId);

                                String minimum = (child.child("EventVolunteers").child("minimum").getValue() != null)
                                        ? child.child("EventVolunteers").child("minimum").getValue().toString() : null;
                                String maximum = (child.child("EventVolunteers").child("maximum").getValue() != null)
                                        ? child.child("EventVolunteers").child("maximum").getValue().toString() : null;

//                                ArrayList<String> pendingVolunteers = (child.child("EventVolunteers").child("pendingVolunteers").getValue() != null)
//                                        ? child.child("pendingVolunteers").getValue() : null;
//                                String maximum = (child.child("EventVolunteers").child("maximum").getValue() != null)
//                                        ? child.child("maximum").getValue().toString() : null;
//                                String maximum = (child.child("EventVolunteers").child("maximum").getValue() != null)
//                                        ? child.child("maximum").getValue().toString() : null;


                                EventVolunteers eventVolunteers = new EventVolunteers();
                                int intMin = minimum == null ? 0 : Integer.parseInt(minimum);
                                int intMax = maximum == null ? 0 : Integer.parseInt(maximum);
                                eventVolunteers.setMinimum(intMin);
                                eventVolunteers.setMaximum(intMax);


                                Event event = child.getValue(Event.class);
                                event.setId(eventId);
                                event.setVolunteers(eventVolunteers);

                                // Will only display events that the charity has created
                                Log.d(TAG, "Organisers are: " + event.getOrganisers() + "\tMyID: " + mUser.getUid());
                                if (event.getOrganisers().equals(mUser.getUid())) {
                                    Log.d(TAG, event.toString());
                                    if (upcomingEvents.containsKey(eventId)) {
                                        Log.d(TAG, "Upcoming events contains " + eventId);
                                        upcomingEventList.add(event);
                                        if (upcomingEventsTV.getVisibility() != View.INVISIBLE) {
                                            upcomingEventsTV.setVisibility(View.INVISIBLE);
                                        }
                                    } else if (previousEvents.containsKey(eventId)) {
                                        Log.d(TAG, "Previous events contains " + eventId);
                                        previousEventList.add(event);
                                        if (previousEventsTV.getVisibility() != View.INVISIBLE) {
                                            previousEventsTV.setVisibility(View.INVISIBLE);
                                        }
                                    } else {
                                        Log.d(TAG, "NEITHER event contains " + eventId);
                                        Log.d(TAG, "UPCOMING: " + upcomingEventList.toString());
                                        Log.d(TAG, "PREVIOUS: " + previousEventList.toString());
                                    }
//                                    if (child.child("approved").exists()) {
//                                        if (child.child("approved").getValue().toString().equals("pending")) {
//                                            pendingPatientList.add(patient);
//                                            if (pendingPatientsTV.getVisibility() != View.INVISIBLE)
//                                                pendingPatientsTV.setVisibility(View.INVISIBLE);
//                                        } else if (child.child("approved").getValue().toString().equals("accepted")) {
//                                            patientList.add(patient);
//                                            if (currentPatientsTV.getVisibility() != View.INVISIBLE)
//                                                currentPatientsTV.setVisibility(View.INVISIBLE);
//                                        }
//                                        listOfPatients.invalidateViews();
//                                        listOfPendingPatients.invalidateViews();
//                                    }
                                    listOfUpcomingEvents.invalidateViews();
                                    listOfPreviousEvents.invalidateViews();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public DatabaseReference getDBReference() {
        return this.charityReference;
    }
    public FirebaseUser getUser() {
        return mUser;
    }

    @OnClick(R.id.createEventIV)
    public void createEventOnClick() {
        Log.d(TAG, "Clicked");
        Intent createEventIntent = new Intent(getActivity(), CreateEventActivity.class);
        createEventIntent.putExtra("id", charity.getId());
        createEventIntent.putExtra("categories", charity.getCategory());
        startActivity(createEventIntent);
    }

}
