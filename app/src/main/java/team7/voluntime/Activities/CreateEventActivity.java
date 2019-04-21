package team7.voluntime.Activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team7.voluntime.R;
import team7.voluntime.Utilities.Utilities;

public class CreateEventActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference eventsReference;
    private DatabaseReference charityReference;
    private FirebaseUser mUser;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private String id;
    private String categories;
    private ArrayList<String> upcomingEvents;


    @BindView(R.id.createEventTitleET)
    EditText titleET;

    @BindView(R.id.createEventDescriptionET)
    EditText descriptionET;

    @BindView(R.id.createEventLocationET)
    EditText locationET;

    @BindView(R.id.createEventDateET)
    EditText eventDateET;

    @BindView(R.id.createEventMinAttendeesET)
    EditText minAttendeesET;

    @BindView(R.id.createEventMaxAttendeesET)
    EditText maxAttendeesET;


    private final static String TAG = "CreateEventActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        ButterKnife.bind(this);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        addListeners();
        Bundle extra = getIntent().getExtras();
        id = extra.getString("id");
        categories = extra.getString("categories");

    }

    public void addListeners() {
        // Listener for the Date Picker
        eventDateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int day, month, year;
                day = 1;
                month = 0;
                year = cal.get(Calendar.YEAR);

                DatePickerDialog dialog = new DatePickerDialog(
                        CreateEventActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month++;
                String dayString = day + "";
                String monthString = month + "";
                // Do this to keep the format consistent
                if (dayString.length() == 1) {
                    dayString = "0" + dayString;
                }
                if (monthString.length() == 1) {
                    monthString = "0" + monthString;
                }
                String currentDateString = dayString + "/" + monthString + "/" + year;
                Log.d(TAG, "Logged date as: " + currentDateString);
                eventDateET.setText(currentDateString);
            }
        };
    }

    private void getUpcomingEvents(final String eventId) {
        charityReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                        upcomingEvents = (ArrayList<String>) dataSnapshot.getValue();
                        upcomingEvents.add(eventId);
                        charityReference.setValue(upcomingEvents);
                } else {
                    upcomingEvents = new ArrayList<>();
                    upcomingEvents.add(eventId);
                    charityReference.setValue(upcomingEvents);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Will say a month before the current month is valid for whatever reason... too tired to look into
    private boolean isValidEventDate() {
        Calendar currentCal = Calendar.getInstance();
        Calendar dob = Calendar.getInstance();
        String date = eventDateET.getText().toString().trim();
        if (date.length() == 0) {
            return false;
        }
        int day = Integer.parseInt(date.substring(0, 2));
        int month = Integer.parseInt(date.substring(3, 5));
        int year = Integer.parseInt(date.substring(6, 10));
        dob.set(Calendar.DAY_OF_MONTH, day);
        dob.set(Calendar.MONTH, month);
        dob.set(Calendar.YEAR, year);
        Log.d(TAG, "IsValidDOB being called: " + dob);
        if(dob.after(currentCal)) {
            return true;
        } else {
            return false;
        }
    }

    @OnClick(R.id.createEventSubmitBtn)
    public void submitEvent() {
        if (checkValidFields()) {
            eventsReference = database.getReference("Events");
            String eventId = eventsReference.push().getKey();
            charityReference = database.getReference("Charities").child(id).child("Events").child("Upcoming");
            Map event = new HashMap();
            Map volunteers = new HashMap();

            String title = titleET.getText().toString().trim();
            String description = descriptionET.getText().toString().trim();
            String location = locationET.getText().toString().trim();
            String date = eventDateET.getText().toString().trim(); // Make date object
            String currentTime = Utilities.getCurrentDate();
            int minAttendees = Integer.parseInt(minAttendeesET.getText().toString().trim());
            int maxAttendees = Integer.parseInt(maxAttendeesET.getText().toString().trim());

            event.put("title", title);
            event.put("description", description);
            event.put("category", categories);
            event.put("location", location);
            event.put("date", date);
            event.put("createdTime", currentTime);
            event.put("organisers", id); // There could eventually be multiple organisers but for now, just one!

            volunteers.put("minimum", minAttendees);
            volunteers.put("maximum", maxAttendees);
            event.put("EventVolunteers", volunteers);

            eventsReference.child(eventId).setValue(event);
            getUpcomingEvents(eventId);
            Toast.makeText(this, "Event Created", Toast.LENGTH_SHORT);

            Intent intent = new Intent(CreateEventActivity.this, MainActivity.class);
            intent.putExtra("accountType", "Charity");
            startActivity(intent);
            finish();
        }
    }

    public boolean checkValidFields() {
        Log.d(TAG, "Check all fields being called");
        int minNum = Integer.parseInt(minAttendeesET.getText().toString().trim());
        int maxNum = Integer.parseInt(maxAttendeesET.getText().toString().trim());

        if (titleET.getText().toString().trim().isEmpty() || !StringUtils.isAlphaSpace(titleET.getText().toString())) {
            Toast.makeText(this, "Please enter a valid event name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (descriptionET.getText().toString().trim().isEmpty() || !StringUtils.isAlphaSpace(descriptionET.getText().toString())) {
            Toast.makeText(this, "Please enter a valid event description", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (locationET.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Fill in a valid location", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidEventDate()) {
            Toast.makeText(this, "Fill in a valid date for this event to be run", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (minAttendeesET.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Fill in a valid number of minimum attendees", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (maxAttendeesET.getText().toString().trim().isEmpty() || maxNum < minNum) {
            Toast.makeText(this, "Fill in a valid number of maximum attendees that is greater than your minimum", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


}
