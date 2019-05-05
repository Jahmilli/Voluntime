package team7.voluntime.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import team7.voluntime.Domains.Event;
import team7.voluntime.R;
import team7.voluntime.Utilities.Utilities;

public class EventRegisterActivity extends AppCompatActivity {

    private final static String TAG = "EventRegister";
    FirebaseUser mUser;
    @BindView(R.id.eventRegisterEventNameTV)
    TextView eventRegisterEventNameTV;
    @BindView(R.id.eventRegisterEventDescriptionTV)
    TextView eventRegisterEventDescriptionTV;
    @BindView(R.id.eventRegisterEventAddressTV)
    TextView eventRegisterEventAddressTV;
    @BindView(R.id.eventRegisterEventDateTV)
    TextView eventRegisterEventDateTV;
    private FirebaseDatabase database;
    private String[] coords;
    private String eventName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_register);
        ButterKnife.bind(this);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        Event event = intent.getParcelableExtra("event");
        //eventName.equals(event.getTitle());

        coords = event.getLocation().split(" ");
        eventRegisterEventNameTV.setText(event.getTitle());
        eventRegisterEventDescriptionTV.setText(event.getDescription());
        eventRegisterEventDateTV.setText(event.getDate());
        String address = "Location Unavailable";
        try {
            address = Utilities.getLocation(
                    this,
                    Double.parseDouble(coords[0]),
                    Double.parseDouble(coords[1]))
                    .get(0).getAddressLine(0);
            //mapIV.setVisibility(View.VISIBLE);
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "An error occurred when passing location coords: " + event.getLocation());
            Log.e(TAG, e.toString());
        }
        eventRegisterEventAddressTV.setText(address);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    public void eventRegisterConfirm(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Registration");
        builder.setMessage("Are you sure you wish to register for ");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getBaseContext(), mUser.getUid(), Toast.LENGTH_SHORT).show();
                //TODO: Add ID to Database
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //  Action for 'NO' Button
                //Toast.makeText(getBaseContext(), "negative button", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });
        builder.show();
    }

}
