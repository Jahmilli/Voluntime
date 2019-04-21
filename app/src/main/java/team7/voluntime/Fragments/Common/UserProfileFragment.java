package team7.voluntime.Fragments.Common;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

    //Local variables used in the store database values
    private String Type;
    private String UserName;
    private String DOB;
    private String Phone;
    private String Address;
    @BindView(R.id.charityStuff)
    LinearLayout charityStuff;
    @BindView(R.id.uCat)
    TextView uCat;
    @BindView(R.id.uDesc)
    TextView uDesc;
    private String GEN;
    private String Category;
    private String Desc;

    //Bindings
    @BindView(R.id.edit)
    ImageView editLogo;

    @BindView(R.id.volunteerStuff)
    LinearLayout volunteerStuff;
    //Local database refrence
    private FirebaseAuth mAuth;

    @BindView(R.id.uName)
    TextView uName;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser mUser;

    @BindView(R.id.uType)
    TextView uType;

    @BindView(R.id.uEmail)
    TextView uEmail;

    @BindView(R.id.uPhone)
    TextView uPhone;

    @BindView(R.id.uAddress)
    TextView uAddress;

    @BindView(R.id.uGen)
    TextView uGen;

    @BindView(R.id.uDob)
    TextView uDob;


    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user_profile, container, false);
        ButterKnife.bind(this, v);

        //Get Current logedin instance from database
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        //Get Accoount type and pass it into getType() method to return corects string path
        DatabaseReference typeRef = FirebaseDatabase.getInstance().getReference(getType());

        typeRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    if (getType().equals("Volunteers")) {
                        volunteerStuff.setVisibility(VISIBLE);
                        Type = dataSnapshot.child("AccountType").getValue().toString();
                        UserName = dataSnapshot.child("Profile").child("FullName").getValue().toString();
                        Phone = dataSnapshot.child("Profile").child("PhoneNumber").getValue().toString();
                        Address = dataSnapshot.child("Profile").child("Address").getValue().toString();

                        DOB = dataSnapshot.child("Profile").child("DateOfBirth").getValue().toString();
                        uDob.setText(DOB);

                        GEN = dataSnapshot.child("Profile").child("Gender").getValue().toString();
                        uGen.setText(GEN);
                    }
                    if (getType().equals("Charities")) {
                        charityStuff.setVisibility(VISIBLE);
                        Type = dataSnapshot.child("AccountType").getValue().toString();
                        UserName = dataSnapshot.child("Profile").child("Name").getValue().toString();
                        Address = dataSnapshot.child("Profile").child("Address").getValue().toString();
                        Phone = dataSnapshot.child("Profile").child("PhoneNumber").getValue().toString();
                        Category = dataSnapshot.child("Profile").child("Category").getValue().toString();
                        uCat.setText(Category);
                        Desc = dataSnapshot.child("Profile").child("Description").getValue().toString();
                        uDesc.setText(Desc);
                    }
                    uName.setText(UserName);
                    uType.setText(Type);
                    uPhone.setText(Phone);
                    uAddress.setText(Address);
                    uEmail.setText(mUser.getEmail());

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

    @OnClick(R.id.edit)
    public void onClick(View arg0) {
        Toast.makeText(getContext(), "Function Not Complete", Toast.LENGTH_LONG).show();

    }


    public String getType() {
        String type = MainActivity.getAccountType();
        if (type.equals("Volunteer")) return "Volunteers";
        else return "Charities";
    }


}
