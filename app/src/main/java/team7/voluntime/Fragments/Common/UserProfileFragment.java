package team7.voluntime.Fragments.Common;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team7.voluntime.Activities.EditCharityActivity;
import team7.voluntime.Activities.EditVolunteerActivity;
import team7.voluntime.Activities.MainActivity;
import team7.voluntime.Activities.VolunteerHistoryActivity;
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
    private DatabaseReference eventsReference;
    private Charity charity;
    private Volunteer volunteer;

    private final SimpleDateFormat format = new SimpleDateFormat("HH:mm");

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
    @BindView(R.id.userprofileRatingTV)
    TextView ratingTV;
    @BindView(R.id.userprofileTotalTimeTV)
    TextView totalTimeTV;
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

    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Profile");
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
        eventsReference = Utilities.getEventsReference(database);

        // Get Account type and pass it into getType() method to return correct string path
        DatabaseReference typeRef = database.getReference(getType());
        final DecimalFormat df = new DecimalFormat();

        typeRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        emailTV.setText(mUser.getEmail());
                        if (getType().equals("Volunteers")) {
                            volunteerLayout.setVisibility(VISIBLE);
                            volunteerReference.child("Profile").addListenerForSingleValueEvent(new ValueEventListener() {
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        volunteer = dataSnapshot.getValue(Volunteer.class);
                                        volunteer.setId(mUser.getUid());
                                        nameTV.setText(volunteer.getName());
                                        typeTV.setText("Volunteer");
                                        phoneTV.setText(volunteer.getPhoneNumber());
                                        addressTV.setText(volunteer.getAddress());
                                        genTV.setText(volunteer.getGender());
                                        dobTV.setText(volunteer.getDateOfBirth());
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.e(TAG, "The read failed: " + databaseError.getCode());
                                }
                            });

                            // User to calculate the average rating of the volunteer
                            volunteerReference.child("Ratings").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    int ratingsCount = 0;
                                    float sumOfRatings = 0;
                                    float average = 0;
                                    for (DataSnapshot rating : dataSnapshot.getChildren()) {
                                        if (rating.exists()) {
                                            ratingsCount++;
                                            sumOfRatings += Float.parseFloat(rating.child("rating").getValue().toString());
                                        }
                                    }
                                    average = ratingsCount > 0 ? sumOfRatings / ratingsCount : 0;
                                    df.setMaximumFractionDigits(2);
                                    ratingTV.setText("Current Rating is: " + df.format(average));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            // Used to get the total sum of volunteering hours completed by the volunteer
                            volunteerReference.child("Events").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        final long[] totalVolunteeringTime = new long[1];
                                        for (final DataSnapshot event : dataSnapshot.getChildren()) {
                                            if (event.getValue() != null && event.getValue().toString().equals("previous")) {
                                                eventsReference.child(event.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot eventSnapshot) {
                                                        if (eventSnapshot.exists() &&
                                                                eventSnapshot.child("startTime").getValue() != null &&
                                                                eventSnapshot.child("endTime").getValue() != null) {

                                                            String startTime = eventSnapshot.child("startTime").getValue().toString();
                                                            String endTime = eventSnapshot.child("endTime").getValue().toString();
                                                            long difference = 0;
                                                            try {
                                                                Date formattedStartTime = format.parse(startTime);
                                                                Date formattedEndTime = format.parse(endTime);
                                                                difference = (formattedEndTime.getTime() - formattedStartTime.getTime())/1000; // Gets difference in seconds
                                                                totalVolunteeringTime[0] += difference;

                                                                long hours = totalVolunteeringTime[0] / 3600;
                                                                long minutes = (totalVolunteeringTime[0] % 3600) / 60;
                                                                String timeString = String.format("%2d:%02d", hours, minutes);

                                                                totalTimeTV.setText("Total Volunteered Time: " + timeString);
                                                                Log.d(TAG, "Differences from " + formattedEndTime.getTime() + " and date 1: " + formattedStartTime.getTime() + " is: " + difference);
                                                                Log.d(TAG, "Total volunteering time is " + totalVolunteeringTime[0]);
                                                            } catch (ParseException e) {
                                                                Log.e(TAG, e.toString());
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            totalTimeTV.setVisibility(VISIBLE);
                            ratingTV.setVisibility(VISIBLE);

                        }



                        if (getType().equals("Charities")) {
                            charityLayout.setVisibility(VISIBLE);
                            charityReference.child("Profile").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull  DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        charity = dataSnapshot.getValue(Charity.class);
                                        charity.setId(mUser.getUid());
                                        nameTV.setText(charity.getName());
                                        typeTV.setText("Charity");
                                        phoneTV.setText(charity.getPhoneNumber());
                                        addressTV.setText(charity.getAddress());
                                        catTV.setText(charity.getCategory());
                                        descTV.setText(charity.getDescription());
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.e(TAG, "The read failed: " + databaseError.getCode());
                                }
                            });

                        }
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
    public void onClick() {
        Intent intent = new Intent(getContext(), getEditActivity());
        startActivity(intent);

    }


    public String getType() {
        String type = MainActivity.getAccountType();
        if (type.equals("Volunteer")) {
            return "Volunteers";
        } else {
            return "Charities";
        }
    }

    public Class getEditActivity() {
        String type = MainActivity.getAccountType();
        if (type.equals("Volunteer")) {
            return EditVolunteerActivity.class;
        } else {
            return EditCharityActivity.class;
        }
    }

    @OnClick(R.id.userprofileVolHisBtn)
    public void volunteerHistoryOnClick() {
        startActivity(new Intent(getContext(), VolunteerHistoryActivity.class));
    }
}
