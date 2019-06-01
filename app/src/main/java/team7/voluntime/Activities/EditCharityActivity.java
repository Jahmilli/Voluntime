package team7.voluntime.Activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team7.voluntime.Domains.Charity;
import team7.voluntime.R;
import team7.voluntime.Utilities.Utilities;


public class EditCharityActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private Charity charity;
    private boolean animated = false;

    @BindView(R.id.editCharityNameET)
    EditText charityNameET;

    @BindView(R.id.editCharityAddressET)
    EditText charityAddressET;

    @BindView(R.id.editCharityPhoneET)
    EditText charityPhoneET;

    @BindView(R.id.editCharityDescriptionET)
    EditText charityDescriptionET;

    @BindView(R.id.editCharityCategoryET)
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

        reference = Utilities.getCharityReference(database, user.getUid());

        ButterKnife.bind(this);

        charityPhoneET.addTextChangedListener(phoneNumberTextWatcher(charityPhoneET));


        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reference.child("Profile").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        charity = dataSnapshot.getValue(Charity.class);
                        charity.setId(user.getUid());
                        charityNameET.setText(charity.getName());
                        charityPhoneET.setText(charity.getPhoneNumber());
                        charityAddressET.setText(charity.getAddress());
                        charityCategoryET.setText(charity.getCategory());
                        charityDescriptionET.setText(charity.getDescription());
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

    public boolean isValidPhoneNumber(String phoneET) {
        phoneET = phoneET.replaceAll(" ", "");
        return (phoneET.length() == 8 || phoneET.length() == 10) && StringUtils.isNumericSpace(phoneET);
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

            reference.child("Profile").child("name").setValue(name);
            reference.child("Profile").child("address").setValue(address);
            reference.child("Profile").child("phoneNumber").setValue(phoneNumber);
            reference.child("Profile").child("description").setValue(description);
            reference.child("Profile").child("category").setValue(category);

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("accountType", "Charity");
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
        intent.putExtra("accountType", "Charity");
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