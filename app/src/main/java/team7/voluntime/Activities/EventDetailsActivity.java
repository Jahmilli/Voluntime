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
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team7.voluntime.Domains.Event;
import team7.voluntime.Domains.Volunteer;
import team7.voluntime.R;
import team7.voluntime.Utilities.Utilities;
import team7.voluntime.Utilities.VolunteerListAdapter;

public class EventDetailsActivity extends AppCompatActivity {

    private String[] coords;
    private FirebaseDatabase mDatabase;
    private DatabaseReference volunteersReference;

    @BindView(R.id.eventDetailsTitleTV)
    TextView titleTV;

    @BindView(R.id.eventDetailsDescriptionTV)
    TextView descriptionTV;

    @BindView(R.id.eventDetailsCategoryTV)
    TextView categoryTV;

    @BindView(R.id.eventDetailsLocationTV)
    TextView locationTV;

    @BindView(R.id.eventDetailsDateTV)
    TextView dateTV;

    @BindView(R.id.eventDetailsCreatedTimeTV)
    TextView createdTimeTV;

    @BindView(R.id.eventDetailsOrganisersTV)
    TextView organisersTV;

    @BindView(R.id.eventDetailsMapIV)
    ImageView mapIV;

//    @BindView(R.id.eventPendingVolunteersLV)
    private ListView pendingVolunteersLV;

    private final static String TAG = "EventDetails";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Event event = (Event) intent.getParcelableExtra("event");
        final HashMap<String, String> volunteers = (HashMap<String,String>) intent.getExtras().get("volunteers");
//        int minimum = intent.getExtras().getInt("minimum", 0);
//        int maximum = intent.getExtras().getInt("maximum", 0);
        pendingVolunteersLV = (ListView) findViewById(R.id.eventPendingVolunteersLV);
        mDatabase = FirebaseDatabase.getInstance();
        volunteersReference = mDatabase.getReference().child("Volunteers");

        if (event != null) {
            coords = event.getLocation().split(" ");

            titleTV.setText(event.getTitle());
            descriptionTV.setText(event.getDescription());
            categoryTV.setText(event.getCategory());
            dateTV.setText(event.getDate());
            createdTimeTV.setText(event.getCreatedTime());
            organisersTV.setText(event.getOrganisers());
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
        }


        if (!volunteers.isEmpty()) {
            Log.d(TAG, "Event Volunteers is: " + volunteers.toString());
            final ArrayList<Volunteer> pendingVolunteersList = new ArrayList<>();
            final ArrayList<Volunteer> registeredVolunteersList = new ArrayList<>();

            final VolunteerListAdapter pendingVolunteersAdapter = new VolunteerListAdapter(this,  R.layout.adapter_view_volunteer_layout, pendingVolunteersList, this);
            pendingVolunteersLV.setAdapter(pendingVolunteersAdapter);


            volunteersReference
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            pendingVolunteersList.clear();
//                            registeredVolunteersList.clear();
//                            upcomingEventsTV.setVisibility(View.VISIBLE);
//                            previousEventsTV.setVisibility(View.VISIBLE);

                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                if (child.exists()) {
                                    if (volunteers.containsKey(child.getKey())) {
                                        Volunteer tempVolunteer = child.child("Profile").getValue(Volunteer.class);
                                        tempVolunteer.setId(child.getKey());
                                        Log.d(TAG, "Volunteer is : " + tempVolunteer.toString());
                                        pendingVolunteersList.add(tempVolunteer);
                                    }
                                    pendingVolunteersLV.invalidateViews();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }

                    });
        }
    }

    @OnClick(R.id.eventDetailsMapIV)
    public void mapOnClick() {
        Intent intent = new Intent(this, LocationActivity.class);
        intent.putExtra("latitude", Double.parseDouble(coords[0]));
        intent.putExtra("longitude", Double.parseDouble(coords[1]));
        intent.putExtra("address", locationTV.getText().toString());
        startActivity(intent);
    }

}
