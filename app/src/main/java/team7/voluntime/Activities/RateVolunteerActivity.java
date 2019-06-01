package team7.voluntime.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team7.voluntime.Domains.Event;
import team7.voluntime.Domains.Rating;
import team7.voluntime.Domains.Volunteer;
import team7.voluntime.R;
import team7.voluntime.Utilities.Utilities;

public class RateVolunteerActivity extends AppCompatActivity {
    private final static String TAG = "RateVolunteer";

    private Volunteer volunteer;
    private DatabaseReference reference;
    private FirebaseDatabase database;
    private Rating rating;
    private Event event;
    private boolean isPastEvent;

    // Bindings
    @BindView(R.id.rateVolunteerNameTV)
    TextView nameTV;
    @BindView(R.id.rateVolunteerRatingBar)
    RatingBar ratingBar;
    @BindView(R.id.rateVolunteerCommentET)
    EditText commentET;
    @BindView(R.id.rateVolunteerCommentTV)
    TextView commentTV;
    @BindView(R.id.rateVolunteerSubmitBtn)
    Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_volunteer);
        ButterKnife.bind(this);
        database = FirebaseDatabase.getInstance();

        Intent intent = getIntent();
        volunteer = intent.getParcelableExtra("volunteer");
        event = intent.getParcelableExtra("event");
        isPastEvent = intent.getBooleanExtra("isPastEvent", false);
        if (isPastEvent) {
            ratingBar.setIsIndicator(true);
            commentET.setVisibility(View.GONE);
            commentTV.setVisibility(View.VISIBLE);
            submitBtn.setVisibility(View.GONE);
        }

        nameTV.setText(volunteer.getName());
        reference = database.getReference("Volunteers").child(volunteer.getId()).child("Ratings").child(event.getId());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                rating = dataSnapshot.getValue(Rating.class);
                if (rating != null) {
                    ratingBar.setRating(rating.getRating());
                    if (isPastEvent) {
                        commentTV.setText(rating.getComment());
                    } else {
                        commentET.setText(rating.getComment());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean checkFields() {
        if (ratingBar.getRating() == 0) {
            Toast.makeText(this, "Please fill in a rating", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (commentET.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please fill in a comment", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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

    @OnClick(R.id.rateVolunteerSubmitBtn)
    public void submitOnClick() {
        Log.d(TAG, "Rating is " + ratingBar.getRating());
        if (checkFields()) {
            double rating = ratingBar.getRating();
            Map rateVolunteer = new HashMap();
            rateVolunteer.put("charityId", event.getOrganisers());
            rateVolunteer.put("eventId", event.getId());
            rateVolunteer.put("rating", rating);
            rateVolunteer.put("comment", commentET.getText().toString().trim());
            rateVolunteer.put("dateRated", Utilities.getCurrentDate());
            reference.setValue(rateVolunteer);
            finish();
        }
    }
}