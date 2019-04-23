package team7.voluntime.Fragments.Common;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import team7.voluntime.R;

import static android.view.View.VISIBLE;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserProfileFragment extends Fragment {

    private final static String TAG = "UserProfileFragment";

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
    @BindView(R.id.userprofileEmailTV)
    TextView phoneTV;
    @BindView(R.id.userprofileAddressTV)
    TextView addressTV;
    @BindView(R.id.userprofileGenTV)
    TextView genTV;
    @BindView(R.id.userprofileDescTV)
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
    // Local database reference
    private FirebaseUser mUser;


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

        // Get Account type and pass it into getType() method to return correct string path
        DatabaseReference typeRef = FirebaseDatabase.getInstance().getReference(getType());

        typeRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    if (getType().equals("Volunteers")) {
                        volunteerLayout.setVisibility(VISIBLE);
                        type = dataSnapshot.child("AccountType").getValue().toString();
                        userName = dataSnapshot.child("Profile").child("FullName").getValue().toString();
                        phone = dataSnapshot.child("Profile").child("PhoneNumber").getValue().toString();
                        address = dataSnapshot.child("Profile").child("Address").getValue().toString();

                        dob = dataSnapshot.child("Profile").child("DateOfBirth").getValue().toString();
                        dobTV.setText(dob);

                        gender = dataSnapshot.child("Profile").child("Gender").getValue().toString();
                        genTV.setText(gender);
                    }
                    if (getType().equals("Charities")) {
                        charityLayout.setVisibility(VISIBLE);
                        type = dataSnapshot.child("AccountType").getValue().toString();
                        userName = dataSnapshot.child("Profile").child("Name").getValue().toString();
                        address = dataSnapshot.child("Profile").child("Address").getValue().toString();
                        phone = dataSnapshot.child("Profile").child("PhoneNumber").getValue().toString();
                        category = dataSnapshot.child("Profile").child("Category").getValue().toString();
                        catTV.setText(category);
                        description = dataSnapshot.child("Profile").child("Description").getValue().toString();
                        descTV.setText(description);
                    }
                    nameTV.setText(userName);
                    typeTV.setText(type);
                    phoneTV.setText(phone);
                    addressTV.setText(address);
                    emailTV.setText(mUser.getEmail());

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
