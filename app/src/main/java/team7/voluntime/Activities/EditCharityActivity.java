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

public class EditCharityActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private boolean animated = false;

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

    @BindView(R.id.editCharitySV)
    ScrollView editCharitySV;

    private final String TAG = "EditCharityActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_edit_details);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        database = FirebaseDatabase.getInstance();

        ButterKnife.bind(this);

        charityPhoneET.addTextChangedListener(phoneNumberTextWatcher(charityPhoneET));
    }

    public boolean isValidPhoneNumber(String phoneET) {
        phoneET = phoneET.replaceAll(" ", "");
        return (phoneET.length() == 8 || phoneET.length() == 10) && StringUtils.isNumericSpace(phoneET.toString());
    }


    @OnClick(R.id.editFinishCharityTV)
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
            reference.child("AccountType").setValue("Charity");

            Intent intent = new Intent(EditCharityActivity.this, MainActivity.class);
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
        if (!isValidPhoneNumber(charityPhoneET.getText().toString())) {
            Toast.makeText(this, "Please enter a phone number following the 04XX XXX XXX format", Toast.LENGTH_SHORT).show();
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
        intent.putExtra("AccountType", "Charity");
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.editDetailsBackTV)
    public void editDetailsBackOnClick() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("AccountType", "Charity");
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