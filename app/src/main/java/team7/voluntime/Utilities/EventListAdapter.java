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

import java.util.ArrayList;

import team7.voluntime.Activities.EventDetailsActivity;
import team7.voluntime.Domains.Event;
import team7.voluntime.Domains.EventVolunteers;
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
        eventId = getItem(position).getId();

        String title = getItem(position).getTitle();
        String description = getItem(position).getDescription();
        String category = getItem(position).getCategory();
        String location = getItem(position).getLocation();
        String date = getItem(position).getDate();
        String createdTime = getItem(position).getCreatedTime();
        String organisers = getItem(position).getOrganisers();
        EventVolunteers eventVolunteers = getItem(position).getEventVolunteers();

        final Event event = new Event(eventId, title, description, category, location, date, createdTime, organisers, eventVolunteers);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView dateTV = (TextView) convertView.findViewById(R.id.adapterEventDateTV);
        TextView titleTV = (TextView) convertView.findViewById(R.id.adapterEventTitleTV);
        ImageView adapterEventIV2 = (ImageView) convertView.findViewById(R.id.adapterEventIV2);


        // createDeclineAlertDialog(); // Creates the decline alert dialog

        if (adapterEventIV2 != null) {
            adapterEventIV2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view != null) {
                        Log.d("Event Details on Click", event.toString());
                        Intent intent = new Intent(mContext, EventDetailsActivity.class);
                        intent.putExtra("event", (Parcelable) event);
                        intent.putExtra("eventVolunteers", (Parcelable) event.getEventVolunteers());
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
