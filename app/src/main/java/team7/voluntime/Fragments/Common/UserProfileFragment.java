package team7.voluntime.Fragments.Common;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team7.voluntime.Activities.MainActivity;
import team7.voluntime.Domains.Charity;
import team7.voluntime.Domains.Volunteer;
import team7.voluntime.R;
import team7.voluntime.Utilities.Utilities;

import static android.view.View.VISIBLE;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserProfileFragment extends Fragment {

    private final static String TAG = "UserProfileFragment";

    FirebaseUser mUser;
    private FirebaseDatabase database;
    private DatabaseReference charityReference;
    private DatabaseReference volunteerReference;
    private Charity charity;
    private Volunteer volunteer;

    // Bindings
    @BindView(R.id.userprofileCharityLayout)
    LinearLayout charityLayout;
    @BindView(R.id.userprofileVolunteerLayout)
    LinearLayout volunteerLayout;
    @BindView(R.id.userprofileCatTV)
    TextView catTV;
    @BindView(R.id.userprofileDescTV)
    TextView descTV;
    @BindView(R.id.userprofileNameTV)
    TextView nameTV;
    @BindView(R.id.userprofileTypeTV)
    TextView typeTV;
    @BindView(R.id.userprofileEmailTV)
    TextView emailTV;
    @BindView(R.id.userprofilePhoneTV)
    TextView phoneTV;
    @BindView(R.id.userprofileAddressTV)
    TextView addressTV;
    @BindView(R.id.userprofileGenTV)
    TextView genTV;
    @BindView(R.id.userprofileDobTV)
    TextView dobTV;

    // Local variables used in the store database values
    private String type;
    private String userName;
    private String dob;
    private String phone;
    private String address;
    private String gender;
    private String category;
    private String description;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user_profile, container, false);
        ButterKnife.bind(this, v);

        // Get Current logged in instance from database
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        charityReference = Utilities.getCharityReference(database, mUser.getUid());
        volunteerReference = Utilities.getVolunteerReference(database, mUser.getUid());

        // Get Account type and pass it into getType() method to return correct string path
        DatabaseReference typeRef = FirebaseDatabase.getInstance().getReference(getType());

        typeRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    emailTV.setText(mUser.getEmail());
                    if (getType().equals("Volunteers")) {
                        volunteerLayout.setVisibility(VISIBLE);
                        volunteerReference.child("Profile").addListenerForSingleValueEvent(new ValueEventListener() {
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                volunteer = dataSnapshot.getValue(Volunteer.class);
                                volunteer.setId(mUser.getUid());
                                nameTV.setText(volunteer.getName());
                                typeTV.setText("Volunteer");
                                phoneTV.setText(volunteer.getPhoneNumber());
                                addressTV.setText(volunteer.getAddress());
                                genTV.setText(volunteer.getGender());
                                dobTV.setText(volunteer.getDateOfBirth());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, "The read failed: " + databaseError.getCode());
                            }
                        });
                    }
                    if (getType().equals("Charities")) {
                        charityLayout.setVisibility(VISIBLE);
                        charityReference.child("Profile").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                charity = dataSnapshot.getValue(Charity.class);
                                charity.setId(mUser.getUid());
                                nameTV.setText(charity.getName());
                                typeTV.setText("Charity");
                                phoneTV.setText(charity.getPhoneNumber());
                                addressTV.setText(charity.getAddress());
                                catTV.setText(charity.getCategory());
                                descTV.setText(charity.getDescription());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, "The read failed: " + databaseError.getCode());
                            }
                        });

                    }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @OnClick(R.id.userprofileEditBtn)
    public void onClick(View arg0) {
        Toast.makeText(getContext(), "Function Not Complete", Toast.LENGTH_LONG).show();

    }


    public String getType() {
        String type = MainActivity.getAccountType();
        if (type.equals("Volunteer")) {
            return "Volunteers";
        } else {
            return "Charities";
        }
    }


}
