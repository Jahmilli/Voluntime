package team7.voluntime.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import team7.voluntime.Domains.Event;
import team7.voluntime.R;
import team7.voluntime.Utilities.Utilities;

public class EventDetailsActivity extends AppCompatActivity {

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


    private final static String TAG = "EventDetails";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        ButterKnife.bind(this);

        Intent i = getIntent();
        Event event = (Event) i.getParcelableExtra("event");

        if (event != null) {
            String[] coords = event.getLocation().split(" ");

            titleTV.setText(event.getTitle());
            descriptionTV.setText(event.getDescription());
            categoryTV.setText(event.getCategory());
            dateTV.setText(event.getDate());
            createdTimeTV.setText(event.getCreatedTime());
            organisersTV.setText(event.getOrganisers());
            String address = Utilities.getLocation(
                    this,
                    Double.parseDouble(coords[0]),
                    Double.parseDouble(coords[1]))
                    .get(0).getAddressLine(0);
            locationTV.setText(address);





        }
    }


}
