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
import team7.voluntime.Domains.Event;
import team7.voluntime.R;
import team7.voluntime.Utilities.Utilities;


public class EditEventActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private Event Event;
    private boolean animated = false;

    @BindView(R.id.editEventTitleET)
    EditText eventNameET;

    @BindView(R.id.editEventLocationET)
    EditText eventLocationET;

    @BindView(R.id.editEventDateET)
    EditText eventDateET;
    
    
    
    @BindView(R.id.editeventOrganisersET)
    EditText eventDateET;
    
    @BindView(R.id.editeventMinimumET)
    EditText eventDateET;
    
    @BindView(R.id.editeventMaximumET)
    EditText eventDateET;
    
    

    @BindView(R.id.editEventDescriptionET)
    EditText eventDescriptionET;

    @BindView(R.id.editEventSV)
    ScrollView editeventSV;

    private final String TAG = "EditEventActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit_details);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        database = FirebaseDatabase.getInstance();

        reference = Utilities.getEventReference(database, user.getUid());

        ButterKnife.bind(this);

        

        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reference.child("Profile").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        event = dataSnapshot.getValue(event.class);
                        event.setId(user.getUid());
                        eventTitle.setText(event.getTitle());
                        eventDescriptionET.setText(event.getDescription());
                        eventLocationET.setText(event.getLocation());
                        eventDateET.setText(event.getDate());
                  
                        eventOrganisersET.setText(event.getOrganisers());
                        eventMinimumET.setText(event.getMinimum());
                        eventMaximumET.setText(event.getMaximum());
                        
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


    }

    @OnClick(R.id.editFinishEvent)
    public void setEventInfo() {
        if (checkPassedEvent()) {
            reference = database.getReference(“Events”).child(user.getUid());
            String title = eventTitleET.getText().toString().trim();
            String location = eventLocationET.getText().toString().trim();
            String description = eventDescriptionET.getText().toString().trim();
            String category = eventCategoryET.getText().toString().trim();
           
            String organisers = eventOrganisersET.getText().toString().trim();
            String date = eventDateET.getText().toString().trim();
            String minimum = eventMinimumET.getText().toString().trim();
            String maximum = eventMaximumET.getText().toString().trim();
            
            

            reference.child("Profile").child("title").setValue(title);
            reference.child("Profile").child("location").setValue(location);
            
            reference.child("Profile").child("description").setValue(description);
            reference.child("Profile").child("category").setValue(category);
            reference.child("Profile").child("date").setValue(date);
            reference.child("Profile").child("organisers").setValue(organisers);
            reference.child("Profile").child("minimum").setValue(minimum);
            reference.child("Profile").child("maximum").setValue(maximum);

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("accountType", "Event");
            startActivity(intent);
            finish();
        }
    }

    public boolean checkValidEvent() {
        Log.d(TAG, "Check all fields being called");
        if (eventTitleET.getText().toString().trim().isEmpty() || !StringUtils.isAlphaSpace(eventTitleET.getText().toString())) {
            Toast.makeText(this, "Please enter a valid name for event“, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (eventLocationET.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter your location for event“, Toast.LENGTH_SHORT).show();
            return false;
        }
                
        if (eventCategoryET.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter a description for your event”, Toast.LENGTH_SHORT).show();
            return false;
        }
        
if (eventDescriptionET.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter a description for your event”, Toast.LENGTH_SHORT).show();
            return false;
        }

if (eventDateET.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter Event date”, Toast.LENGTH_SHORT).show();
            return false;
        }
if (eventMinimumET.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Fill in a valid number of minimum attendees”, Toast.LENGTH_SHORT).show();
            return false;
        }
if (eventMaximumET.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Fill in a valid number of maximum attendees”, Toast.LENGTH_SHORT).show();
            return false;
        }
int minNum = Integer.parseInt(eventMinimumET.getText().toString().trim());
int maxNum = Integer.parseInt(eventMaximumET.getText().toString().trim());

        if (maxNum < minNum) {
            Toast.makeText(this, "Your minimum number of attendees must be less than or equal to your maximum", Toast.LENGTH_SHORT).show();
            return false;
        return true;
    }

   

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("accountType", “Event”);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.editDetailsBackTV)
    public void editDetailsBackOnClick() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("accountType", “Event”);
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
