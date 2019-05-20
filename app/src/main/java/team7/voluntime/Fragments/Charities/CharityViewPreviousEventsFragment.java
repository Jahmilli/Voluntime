package team7.voluntime.Fragments.Charities;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import team7.voluntime.Domains.Charity;
import team7.voluntime.Domains.Event;
import team7.voluntime.R;
import team7.voluntime.Utilities.EventListAdapter;
import team7.voluntime.Utilities.Utilities;


public class CharityViewPreviousEventsFragment extends Fragment {
    private final static String TAG = "CharityViewEvents";
    FirebaseUser mUser;
    //ListView listOfUpcomingEventsLV;
    ListView listOfPreviousEventsLV;
    private FirebaseDatabase database;
    private DatabaseReference charityReference;
    private DatabaseReference eventReference;
    private Charity charity;

    public CharityViewPreviousEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("View Previous Events");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_charity_view_previous_events, container, false);
        ButterKnife.bind(this, v);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        charityReference = Utilities.getCharityReference(database, mUser.getUid());
        eventReference = Utilities.getEventsReference(database);

        listOfPreviousEventsLV = v.findViewById(R.id.listOfPreviousEventsLV);
        final ArrayList<Event> previousEventList = new ArrayList<>();

        final HashMap<String, String> previousEvents = new HashMap<>();

        EventListAdapter<CharityViewPreviousEventsFragment> previousEventListAdapter = new EventListAdapter<>(getActivity(), R.layout.adapter_view_event_layout, previousEventList, CharityViewPreviousEventsFragment.this);

        listOfPreviousEventsLV.setAdapter(previousEventListAdapter);

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

        charityReference.child("Events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.exists() && child.getValue() != null) {
                            String id = child.getKey();
                            String value = child.getValue().toString();
                            if (value.equals("previous")) {
                                previousEvents.put(id, ""); // Add the id which can then be searched later on
                            }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        eventReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                previousEventList.clear();

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.exists()) {
                        String eventId = child.getKey();
                        Log.d(TAG, "Event id is " + eventId);
                        HashMap<String, String> volunteers = Utilities.getVolunteers(child.child("Volunteers"), TAG);

                        Event event = child.getValue(Event.class);
                        event.setVolunteers(volunteers);
                        event.setId(eventId);

                        // Will only display events that the charity has created
                        if (event.getOrganisers().equals(mUser.getUid())) {
                            if (previousEvents.containsKey(eventId)) {
                                event.setPastEvent(true);
                                previousEventList.add(event);
                                listOfPreviousEventsLV.invalidateViews();
                            }
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

}
