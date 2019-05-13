package team7.voluntime.Utilities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import team7.voluntime.Activities.EventDetailsActivity;
import team7.voluntime.Activities.EventRegisterActivity;
import team7.voluntime.Activities.VolunteerDetailsActivity;
import team7.voluntime.Domains.Event;
import team7.voluntime.Fragments.Charities.CharityViewEventsFragment;
import team7.voluntime.Fragments.Volunteers.VolunteerEventsListFragment;
import team7.voluntime.R;


public class EventListAdapter<T> extends ArrayAdapter<Event> {
    private static final String TAG = "EventListAdapter";
    private Context mContext;
    private T screen;
    int mResource;
    private String eventId;


    public EventListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Event> objects, T screen) {
        super(context, resource, objects);
        this.mContext = context;
        this.screen = screen;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        eventId = Objects.requireNonNull(getItem(position)).getId();

        String title = getItem(position).getTitle();
        String description = getItem(position).getDescription();
        String category = getItem(position).getCategory();
        String location = getItem(position).getLocation();
        String date = getItem(position).getDate();
        String createdTime = getItem(position).getCreatedTime();
        String organisers = getItem(position).getOrganisers();
        int minimum = getItem(position).getMinimum();
        int maximum = getItem(position).getMaximum();

        // TODO: Check how this works with volunteer page
        final String eventStatus = getItem(position).getEventStatus();

        final HashMap<String, String> volunteers = getItem(position).getVolunteers();

        final Event event = new Event(eventId, title, description, category, location, date, createdTime, organisers, minimum, maximum, volunteers);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView dateTV = convertView.findViewById(R.id.adapterEventDateTV);
        TextView titleTV = convertView.findViewById(R.id.adapterEventTitleTV);
        ImageView adapterEventIV1 = convertView.findViewById(R.id.adapterEventIV1);
        ImageView adapterEventIV2 = convertView.findViewById(R.id.adapterEventIV2);


        if (screen.getClass().equals(CharityViewEventsFragment.class)) {
            adapterEventIV2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(@NonNull View view) {
                    Intent intent = new Intent(mContext, EventDetailsActivity.class);
                    intent.putExtra("event", event);
                    intent.putExtra("parentActivity", CharityViewEventsFragment.class.toString());
                    // TODO: Event status is being used on event details to remove pending and registered list and will just show the attended volunteers
                    intent.putExtra("eventStatus", eventStatus);

                    intent.putExtra("volunteers", event.getVolunteers());
                    Log.d(TAG, "Event is: " + event.toString());
                    mContext.startActivity(intent);
                }

            });
        } else if (screen.getClass().equals(VolunteerDetailsActivity.class)) {
            adapterEventIV2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, EventDetailsActivity.class);
                    intent.putExtra("event", event);
                    intent.putExtra("parentActivity", VolunteerDetailsActivity.class.toString());
                    Log.d(TAG, "Event is: " + event.toString());
                    mContext.startActivity(intent);
                }
            });
        } else if (screen.getClass().equals(VolunteerEventsListFragment.class)) {
            adapterEventIV1.setVisibility(View.GONE);
            adapterEventIV2.setImageResource(R.drawable.ic_plus);
            adapterEventIV2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view != null) {
                        Log.d("Event Details on Click", event.toString());
                        Intent intent = new Intent(mContext, EventRegisterActivity.class);
                        intent.putExtra("event", event);
                        mContext.startActivity(intent);
                    }
                }
            });
        }

        dateTV.setText(date);
        titleTV.setText(title);
        return convertView;
    }
}

