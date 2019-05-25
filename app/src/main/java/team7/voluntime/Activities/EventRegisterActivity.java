package team7.voluntime.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import butterknife.OnClick;
import team7.voluntime.Domains.Event;
import team7.voluntime.R;
import team7.voluntime.Utilities.Constants;
import team7.voluntime.Utilities.Utilities;

public class EventRegisterActivity extends AppCompatActivity {

    private final static String TAG = "EventRegister";

    protected String[] coords;
    private FirebaseDatabase database;
    private DatabaseReference eReference;
    private DatabaseReference vReference;
    private DatabaseReference reference;
    private FirebaseUser mUser;
    private String eventID;
    private String charityID;
    private String charityName;
    private DatabaseReference nReference;

    @BindView(R.id.eventRegisterEventHostTV)
    TextView eventRegisterEventHostTV;
    @BindView(R.id.eventRegisterStatusTV)
    TextView eventRegisterStatusTV;

    @BindView(R.id.eventRegisterEventNameTV)
    TextView eventRegisterEventNameTV;
    @BindView(R.id.eventRegisterButton)
    Button eventRegisterButton;
    @BindView(R.id.eventRegisterEventDescriptionTV)
    TextView eventRegisterEventDescriptionTV;
    @BindView(R.id.eventRegisterEventAddressTV)
    TextView eventRegisterEventAddressTV;
    @BindView(R.id.eventRegisterEventDateTV)
    TextView eventRegisterEventDateTV;
    @BindView(R.id.eventRegisterEventStartTimeTV)
    TextView startTimeTV;
    @BindView(R.id.eventRegisterEventEndTimeTV)
    TextView endTimeTV;
    @BindView(R.id.eventCancelRegisterButton)
    Button eventCancelButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_register);
        ButterKnife.bind(this);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();

        Intent intent = getIntent();
        Event event = intent.getParcelableExtra("event");

        eventID = event.getId();
        charityID = event.getOrganisers();
        nReference = database.getReference("Charities").child(charityID);
        nReference.child("Profile").child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                            charityName = dataSnapshot.getValue().toString();
                            eventRegisterEventHostTV.setText(charityName);
                        }
                    }

                    @Override
                    public void onCancelled(@NotNull DatabaseError databaseError) {
                    }
                }
        );

        coords = event.getLocation().split(" ");
        eventRegisterEventNameTV.setText(event.getTitle());
        eventRegisterEventDescriptionTV.setText(event.getDescription());
        eventRegisterEventDateTV.setText(event.getDate());
        startTimeTV.setText(event.getStartTime());
        endTimeTV.setText(event.getEndTime());
        String address = "Location Unavailable";
        try {
            address = Utilities.getLocation(
                    this,
                    Double.parseDouble(coords[0]),
                    Double.parseDouble(coords[1]))
                    .get(0).getAddressLine(0);
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "An error occurred when passing location coords: " + event.getLocation());
            Log.e(TAG, e.toString());
        }
        eventRegisterEventAddressTV.setText(address);
        eventRegisterStatusTV.setText("You are not registered for this event");

        reference = database.getReference("Volunteers").child(mUser.getUid());
        reference.child("Events").child(eventID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    String status = dataSnapshot.getValue().toString();
                    switch (status) {
                        case Constants.EVENT_PENDING:
                            eventRegisterStatusTV.setText("You have registered for this event and are currently pending");
                            eventRegisterButton.setVisibility(View.GONE);
                            eventCancelButton.setVisibility(View.VISIBLE);
                            break;
                        case Constants.EVENT_REGISTERED:
                            eventRegisterStatusTV.setText("You are registered and approved for this event!");
                            eventRegisterButton.setVisibility(View.GONE);
                            eventCancelButton.setVisibility(View.VISIBLE);
                            break;
                        // Need to figure cancelled out a bit more...
                        case Constants.EVENT_CANCELLED:
                            eventRegisterStatusTV.setText("You have cancelled your registration for this event");
                            eventCancelButton.setVisibility(View.GONE);
                            eventRegisterButton.setVisibility(View.VISIBLE);
                            break;
                        case Constants.EVENT_REJECTED:
                            eventRegisterStatusTV.setText("You have been rejected from this event");
                            eventCancelButton.setVisibility(View.GONE);
                            eventRegisterButton.setVisibility(View.GONE);
                            break;
                        default:
                            eventRegisterStatusTV.clearComposingText();
                            eventCancelButton.setVisibility(View.GONE);

                    }
                }
            }
            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @OnClick(R.id.eventRegisterMapIV)
    public void mapOnClick() {
        Intent intent = new Intent(this, LocationActivity.class);
        intent.putExtra("latitude", Double.parseDouble(coords[0]));
        intent.putExtra("longitude", Double.parseDouble(coords[1]));
        intent.putExtra("address", eventRegisterEventAddressTV.getText().toString());
        startActivity(intent);
    }

    public void eventRegisterConfirm(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Registration");
        builder.setMessage("Are you sure you wish to register for ");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                vReference = database.getReference("Volunteers").child(mUser.getUid());
                vReference.child("Events").child(eventID).setValue(Constants.EVENT_PENDING);
                eReference = database.getReference("Events").child(eventID);
                eReference.child("Volunteers").child(mUser.getUid()).setValue(Constants.EVENT_PENDING);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void eventRegisterCancel(View view) {
        vReference = database.getReference("Volunteers").child(mUser.getUid());

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Cancellation");
        builder.setMessage("Once cancelled you will NO longer be able to re-register for this event ");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                vReference.child("Events").child(eventID).setValue(Constants.EVENT_CANCELLED);
                eReference = database.getReference("Events").child(eventID);
                eReference.child("Volunteers").child(mUser.getUid()).setValue(Constants.EVENT_CANCELLED);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    @OnClick(R.id.eventRegisterEventHostTV)
    public void onClickHost() {
        Intent intent = new Intent(getBaseContext(), CharityDetailsActivity.class);
        intent.putExtra("ID", charityID);
        startActivity(intent);
    }

}
