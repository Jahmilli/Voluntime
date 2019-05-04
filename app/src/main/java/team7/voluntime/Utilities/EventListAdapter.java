package team7.voluntime.Utilities;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import team7.voluntime.Activities.EventDetailsActivity;
import team7.voluntime.Domains.Event;
import team7.voluntime.R;


public class EventListAdapter extends ArrayAdapter<Event> {
    private static final String TAG = "EventListAdapter";
    private Context mContext;
    private Fragment fragment;
    int mResource;
    private String eventId;
    private AlertDialog.Builder declineAlertBuilder;
    private AlertDialog declineAlert;

    public EventListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Event> objects, Fragment fragment) {
        super(context, resource, objects);
        this.mContext = context;
        this.fragment = fragment;
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
        final HashMap<String, String> volunteers =  getItem(position).getVolunteers();

        final Event event = new Event(eventId, title, description, category, location, date, createdTime, organisers, minimum, maximum, volunteers);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView dateTV = (TextView) convertView.findViewById(R.id.adapterEventDateTV);
        TextView titleTV = (TextView) convertView.findViewById(R.id.adapterEventTitleTV);
        ImageView adapterEventIV2 = (ImageView) convertView.findViewById(R.id.adapterEventIV2);

        if (adapterEventIV2 != null) {
            adapterEventIV2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(@NonNull View view) {
                    Intent intent = new Intent(mContext, EventDetailsActivity.class);
                    intent.putExtra("event", (Parcelable) event);
                    intent.putExtra("volunteers", event.getVolunteers());
                    Log.d(TAG, "Event is: " + event.toString());
                    mContext.startActivity(intent);
                }

            });
        }


        dateTV.setText(date);
        titleTV.setText(title);
        return convertView;
    }
}
