package team7.voluntime.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team7.voluntime.Domains.Event;
import team7.voluntime.Domains.EventVolunteers;
import team7.voluntime.R;
import team7.voluntime.Utilities.Utilities;

public class EventDetailsActivity extends AppCompatActivity {

    private String[] coords;

    @BindView(R.id.eventDetailsTitleTV)
    TextView titleTV;

    @BindView(R.id.eventDetailsDescriptionTV)
    TextView descriptionTV;

    @BindView(R.id.eventDetailsCategoryTV)
    TextView categoryTV;

    @BindView(R.id.eventDetailsLocationTV)
    TextView locationTV;

    @BindView(R.id.eventDetailsDateTV)
    TextView dateTV;

    @BindView(R.id.eventDetailsCreatedTimeTV)
    TextView createdTimeTV;

    @BindView(R.id.eventDetailsOrganisersTV)
    TextView organisersTV;

    @BindView(R.id.eventDetailsMapIV)
    ImageView mapIV;

    private final static String TAG = "EventDetails";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Event event = (Event) intent.getParcelableExtra("event");
        EventVolunteers eventVolunteers = (EventVolunteers) intent.getParcelableExtra("eventVolunteers");

        if (event != null) {
            coords = event.getLocation().split(" ");

            titleTV.setText(event.getTitle());
            descriptionTV.setText(event.getDescription());
            categoryTV.setText(event.getCategory());
            dateTV.setText(event.getDate());
            createdTimeTV.setText(event.getCreatedTime());
            organisersTV.setText(event.getOrganisers());
            String address = "Location Unavailable";
            try {
                 address = Utilities.getLocation(
                        this,
                        Double.parseDouble(coords[0]),
                        Double.parseDouble(coords[1]))
                        .get(0).getAddressLine(0);
                 mapIV.setVisibility(View.VISIBLE);
            } catch(IndexOutOfBoundsException e) {
                Log.e(TAG, "An error occurred when passing location coords: " + event.getLocation());
                Log.e(TAG, e.toString());
            }
            locationTV.setText(address);
        }
        if (eventVolunteers != null) {
            Log.d(TAG, "Event Volunteers is: " + eventVolunteers.toString());
        } else {
            Log.d(TAG, "Event Volunteers is null");
        }
    }

    @OnClick(R.id.eventDetailsMapIV)
    public void mapOnClick() {
        Intent intent = new Intent(this, LocationActivity.class);
        intent.putExtra("latitude", Double.parseDouble(coords[0]));
        intent.putExtra("longitude", Double.parseDouble(coords[1]));
        intent.putExtra("address", locationTV.getText().toString());
        startActivity(intent);
    }

}
