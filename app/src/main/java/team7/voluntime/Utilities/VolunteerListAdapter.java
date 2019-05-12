package team7.voluntime.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Objects;

import team7.voluntime.Activities.EventDetailsActivity;
import team7.voluntime.Activities.RateVolunteerActivity;
import team7.voluntime.Activities.VolunteerDetailsActivity;
import team7.voluntime.Domains.Event;
import team7.voluntime.Domains.Volunteer;
import team7.voluntime.R;


public class VolunteerListAdapter extends ArrayAdapter<Volunteer> {
    private static final String TAG = "VolunteerListAdapter";
    private Context mContext;
    private EventDetailsActivity activity;
    int mResource;
    private String eventId;

    public VolunteerListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Volunteer> objects, EventDetailsActivity activity) {
        super(context, resource, objects);
        this.mContext = context;
        this.activity = activity;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        eventId = Objects.requireNonNull(getItem(position)).getId();

        final Event event = activity.getEvent();
        final String volunteerId = getItem(position).getId();
        String dateOfBirth = getItem(position).getDateOfBirth();
        String name = getItem(position).getName();
        String address = getItem(position).getAddress();
        String phoneNumber = getItem(position).getPhoneNumber();
        String gender = getItem(position).getGender();
        String email = getItem(position).getEmail();

        final Volunteer volunteer = new Volunteer(volunteerId, dateOfBirth, name, address, phoneNumber, gender, email);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView nameTV  = (TextView) convertView.findViewById(R.id.volunteerAdapterNameTV);
        ImageView volunteerAdapterAddIV = (ImageView) convertView.findViewById(R.id.volunteerAdapterAddIV);
        ImageView volunteerAdapterRemoveIV = (ImageView) convertView.findViewById(R.id.volunteerAdapterRemoveIV);
        ImageView volunteerAdapterProfileIV = (ImageView) convertView.findViewById(R.id.volunteerAdapterProfileIV);
        ImageView volunteerAdapterRatingIV = (ImageView) convertView.findViewById(R.id.volunteerAdapterRatingIV);
        final boolean isRegistered = volunteerAdapterAddIV == null;



        if (volunteerAdapterAddIV != null) {
            volunteerAdapterAddIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(@NonNull View view) {
                DatabaseReference reference = activity.getDatabaseReference();
                reference.child("Volunteers").child(volunteerId).child("Events").child(event.getId()).setValue("registered");
                reference.child("Events").child(event.getId()).child("Volunteers").child(volunteerId).setValue("registered");
                }
            });
        }

        if (volunteerAdapterRemoveIV != null) {
            volunteerAdapterRemoveIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(@NonNull View view) {
                    DatabaseReference reference = activity.getDatabaseReference();
                    reference.child("Volunteers").child(volunteerId).child("Events").child(event.getId()).setValue("pending");
                    reference.child("Events").child(event.getId()).child("Volunteers").child(volunteerId).setValue("pending");
                }
            });
        }

        if (volunteerAdapterProfileIV != null) {
            volunteerAdapterProfileIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(@NonNull View view) {
                Log.d(TAG, "Volunteer list " + volunteer.toString());
                Intent intent = new Intent(mContext, VolunteerDetailsActivity.class);
                intent.putExtra("volunteer", (Parcelable) volunteer);
                intent.putExtra("event", event);
                intent.putExtra("canBeRated", isRegistered);
                mContext.startActivity(intent);
                }
            });
        }

        if (volunteerAdapterRatingIV != null) {
            volunteerAdapterRatingIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(@NonNull View view) {
                    Log.d(TAG, "Volunteer list " + volunteer.toString());
                    Intent intent = new Intent(mContext, RateVolunteerActivity.class);
                    intent.putExtra("volunteer", (Parcelable) volunteer);
                    intent.putExtra("event", event);
                    mContext.startActivity(intent);
                }
            });
        }

        nameTV.setText(name);
        return convertView;
    }
}
