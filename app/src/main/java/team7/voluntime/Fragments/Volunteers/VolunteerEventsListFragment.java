package team7.voluntime.Fragments.Volunteers;

import android.app.Fragment;
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
import team7.voluntime.Domains.Event;
import team7.voluntime.Domains.EventVolunteers;
import team7.voluntime.Domains.Volunteer;
import team7.voluntime.R;
import team7.voluntime.Utilities.EventListAdapter;
import team7.voluntime.Utilities.Utilities;


public class VolunteerEventsListFragment extends Fragment {
    FirebaseUser mUser;
    private FirebaseDatabase database;
    private DatabaseReference volunteerReference;
    private DatabaseReference eventsReference;
    private Volunteer volunteer;

    ListView listOfUpcomingEvents;

    @BindView(R.id.volunteerUpcomingEventsTV)
    TextView upcomingEventsTV;

    @BindView(R.id.volunteerViewEventTitleTV)
    TextView viewEventTitleTV;

    @BindView(R.id.volunteerCreateEventIV)
    ImageView createEventIV;

    private final static String TAG = "VolunteerEventsList";


    public VolunteerEventsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().setTitle("All Events");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_volunteer_events_list, container, false);
        ButterKnife.bind(this, v);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        volunteerReference = Utilities.getVolunteerReference(database, mUser.getUid());
        eventsReference = Utilities.getEventsReference(database);

        listOfUpcomingEvents = (ListView) v.findViewById(R.id.volunteerListOfUpcomingEventsLV);

        final ArrayList<Event> upcomingEventList = new ArrayList<>();

        EventListAdapter upcomingEventListAdapter = new EventListAdapter(
                getActivity(),
                R.layout.adapter_view_event_layout,
                upcomingEventList,
                VolunteerEventsListFragment.this);

        listOfUpcomingEvents.setAdapter(upcomingEventListAdapter);

        // Attach a listener to read the data at our posts reference
        volunteerReference.child("Profile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                volunteer = dataSnapshot.getValue(Volunteer.class);
                volunteer.setId(mUser.getUid());
                Log.d(TAG, volunteer.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "The read failed: " + databaseError.getCode());
            }
        });

        eventsReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        upcomingEventList.clear();
                        upcomingEventsTV.setVisibility(View.VISIBLE);

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

                                // Will only display events that the charity has createdif (event.getOrganisers().equals(mUser.getUid())) {
                                Log.d(TAG, event.toString());
                                upcomingEventList.add(event);
                                if (upcomingEventsTV.getVisibility() != View.INVISIBLE) {
                                    upcomingEventsTV.setVisibility(View.INVISIBLE);
                                }
                                listOfUpcomingEvents.invalidateViews();
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

    @OnClick(R.id.volunteerCreateEventIV)
    public void createEventOnClick() {
//        Intent createEventIntent = new Intent(getActivity(), CreateEventActivity.class);
//        createEventIntent.putExtra("id", volunteer.getId());
//        createEventIntent.putExtra("categories", volunteer.getCategory());
//        startActivity(createEventIntent);
        Log.d(TAG, "Event Button Clicked");
    }

}
