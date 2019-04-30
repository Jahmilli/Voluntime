package team7.voluntime.Activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team7.voluntime.Domains.Volunteer;
import team7.voluntime.R;
import team7.voluntime.Utilities.Utilities;

public class EditVolunteerActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private boolean animated = false;
    private Volunteer volunteer;

    @BindView(R.id.volunteerNameET)
    EditText volunteerNameET;

    @BindView(R.id.volunteerPhoneET)
    EditText volunteerPhoneET;

    @BindView(R.id.volunteerDOBET)
    EditText volunteerDOBET;

    @BindView(R.id.volunteerAddressET)
    EditText volunteerAddressET;

    @BindView(R.id.volunteerGenderRG)
    RadioGroup volunteerGenderRG;

    @BindView(R.id.volunteerMaleRB)
    RadioButton volunteerMaleRB;

    @BindView(R.id.volunteerFemaleRB)
    RadioButton volunteerFemaleRB;

    @BindView(R.id.editVolunteerSV)
    ScrollView editVolunteerSV;

    private final String TAG = "EditVolunteerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_edit_details);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        database = FirebaseDatabase.getInstance();

        reference = Utilities.getVolunteerReference(database, user.getUid());

        ButterKnife.bind(this);

        addListeners();


        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reference.child("Profile").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        volunteer = dataSnapshot.getValue(Volunteer.class);
                        volunteer.setId(user.getUid());
                        volunteerNameET.setText(volunteer.getName());
                        volunteerPhoneET.setText(volunteer.getPhoneNumber());
                        volunteerDOBET.setText(volunteer.getDateOfBirth());
                        volunteerAddressET.setText(volunteer.getAddress());

                        if (volunteer.getGender().equals("Male")) {
                            volunteerMaleRB.setChecked(true);
                            volunteerFemaleRB.setChecked(false);
                        } else {
                            volunteerMaleRB.setChecked(false);
                            volunteerFemaleRB.setChecked(true);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "The read failed: " + databaseError.getCode());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "The read failed: " + databaseError.getCode());
            }

        });

    }

    @OnClick(R.id.editFinishVolunteer)
    public void setVolunteerInfo() {
        if (checkPassedFirstVolunteer()) {
            reference = database.getReference("Volunteers").child(user.getUid());
            String name = volunteerNameET.getText().toString().trim();
            String email = mAuth.getCurrentUser().getEmail();
            String date = volunteerDOBET.getText().toString().trim();
            String phoneNumber = volunteerPhoneET.getText().toString().trim();
            String address = volunteerAddressET.getText().toString().trim();
            RadioButton radioButton = findViewById(volunteerGenderRG.getCheckedRadioButtonId());
            String gender = radioButton.getText().toString().trim();

            reference.child("Profile").child("fullName").setValue(name);
            reference.child("Profile").child("email").setValue(email);
            reference.child("Profile").child("phoneNumber").setValue(phoneNumber);
            reference.child("Profile").child("dateOfBirth").setValue(date);
            reference.child("Profile").child("address").setValue(address);
            reference.child("Profile").child("gender").setValue(gender);

            reference.child("accountType").setValue("Volunteer");

            Intent intent = new Intent(EditVolunteerActivity.this, MainActivity.class);
            intent.putExtra("accountType", "Volunteer");
            startActivity(intent);
            finish();
        }
    }

    public boolean checkPassedFirstVolunteer() {
        Log.d(TAG, "Check all fields being called");

        if (volunteerNameET.getText().toString().trim().isEmpty() || !StringUtils.isAlphaSpace(volunteerNameET.getText().toString())) {
            Toast.makeText(this, "Please enter a valid name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isValidPhoneNumber(volunteerPhoneET.getText().toString())) {
            Toast.makeText(this, "Please enter a phone number following the 04XX XXX XXX format", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (volunteerDOBET.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Fill in your date of birth", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isValidDOB()) {
            Toast.makeText(EditVolunteerActivity.this, "Please fill in a valid date of birth", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (volunteerAddressET.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Fill in your address", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (volunteerGenderRG.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select your gender", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean isValidPhoneNumber(String phoneET) {
        phoneET = phoneET.replaceAll(" ", "");
        return (phoneET.length() == 8 || phoneET.length() == 10) && StringUtils.isNumericSpace(phoneET.toString());
    }

    private boolean isValidDOB() {
        Calendar currentCal = Calendar.getInstance();
        Calendar dob = Calendar.getInstance();
        String date = volunteerDOBET.getText().toString().trim();
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
        if (dob.before(currentCal)) {
            return true;
        } else {
            return false;
        }
    }

    public void addListeners() {
        // Listener for the Date Picker
        volunteerDOBET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int day, month, year;
                day = 1;
                month = 0;
                year = cal.get(Calendar.YEAR);

                DatePickerDialog dialog = new DatePickerDialog(
                        EditVolunteerActivity.this,
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
                volunteerDOBET.setText(currentDateString);
            }
        };

        volunteerPhoneET.addTextChangedListener(phoneNumberTextWatcher(volunteerPhoneET));
    }

    // Edits text being inputted to display phone number format
    public TextWatcher phoneNumberTextWatcher(final EditText phoneET) {
        return new TextWatcher() {
            int previousLength = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousLength = phoneET.getText().length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = phoneET.getText().toString();
                int currentLength = input.length();

                if ((previousLength < currentLength) && (currentLength == 4 || currentLength == 8)) {
                    phoneET.append(" ");
                }
            }
        };
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("accountType", "Volunteer");
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.editDetailsBackTV)
    public void editDetailsBackOnClick() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("accountType", "Volunteer");
        startActivity(intent);
        finish();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            animated = true;
        }
    }
}