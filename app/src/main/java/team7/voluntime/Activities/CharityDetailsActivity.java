package team7.voluntime.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import team7.voluntime.R;

public class CharityDetailsActivity extends AppCompatActivity {
    private static String TAG = "CharityDetailsActivity";
    @BindView(R.id.charityDetailsNameTV)
    TextView charityDetailsNameTV;
    @BindView(R.id.charityDetailCategoryTV)
    TextView charityDetailsCatTV;
    @BindView(R.id.charityDetailsEmailTV)
    TextView charityDetailsEmailTV;
    @BindView(R.id.charityDetailsPhoneTV)
    TextView charityDetailsPhoneTV;
    @BindView(R.id.charityDetailsAddressTV)
    TextView charityDetailsAddressTV;
    @BindView(R.id.charityDetailsDescTV)
    TextView charityDetailsDescTV;
    private FirebaseUser mUser;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private String Charity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_details);
        ButterKnife.bind(this);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();


        Bundle bundle = getIntent().getExtras();
        if (bundle.getString("ID") != null) {
            Charity = bundle.getString("ID");
            reference = database.getReference("Charities").child(Charity);
            reference.child("Profile").addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                            charityDetailsNameTV.setText(dataSnapshot.child("name").getValue().toString());
                            charityDetailsCatTV.setText(dataSnapshot.child("category").getValue().toString());
                            charityDetailsPhoneTV.setText(dataSnapshot.child("phoneNumber").getValue().toString());
                            charityDetailsAddressTV.setText(dataSnapshot.child("address").getValue().toString());
                            charityDetailsDescTV.setText(dataSnapshot.child("description").getValue().toString());
                        }

                        @Override
                        public void onCancelled(@NotNull DatabaseError databaseError) {
                        }
                    }
            );

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

}
