package team7.voluntime.Utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import team7.voluntime.Activities.RateVolunteerActivity;
import team7.voluntime.Activities.VolunteerDetailsActivity;
import team7.voluntime.Domains.Event;
import team7.voluntime.Domains.Volunteer;
import team7.voluntime.R;


public class VolunteerListAdapter extends ArrayAdapter<Volunteer> {
    private static final String TAG = "VolunteerListAdapter";
    private Context mContext;
    private EventDetailsActivity activity;
    private ArrayList<Volunteer> volunteerList;
    int mResource;
    private String eventId;

    public VolunteerListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Volunteer> objects, EventDetailsActivity activity) {
        super(context, resource, objects);
        this.volunteerList = objects;
        this.mContext = context;
        this.activity = activity;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        eventId = Objects.requireNonNull(getItem(position)).getId();

        final Event event = activity.getEvent();
        final boolean isPastEvent = activity.getIsPastEvent();
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
        final ImageView volunteerAdapterAddIV = (ImageView) convertView.findViewById(R.id.volunteerAdapterAddIV);
        final ImageView volunteerAdapterRemovePendingIV = (ImageView) convertView.findViewById(R.id.volunteerAdapterRemovePendingIV);
        final ImageView volunteerAdapterRemoveRegisteredIV = (ImageView) convertView.findViewById(R.id.volunteerAdapterRemoveRegisteredIV);
        ImageView volunteerAdapterProfileIV = (ImageView) convertView.findViewById(R.id.volunteerAdapterProfileIV);
        ImageView volunteerAdapterRatingIV = (ImageView) convertView.findViewById(R.id.volunteerAdapterRatingIV);

        if (volunteerAdapterAddIV != null) {
            volunteerAdapterAddIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(@NonNull View view) {
                    DatabaseReference reference = activity.getDatabaseReference();
                    reference.child("Volunteers").child(volunteerId).child("Events").child(event.getId()).setValue(Constants.EVENT_REGISTERED);
                    reference.child("Events").child(event.getId()).child("Volunteers").child(volunteerId).setValue(Constants.EVENT_REGISTERED);
                    activity.removeVolunteerFromList(position, true);
                }
            });
        }

        if (volunteerAdapterRemovePendingIV != null) {
            volunteerAdapterRemovePendingIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(@NonNull View view) {
                    final android.app.AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Reject Volunteer");
                    builder.setMessage("Are you sure you wish to reject this volunteer?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            DatabaseReference reference = activity.getDatabaseReference();
                            reference.child("Volunteers").child(volunteerId).child("Events").child(event.getId()).setValue(Constants.EVENT_REJECTED);
                            reference.child("Events").child(event.getId()).child("Volunteers").child(volunteerId).setValue(Constants.EVENT_REJECTED);
                            activity.removeVolunteerFromList(position, true);
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                     builder.show();
                }
            });

        }

        if (volunteerAdapterRemoveRegisteredIV != null) {
            volunteerAdapterRemoveRegisteredIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(@NonNull View view) {
                    final android.app.AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Reject Volunteer");
                    builder.setMessage("Are you sure you wish to reject this volunteer?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            DatabaseReference reference = activity.getDatabaseReference();
                            reference.child("Volunteers").child(volunteerId).child("Events").child(event.getId()).setValue(Constants.EVENT_REJECTED);
                            reference.child("Events").child(event.getId()).child("Volunteers").child(volunteerId).setValue(Constants.EVENT_REJECTED);
                            activity.removeVolunteerFromList(position, false);
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
            });
        }

        if (volunteerAdapterProfileIV != null) {
            volunteerAdapterProfileIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(@NonNull View view) {
                Log.d(TAG, "Volunteer list " + volunteer.toString());
                Log.d(TAG, "event " + event.toString());
                Intent intent = new Intent(mContext, VolunteerDetailsActivity.class);
                intent.putExtra("volunteer", volunteer);
                intent.putExtra("event", event);
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
                    intent.putExtra("isPastEvent", isPastEvent);
                    mContext.startActivity(intent);
                }
            });
        }

        nameTV.setText(name);
        return convertView;
    }
}
