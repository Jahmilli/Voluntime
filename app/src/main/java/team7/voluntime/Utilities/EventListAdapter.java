package team7.voluntime.Utilities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import team7.voluntime.Domains.Event;
import team7.voluntime.Domains.EventVolunteers;
import team7.voluntime.Fragments.Charities.ViewEventsFragment;
import team7.voluntime.R;


public class EventListAdapter extends ArrayAdapter<Event> {
    private static final String TAG = "EventListAdapter";
    private Context mContext;
    private ViewEventsFragment fragment;
    int mResource;
    private String eventId;
    private AlertDialog.Builder declineAlertBuilder;
    private AlertDialog declineAlert;

    public EventListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Event> objects, ViewEventsFragment fragment) {
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

        Event event = new Event(eventId, title, description, category, location, date, createdTime, organisers, eventVolunteers);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView dateTV = (TextView) convertView.findViewById(R.id.adapterEventDateTV);
        TextView titleTV = (TextView) convertView.findViewById(R.id.adapterEventTitleTV);
//        ImageView patientProfileIV = (ImageView) convertView.findViewById(R.id.patientProfileIV);
//        ImageView patientRejectIV = (ImageView) convertView.findViewById(R.id.patientRejectIV);
//        ImageView patientAcceptIV = (ImageView) convertView.findViewById(R.id.patientAcceptIV);

        // createDeclineAlertDialog(); // Creates the decline alert dialog

//        if (patientProfileIV != null)
//            patientProfileIV.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (view != null) {
//                        Intent patientPackets = new Intent(mContext, ProfileActivity.class);
//                        patientPackets.putExtra("uid", eventId);
//                        mContext.startActivity(patientPackets);
//                    }
//                }
//            });
//
//        if (patientRejectIV != null)
//            patientRejectIV.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (view != null) {
//                        declineAlert.show();
//                    }
//                }
//            });
//
//        if (patientAcceptIV != null)
//            patientAcceptIV.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (view != null) {
//                        DatabaseReference reference = fragment.getDBReference();
//                        FirebaseUser mUser = fragment.getUser();
//                        reference.child(mUser.getUid()).child("Patients").child(eventId).child("approved").setValue("accepted");
//                        reference.child(eventId).child("Doctor").child("approved").setValue("accepted");
//                    }
//                }
//            });

//        if (patientDiagnosisIV != null) {
//            patientDiagnosisIV.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (view != null) {
//                        Intent diagnosisHistory = new Intent(mContext, DiagnosisHistoryActivity.class);
//                        diagnosisHistory.putExtra("uid", eventId);
//                        mContext.startActivity(diagnosisHistory);
//                    }
//                }
//            });
//        }

//        if (patientPacketIV != null)
//            patientPacketIV.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (view != null) {
//                        Intent patientPackets = new Intent(mContext, PatientPacketsActivity.class);
//                        patientPackets.putExtra("uid", eventId);
//                        mContext.startActivity(patientPackets);
//                    }
//                }
//            });

        dateTV.setText(date);
        titleTV.setText(title);
        return convertView;
    }
}
