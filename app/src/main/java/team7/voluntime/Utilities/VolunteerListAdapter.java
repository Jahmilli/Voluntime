package team7.voluntime.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import team7.voluntime.Activities.VolunteerDetailsActivity;
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

        final String eventId = this.activity.getEventId();
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
        ImageView volunteerAdapterIV2 = (ImageView) convertView.findViewById(R.id.volunteerAdapterIV2);


        if (volunteerAdapterAddIV != null) {
            volunteerAdapterAddIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(@NonNull View view) {
                DatabaseReference reference = activity.getDatabaseReference();
                reference.child("Volunteers").child(volunteerId).child("Events").child(eventId).setValue("registered");
                reference.child("Events").child(eventId).child("Volunteers").child(volunteerId).setValue("registered");
                }
            });
        }

        if (volunteerAdapterRemoveIV != null) {
            volunteerAdapterRemoveIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(@NonNull View view) {
                    DatabaseReference reference = activity.getDatabaseReference();
                    reference.child("Volunteers").child(volunteerId).child("Events").child(eventId).setValue("pending");
                    reference.child("Events").child(eventId).child("Volunteers").child(volunteerId).setValue("pending");
                }
            });
        }

        if (volunteerAdapterIV2 != null) {
            volunteerAdapterIV2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(@NonNull View view) {
                Log.d(TAG, "Volunteer list " + volunteer.toString());
                Intent intent = new Intent(mContext, VolunteerDetailsActivity.class);
                intent.putExtra("volunteer", (Parcelable) volunteer);
                mContext.startActivity(intent);
                }
            });
        }


        nameTV.setText(name);
        return convertView;
    }
}
