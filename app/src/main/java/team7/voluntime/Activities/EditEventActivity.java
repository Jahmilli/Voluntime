package team7.voluntime.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team7.voluntime.Domains.Event;
import team7.voluntime.R;
import team7.voluntime.Utilities.Utilities;


public class EditEventActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private Event event;
    private String eventID;

    // Request Codes
    private static final int LOCATION_REQUEST_CODE = 6;

    @BindView(R.id.editEventTitleET)
    EditText eventTitleET;

    @BindView(R.id.editEventLocationET)
    EditText eventLocationET;

    @BindView(R.id.editEventDateET)
    EditText eventDateET;

    @BindView(R.id.editEventMinAttendeesET)
    EditText eventMinimumET;

    @BindView(R.id.editEventMaxAttendeesET)
    EditText eventMaximumET;

    @BindView(R.id.editEventDescriptionET)
    EditText eventDescriptionET;

    private final String TAG = "EditEventActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        Intent intent = getIntent();
        event = intent.getParcelableExtra("event");
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = Utilities.getEventsReference(database).child(event.getId());
        ButterKnife.bind(this);


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    event = dataSnapshot.getValue(Event.class);
                    event.setId(user.getUid());
                    eventTitleET.setText(event.getTitle());
                    eventDescriptionET.setText(event.getDescription());

                    String address = "Location Unavailable";
                    try {
                        Log.d(TAG, "HEREEEEE");
                        String coords[] = event.getLocation().split(" ");
                        address = Utilities.getLocation(
                                EditEventActivity.this,
                                Double.parseDouble(coords[0]),
                                Double.parseDouble(coords[1]))
                                .get(0)
                                .getAddressLine(0);
                    } catch(IndexOutOfBoundsException e) {
                        Log.e(TAG, "An error occurred when passing location coords: " + event.getLocation());
                        Log.e(TAG, e.toString());
                    }

                    eventLocationET.setText(address);
                    eventDateET.setText(event.getDate());
                    eventMinimumET.setText(String.valueOf(event.getMinimum()));
                    eventMaximumET.setText(String.valueOf(event.getMaximum()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "The read failed: " + databaseError.getCode());
            }

        });
    }

    @OnClick(R.id.editEventSubmitBtn)
    public void setEventInfo() {
        if (checkValidEvent()) {
            String title = eventTitleET.getText().toString().trim();
            String description = eventDescriptionET.getText().toString().trim();
            String date = eventDateET.getText().toString().trim();

            int minAttendees = Integer.parseInt(eventMinimumET.getText().toString().trim());
            int maxAttendees = Integer.parseInt(eventMaximumET.getText().toString().trim());

            reference.child("title").setValue(title);
            reference.child("location").setValue(event.getLocation());
            reference.child("description").setValue(description);
            reference.child("date").setValue(date);
            reference.child("minimum").setValue(minAttendees);
            reference.child("maximum").setValue(maxAttendees);

            finish();
        }
    }

    public boolean checkValidEvent() {
        Log.d(TAG, "Check all fields being called");
        if (eventTitleET.getText().toString().trim().isEmpty() || !StringUtils.isAlphaSpace(eventTitleET.getText().toString())) {
            Toast.makeText(this, "Please enter a valid event name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (eventLocationET.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Fill in a valid location", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (eventDescriptionET.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter a valid event description", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (eventDateET.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Fill in a valid date for this event to be run", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (eventMinimumET.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Fill in a valid number of minimum attendees", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (eventMaximumET.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Fill in a valid number of maximum attendees", Toast.LENGTH_SHORT).show();
            return false;
        }
        int minNum = Integer.parseInt(eventMinimumET.getText().toString().trim());
        int maxNum = Integer.parseInt(eventMaximumET.getText().toString().trim());

        if (maxNum < minNum) {
            Toast.makeText(this, "Your minimum number of attendees must be less than or equal to your maximum", Toast.LENGTH_SHORT).show();
            return false;}

        return true;
    }



    @OnClick(R.id.editEventLocationET)
    public void editLocationOnClick() {
        String coords[] = event.getLocation().split(" ");
        Intent intent = new Intent(this, LocationActivity.class);
        intent.putExtra("latitude", Double.parseDouble(coords[0]));
        intent.putExtra("longitude", Double.parseDouble(coords[1]));
        intent.putExtra("address", eventLocationET.getText().toString());
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_REQUEST_CODE && resultCode == RESULT_OK) {
            double latitude = data.getDoubleExtra("latitude", 0);
            double longitude = data.getDoubleExtra("longitude", 0);
            String address = data.getStringExtra("address");
            if (longitude == 0 && latitude == 0) {
                Toast.makeText(this, "No Location was Selected", Toast.LENGTH_SHORT);
            } else {
                eventLocationET.setText(address);
                event.setLocation(latitude + " " + longitude);
            }
        }
    }
}
