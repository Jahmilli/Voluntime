package team7.voluntime.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
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
import team7.voluntime.Utilities.EventListAdapter;
import team7.voluntime.Utilities.Utilities;

public class VolunteerDetailsActivity extends AppCompatActivity {
    private static String TAG = "VolunteerDetailsActivity";

    Volunteer volunteer;
    private FirebaseDatabase mDatabase;
    private DatabaseReference volunteerHistoryReference;
    private DatabaseReference eventsReference;
    private ListView volunteerHistoryLV;

    // Bindings
    @BindView(R.id.volunteerDetailsVolunteerLayout)
    LinearLayout volunteerLayout;

    @BindView(R.id.volunteerDetailsNameTV)
    TextView nameTV;
    @BindView(R.id.volunteerDetailsEmailTV)
    TextView emailTV;
    @BindView(R.id.volunteerDetailsPhoneTV)
    TextView phoneTV;
    @BindView(R.id.volunteerDetailsAddressTV)
    TextView addressTV;
    @BindView(R.id.volunteerDetailsGenderTV)
    TextView genderTV;
    @BindView(R.id.volunteerDetailsDobTV)
    TextView dobTV;
    @BindView(R.id.volunteerDetailsHistoryTV)
    TextView historyTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_details);
        ButterKnife.bind(this);
        mDatabase = FirebaseDatabase.getInstance();
        volunteerHistoryReference = mDatabase.getReference().child("Volunteers");
        eventsReference = mDatabase.getReference().child("Events");
        volunteerHistoryLV = findViewById(R.id.volunteerDetailsHistoryLV);

        Intent intent = getIntent();
        volunteer = intent.getParcelableExtra("volunteer");
        nameTV.setText(volunteer.getName());
        emailTV.setText(volunteer.getEmail());
        phoneTV.setText(volunteer.getPhoneNumber());
        addressTV.setText(volunteer.getAddress());
        dobTV.setText(volunteer.getDateOfBirth());
        genderTV.setText(volunteer.getGender());

        final ArrayList<Event> volunteerHistoryList = new ArrayList<>();

        final EventListAdapter<VolunteerDetailsActivity> volunteerHistoryAdapter = new EventListAdapter<>(this, R.layout.adapter_view_event_layout, volunteerHistoryList, this);
        volunteerHistoryLV.setAdapter(volunteerHistoryAdapter);

        volunteerHistoryReference
            .child(volunteer.getId())
            .child("Events")
            .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                volunteerHistoryList.clear();
                volunteerHistoryAdapter.notifyDataSetChanged();

                for (final DataSnapshot event : dataSnapshot.getChildren()) {
                    if (event.exists() && event.getValue().toString().equals("previous")) {
                        eventsReference.child(event.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Event tempEvent = dataSnapshot.getValue(Event.class);
                                tempEvent.setVolunteers(new HashMap<String, String>());
                                if (historyTV.getVisibility() != View.INVISIBLE) {
                                    historyTV.setVisibility(View.INVISIBLE);
                                }
                                volunteerHistoryList.add(tempEvent);
                                volunteerHistoryLV.invalidateViews();
                                Utilities.setDynamicHeight(volunteerHistoryLV);
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

    public String getVolunteerID() {
        return volunteer.getId();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @OnClick(R.id.volunteerDetailsBackTV)
    public void backButtonOnClick() {
        finish();
    }
}