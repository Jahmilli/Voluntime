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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;

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


public class CharityViewEventsFragment extends Fragment {
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

    private final static String TAG = "CharityViewEvents";


    public CharityViewEventsFragment() {
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

        EventListAdapter upcomingEventListAdapter = new EventListAdapter(getActivity(), R.layout.adapter_view_event_layout, upcomingEventList, CharityViewEventsFragment.this);
        EventListAdapter previousEventListAdapter = new EventListAdapter(getActivity(), R.layout.adapter_view_event_layout, previousEventList, CharityViewEventsFragment.this);
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

                                String minimum = (Objects.requireNonNull(child.child("EventVolunteers").child("minimum").getValue()).toString());
                                String maximum = (Objects.requireNonNull(child.child("EventVolunteers").child("maximum").getValue()).toString());
                                HashMap<String, String> volunteers = Utilities.getVolunteers(child.child("EventVolunteers").child("Volunteers"), TAG);

                                EventVolunteers eventVolunteers = new EventVolunteers();
                                int intMin = minimum == null ? 0 : Integer.parseInt(minimum);
                                int intMax = maximum == null ? 0 : Integer.parseInt(maximum);
                                eventVolunteers.setMinimum(intMin);
                                eventVolunteers.setMaximum(intMax);
                                eventVolunteers.setVolunteers(volunteers);


                                Event event = child.getValue(Event.class);
                                event.setId(eventId);
                                event.setVolunteers(eventVolunteers);

                                // Will only display events that the charity has created
                                if (event.getOrganisers().equals(mUser.getUid())) {
                                    Log.d(TAG, event.toString());
                                    if (upcomingEvents.containsKey(eventId)) {
                                        upcomingEventList.add(event);
                                        if (upcomingEventsTV.getVisibility() != View.INVISIBLE) {
                                            upcomingEventsTV.setVisibility(View.INVISIBLE);
                                        }
                                    } else if (previousEvents.containsKey(eventId)) {
                                        previousEventList.add(event);
                                        if (previousEventsTV.getVisibility() != View.INVISIBLE) {
                                            previousEventsTV.setVisibility(View.INVISIBLE);
                                        }
                                    }

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
        Intent createEventIntent = new Intent(getActivity(), CreateEventActivity.class);
        createEventIntent.putExtra("id", charity.getId());
        createEventIntent.putExtra("categories", charity.getCategory());
        startActivity(createEventIntent);
    }

}
