package team7.voluntime.Activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
    private DatePickerDialog.OnDateSetListener mDateSetListener;


    // Request Codes
    private static final int LOCATION_REQUEST_CODE = 6;

    @BindView(R.id.editEventTitleET)
    EditText eventTitleET;

    @BindView(R.id.editEventLocationET)
    EditText eventLocationET;

    @BindView(R.id.editEventDateET)
    EditText eventDateET;

    @BindView(R.id.editEventStartTimeET)
    EditText startTimeET;

    @BindView(R.id.editEventEndTimeET)
    EditText endTimeET;

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
        ButterKnife.bind(this);
        Intent intent = getIntent();
        event = intent.getParcelableExtra("event");
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = Utilities.getEventsReference(database).child(event.getId());
        addListeners();


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    event = dataSnapshot.getValue(Event.class);
                    event.setId(user.getUid());
                    eventTitleET.setText(event.getTitle());
                    eventDescriptionET.setText(event.getDescription());
                    startTimeET.setText(event.getStartTime());
                    endTimeET.setText(event.getEndTime());

                    String address = "Location Unavailable";
                    try {
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
            String startTime = startTimeET.getText().toString().trim();
            String endTime = endTimeET.getText().toString().trim();
            int minAttendees = Integer.parseInt(eventMinimumET.getText().toString().trim());
            int maxAttendees = Integer.parseInt(eventMaximumET.getText().toString().trim());

            reference.child("title").setValue(title);
            reference.child("location").setValue(event.getLocation());
            reference.child("description").setValue(description);
            reference.child("date").setValue(date);
            reference.child("startTime").setValue(startTime);
            reference.child("endTime").setValue(endTime);
            reference.child("minimum").setValue(minAttendees);
            reference.child("maximum").setValue(maxAttendees);
            finish();
        }
    }

    public boolean checkValidEvent() {
        Log.d(TAG, "Check all fields being called");

        if (eventTitleET.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter a valid event name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (eventDescriptionET.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter a valid event description", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (eventLocationET.getText().toString().trim().isEmpty()) {
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
            return false;
        }

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
                        EditEventActivity.this,
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


    @OnClick(R.id.editEventStartTimeET)
    public void startTimeOnClick() {
        Calendar currentTime = Calendar.getInstance();
        int hour, minute;
        if (startTimeET.length() > 0) {
            String[] time = startTimeET.getText().toString().split(":");
            hour = Integer.parseInt(time[0]);
            minute = Integer.parseInt(time[1]);
        } else {
            hour = currentTime.get(Calendar.HOUR_OF_DAY);
            minute = currentTime.get(Calendar.MINUTE);
        }
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                startTimeET.setText( selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, false);
        mTimePicker.setTitle("Select Start Time");
        mTimePicker.show();
    }

    @OnClick(R.id.editEventEndTimeET)
    public void endTimeOnClick() {
        Calendar currentTime = Calendar.getInstance();

        int hour, minute;
        if (endTimeET.length() > 0) {
            String[] time = endTimeET.getText().toString().split(":");
            hour = Integer.parseInt(time[0]);
            minute = Integer.parseInt(time[1]);
        } else {
            hour = currentTime.get(Calendar.HOUR_OF_DAY);
            minute = currentTime.get(Calendar.MINUTE);
        }

        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
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
