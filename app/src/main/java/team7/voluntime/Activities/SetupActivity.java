package team7.voluntime.Activities;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team7.voluntime.R;

public class SetupActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private boolean animated = false;
    private int currentPage = 1;
    private boolean isVolunteer = false;
    private String charityKey = "1234";
    private boolean isValidInput = false;

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

    @BindView(R.id.introSetupTV)
    TextView introSetupTV;

    @BindView(R.id.charityNameET)
    EditText charityNameET;

    @BindView(R.id.charityAddressET)
    EditText charityAddressET;

    @BindView(R.id.charityPhoneET)
    EditText charityPhoneET;

    @BindView(R.id.charityDescriptionET)
    EditText charityDescriptionET;

    @BindView(R.id.charityCategoryET)
    EditText charityCategoryET;

    @BindView(R.id.setupFirstVolunteerSV)
    ScrollView setupFirstVolunteerSV;

    @BindView(R.id.setupSecondVolunteerSV)
    ScrollView setupSecondVolunteerSV;

    @BindView(R.id.setupFirstCharitySV)
    ScrollView setupFirstCharitySV;

    @BindView(R.id.selectSetupLL)
    LinearLayout selectSetupLL;

    private final String TAG = "SetupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        database = FirebaseDatabase.getInstance();

        ButterKnife.bind(this);
        addListeners();

        animateWelcome();
    }

    public void charitySetupInit() {
        isVolunteer = false;
        selectSetupLL.setVisibility(View.GONE);
        setupFirstVolunteerSV.setVisibility(View.GONE);
        setupSecondVolunteerSV.setVisibility(View.GONE);
        currentPage = 2;
    }

    @OnClick(R.id.selectVolunteerTV)
    public void volunteerSetupInit() {
        isVolunteer = true;
        selectSetupLL.setVisibility(View.GONE);
        setupFirstCharitySV.setVisibility(View.GONE);
        currentPage = 2;
    }

    @OnClick({R.id.setupInitCharity, R.id.setupInitVolunteer})
    public void returnToInit() {
        selectSetupLL.setVisibility(View.VISIBLE);
        setupFirstCharitySV.setVisibility(View.VISIBLE);
        setupFirstVolunteerSV.setVisibility(View.VISIBLE);
        setupSecondVolunteerSV.setVisibility(View.VISIBLE);
        currentPage = 1;
    }

    @OnClick(R.id.setupNextVolunteer)
    public void toNextPageVolunteer() {
        if (checkPassedFirstVolunteer()) {
            setupFirstVolunteerSV.setVisibility(View.GONE);
            currentPage = 3;
        }
    }

    @OnClick(R.id.setupBackVolunteer)
    public void toPreviousPageVolunteer() {
        setupFirstVolunteerSV.setVisibility(View.VISIBLE);
        currentPage = 2;
    }

    @OnClick(R.id.setupFinishVolunteer)
    public void setVolunteerInfo() {
        // TODO: Decide whether there will be a second screen
//        if (checkPassedSecondVolunteer()) {
            reference = database.getReference("Users").child(user.getUid());
            String name = volunteerNameET.getText().toString().trim();
            String email = mAuth.getCurrentUser().getEmail();
            String date = volunteerDOBET.getText().toString().trim();
            String phoneNumber = volunteerPhoneET.getText().toString().trim();
            String address = volunteerAddressET.getText().toString().trim();
            RadioButton radioButton = findViewById(volunteerGenderRG.getCheckedRadioButtonId());
            String gender = radioButton.getText().toString().trim();

            reference.child("Profile").child("FullName").setValue(name);
            reference.child("Profile").child("Email").setValue(email);
            reference.child("Profile").child("PhoneNumber").setValue(phoneNumber);
            reference.child("Profile").child("DateOfBirth").setValue(date);
            reference.child("Profile").child("Address").setValue(address);
            reference.child("Profile").child("Gender").setValue(address);


        reference.child("setupComplete").setValue(true);
            
            // TODO: Remove this as charities and volunteers wont come under the same 'User' Profile
            reference.child("AccountType").setValue("Volunteer");

            Intent intent = new Intent(SetupActivity.this, MainActivity.class);
            intent.putExtra("AccountType", "Volunteer");
            startActivity(intent);
            finish();
//        }
    }

    public boolean checkPassedFirstVolunteer() {
        Log.d(TAG, "Check all fields being called");

        if (volunteerNameET.getText().toString().trim().isEmpty() || !StringUtils.isAlphaSpace(volunteerNameET.getText().toString())) {
            Toast.makeText(this, "Please enter a valid name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (volunteerPhoneET.getText().toString().replaceAll(" ", "").length() != 10 || !StringUtils.isNumericSpace(volunteerPhoneET.getText().toString())) {
            Toast.makeText(this, "Please enter a mobile number following the 04XX XXX XXX format", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (volunteerDOBET.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Fill in your date of birth", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isValidDOB()) {
            Toast.makeText(SetupActivity.this, "Please fill in a valid date of birth", Toast.LENGTH_SHORT).show();
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

//    public boolean checkPassedSecondVolunteer() {
//        Log.d(TAG, "Check all fields being called");
//        if (allergiesET.getText().toString().trim().isEmpty()) {
//            allergiesET.setText("N/A");
//        }
//        if (medicationET.getText().toString().trim().isEmpty()) {
//            medicationET.setText("N/A");
//        }
//        return true;
//    }

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
        if(dob.before(currentCal))
            return true;
        else
            return false;
    }

    @OnClick(R.id.setupFinishCharity)
    public void setCharityInfo() {
        if (checkPassedCharity()) {
            reference = database.getReference("Charities").child(user.getUid());
            String name = charityNameET.getText().toString().trim();
            String address = charityAddressET.getText().toString().trim();
            String phoneNumber = charityPhoneET.getText().toString().trim();
            String description = charityDescriptionET.getText().toString().trim();
            String category = charityCategoryET.getText().toString().trim();

            reference.child("Profile").child("Name").setValue(name);
            reference.child("Profile").child("Address").setValue(address);
            reference.child("Profile").child("PhoneNumber").setValue(phoneNumber);
            reference.child("Profile").child("Description").setValue(description);
            reference.child("Profile").child("Category").setValue(category);

            reference.child("SetupComplete").setValue(true);
            reference.child("AccountType").setValue("charity");

            Intent intent = new Intent(SetupActivity.this, MainActivity.class);
            intent.putExtra("AccountType", "Charity");
            startActivity(intent);
            finish();
        }
    }

    public boolean checkPassedCharity() {
        Log.d(TAG, "Check all fields being called");
        if (charityNameET.getText().toString().trim().isEmpty() || !StringUtils.isAlphaSpace(charityNameET.getText().toString())) {
            Toast.makeText(this, "Please enter a valid name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (charityAddressET.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter your address", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (charityPhoneET.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (charityDescriptionET.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter a description for your charity", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (charityCategoryET.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter at least one category that describes your charity", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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
                        SetupActivity.this,
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
        charityPhoneET.addTextChangedListener(phoneNumberTextWatcher(charityPhoneET));
    }

    // Edits text being inputted to follow phone number format
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

                if ((previousLength < currentLength) && (currentLength == 4 || currentLength == 8))
                    phoneET.append(" ");
            }
        };
    }

    @Override
    public void onBackPressed() {
        if (currentPage == 1)
            logOut();
        else if (currentPage == 2)
            returnToInit();
        else if (currentPage == 3 && isVolunteer)
            toPreviousPageVolunteer();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            animated = true;
        }
    }

    public void animateWelcome() {
        if (!animated && currentPage == 1) {
            introSetupTV.setVisibility(View.INVISIBLE);

            introSetupTV.postDelayed(new Runnable() {
                @Override
                public void run() {
                    introSetupTV.setVisibility(View.VISIBLE);
                }
            }, 750);

            introSetupTV.postDelayed(new Runnable() {
                @Override
                public void run() {
                    introSetupTV.setVisibility(View.GONE);
                }
            }, 2000);
            animated = true;
        }
    }

    public void logOut() {
        mAuth.signOut();
        finish();
    }

    // TODO: Decide whether this is the best way to validate charities
    @OnClick(R.id.selectCharityTV)
    public void requestCharityKey() {
        AlertDialog.Builder getKeyBuilder = new AlertDialog.Builder(this);
        getKeyBuilder.setTitle("Charity Key");
        getKeyBuilder.setMessage("Please enter the key you have been provided to register your Charity");
        View dialogView = (SetupActivity.this.getLayoutInflater()).inflate(R.layout.dialog_charity_key, null);
        final EditText charityKeyET = dialogView.findViewById(R.id.charityKeyET);
        getKeyBuilder.setView(dialogView);

        getKeyBuilder.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (charityKeyET.getText().toString().trim().equals(charityKey))
                            charitySetupInit();
                        else
                            Toast.makeText(getApplicationContext(), "The entered key is incorrect", Toast.LENGTH_SHORT).show();
                    }
                });

        getKeyBuilder.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        getKeyBuilder.show();
    }
}