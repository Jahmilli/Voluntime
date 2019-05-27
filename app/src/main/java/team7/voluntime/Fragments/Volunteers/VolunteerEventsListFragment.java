package team7.voluntime.Fragments.Volunteers;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team7.voluntime.Activities.SearchCharityActivity;
import team7.voluntime.Activities.SearchEventActivity;
import team7.voluntime.Domains.Event;
import team7.voluntime.Domains.Volunteer;
import team7.voluntime.R;
import team7.voluntime.Utilities.EventListAdapter;
import team7.voluntime.Utilities.Utilities;


public class VolunteerEventsListFragment extends Fragment {
    FirebaseUser mUser;
    private FirebaseDatabase database;
    private DatabaseReference volunteerReference;
    private DatabaseReference eventsReference;
    private DatabaseReference charitiesReference;
    private Volunteer volunteer;
    private ArrayAdapter adapter;
    private EventListAdapter upcomingEventListAdapter;
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");


    ListView listOfUpcomingEvents;

    @BindView(R.id.volunteerUpcomingEventsTV)
    TextView upcomingEventsTV;

    @BindView(R.id.volunteerViewEventTitleTV)
    TextView viewEventTitleTV;

    @BindView(R.id.searchCharity)
    TextView searchCharity;


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
        charitiesReference = database.getReference().child("Charities");

        listOfUpcomingEvents = (ListView) v.findViewById(R.id.volunteerListOfUpcomingEventsLV);

        final ArrayList<Event> upcomingEventList = new ArrayList<>();

        upcomingEventListAdapter = new EventListAdapter(
                getActivity(),
                R.layout.adapter_view_event_layout,
                upcomingEventList,
                VolunteerEventsListFragment.this);

        listOfUpcomingEvents.setAdapter(upcomingEventListAdapter);

        // Attach a listener to read the data at our posts reference
        volunteerReference.child("Profile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    volunteer = dataSnapshot.getValue(Volunteer.class);
                    volunteer.setId(mUser.getUid());
                    Log.d(TAG, volunteer.toString());
                }
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

                        for (final DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                            if (eventSnapshot.exists()) {
                                String eventId = eventSnapshot.getKey();
                                final Event event = eventSnapshot.getValue(Event.class);
                                event.setId(eventId);

                                charitiesReference.child(event.getOrganisers()).child("Events").child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot charityEventStatusSnapshot) {
                                        if (charityEventStatusSnapshot.exists() && charityEventStatusSnapshot.getValue() != null) {
                                            if (charityEventStatusSnapshot.getValue().equals("upcoming")) {
                                                if (eventSnapshot.child("Volunteers").child(volunteer.getId()).getValue() != null) {
                                                    String status = eventSnapshot.child("Volunteers").child(volunteer.getId()).getValue().toString();
                                                    event.setVolunteerStatus(status);
                                                }
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

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        EditText charityFilter = (EditText) v.findViewById(R.id.charityFilter);
        charityFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                (VolunteerEventsListFragment.this).upcomingEventListAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}
