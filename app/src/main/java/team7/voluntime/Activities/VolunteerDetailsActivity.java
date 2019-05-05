package team7.voluntime.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import team7.voluntime.Domains.Charity;
import team7.voluntime.Domains.Volunteer;
import team7.voluntime.R;
import team7.voluntime.Utilities.Utilities;

public class VolunteerDetailsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    
    private static String TAG = "VolunteerDetailsActivity";

    FirebaseUser mUser;
    private FirebaseDatabase database;
    private DatabaseReference charityReference;
    private DatabaseReference volunteerReference;
    private Charity charity;
    private Volunteer volunteer;

    // Bindings
    @BindView(R.id.volunteerDetailsVolunteerLayout)
    LinearLayout volunteerLayout;
    @BindView(R.id.volunteerDetailsNameTV)
    TextView nameTV;
    @BindView(R.id.volunteerDetailsTypeTV)
    TextView typeTV;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_details);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = getIntent();
        Volunteer volunteer = (Volunteer) intent.getParcelableExtra("volunteer");
        if (volunteer != null) {
            nameTV.setText(volunteer.getName());
            phoneTV.setText(volunteer.getPhoneNumber());
            addressTV.setText(volunteer.getAddress());
            dobTV.setText(volunteer.getDateOfBirth());
            genderTV.setText(volunteer.getGender());
        }

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
}