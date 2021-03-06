package team7.voluntime.Activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team7.voluntime.R;
import team7.voluntime.Utilities.MapModels.LocationDefaults;
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
    private double latitude;
    private double longitude;

    // Request Codes
    private static final int LOCATION_REQUEST_CODE = 6;


    @BindView(R.id.createEventTitleET)
    EditText titleET;

    @BindView(R.id.createEventDescriptionET)
    EditText descriptionET;

    @BindView(R.id.createEventLocationET)
    EditText locationET;

    @BindView(R.id.createEventDateET)
    EditText eventDateET;

    @BindView(R.id.createEventStartTimeET)
    EditText startTimeET;

    @BindView(R.id.createEventEndTimeET)
    EditText endTimeET;

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
                if (eventDateET.length() > 0) {
                    String date = eventDateET.getText().toString().trim();
                    day = Integer.parseInt(date.substring(0, 2));
                    month = Integer.parseInt(date.substring(3, 5)) - 1;
                    year = Integer.parseInt(date.substring(6, 10));
                } else {
                    day = cal.get(Calendar.DAY_OF_MONTH);
                    month = cal.get(Calendar.MONTH);
                    year = cal.get(Calendar.YEAR);
                }

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

    private boolean isValidEventDate() {
        Calendar currentCal = Calendar.getInstance();
        Calendar eventDate = Calendar.getInstance();
        String date = eventDateET.getText().toString().trim();
        if (date.length() == 0) {
            return false;
        }
        int day = Integer.parseInt(date.substring(0, 2));
        int month = Integer.parseInt(date.substring(3, 5)) - 1;
        int year = Integer.parseInt(date.substring(6, 10));
        eventDate.set(Calendar.DAY_OF_MONTH, day);
        eventDate.set(Calendar.MONTH, month);
        eventDate.set(Calendar.YEAR, year);
        Log.d(TAG, "IsValidDOB being called: " + eventDate);
        return eventDate.after(currentCal);
    }

    private boolean isValidEventTime() {
        SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
        try {
            Date startTime = parser.parse(startTimeET.getText().toString());
            Date endTime = parser.parse(endTimeET.getText().toString());
            if (startTime.after(endTime)) {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @OnClick(R.id.createEventSubmitBtn)
    public void submitEvent() {
        if (checkValidFields()) {
            eventsReference = database.getReference("Events");
            String eventId = eventsReference.push().getKey();
            charityReference = database.getReference("Charities").child(id).child("Events");
            Map event = new HashMap();

            String title = titleET.getText().toString().trim();
            String description = descriptionET.getText().toString().trim();
            String date = eventDateET.getText().toString();
            String startTime = startTimeET.getText().toString();
            String endTime = endTimeET.getText().toString();

            String currentTime = Utilities.getCurrentDate();
            int minAttendees = Integer.parseInt(minAttendeesET.getText().toString().trim());
            int maxAttendees = Integer.parseInt(maxAttendeesET.getText().toString().trim());

            event.put("title", title);
            event.put("description", description);
            event.put("category", categories);
            event.put("location", latitude + " " + longitude);
            event.put("date", date);
            event.put("startTime", startTime);
            event.put("endTime", endTime);
            event.put("createdTime", currentTime);
            event.put("organisers", id); // There could eventually be multiple organisers but for now, just one!
            event.put("minimum", minAttendees);
            event.put("maximum", maxAttendees);

            eventsReference.child(eventId).setValue(event);
            charityReference.child(eventId).setValue("upcoming");
            Toast.makeText(this, "Event Created", Toast.LENGTH_SHORT);

            Intent intent = new Intent(CreateEventActivity.this, MainActivity.class);
            intent.putExtra("accountType", "Charity");
            startActivity(intent);
            finish();
        }
    }

    public boolean checkValidFields() {
        Log.d(TAG, "Check all fields being called");

        if (titleET.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter a valid event name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (descriptionET.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter a valid event description", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (locationET.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Fill in a valid location", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Only handling 1 day events
        if (!isValidEventDate()) {
            Toast.makeText(this, "Fill in a valid date for this event to be run", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (startTimeET.getText().toString().isEmpty()) {
            Toast.makeText(this, "Fill in a valid start time for this event to be run", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (endTimeET.getText().toString().isEmpty()) {
            Toast.makeText(this, "Fill in a valid end time for this event to be run", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isValidEventTime()) {
            Toast.makeText(this, "Start time must be before your end time.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (minAttendeesET.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Fill in a valid number of minimum attendees", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (maxAttendeesET.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Fill in a valid number of maximum attendees", Toast.LENGTH_SHORT).show();
            return false;
        }

        int minNum = Integer.parseInt(minAttendeesET.getText().toString().trim());
        int maxNum = Integer.parseInt(maxAttendeesET.getText().toString().trim());

        if (maxNum < minNum) {
            Toast.makeText(this, "Your minimum number of attendees must be less than or equal to your maximum", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @OnClick(R.id.createEventLocationET)
    public void locationOnClick() {
        try {
            Intent intent = new Intent(this, LocationActivity.class);
            startActivityForResult(intent, LOCATION_REQUEST_CODE);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            Toast.makeText(this, "Unable to open location in Map", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.createEventStartTimeET)
    public void startTimeOnClick() {
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(CreateEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                startTimeET.setText( selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, false);
        mTimePicker.setTitle("Select Start Time");
        mTimePicker.show();
    }

    @OnClick(R.id.createEventEndTimeET)
    public void endTimeOnClick() {
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(CreateEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                endTimeET.setText( selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, false);
        mTimePicker.setTitle("Select End Time");
        mTimePicker.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_REQUEST_CODE && resultCode == RESULT_OK) {
            latitude = data.getDoubleExtra("latitude", 0);
            longitude = data.getDoubleExtra("longitude", 0);
            String address = data.getStringExtra("address");
            if (longitude == 0 && latitude == 0) {
                Toast.makeText(this, "No Location was Selected", Toast.LENGTH_SHORT);
            } else {
                locationET.setText(address);
            }
        }
    }

}
