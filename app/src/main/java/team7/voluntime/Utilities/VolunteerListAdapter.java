package team7.voluntime.Utilities;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

import team7.voluntime.Domains.Volunteer;
import team7.voluntime.R;


public class VolunteerListAdapter extends ArrayAdapter<Volunteer> {
    private static final String TAG = "VolunteerListAdapter";
    private Context mContext;
    private Activity activity;
    int mResource;
    private String eventId;

    public VolunteerListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Volunteer> objects, Activity activity) {
        super(context, resource, objects);
        this.mContext = context;
        this.activity = activity;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        eventId = Objects.requireNonNull(getItem(position)).getId();

        String volunteerId = getItem(position).getId();
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
        ImageView volunteerAdapterIV1 = (ImageView) convertView.findViewById(R.id.volunteerAdapterIV1);

        if (volunteerAdapterIV1 != null) {
            volunteerAdapterIV1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(@NonNull View view) {
                    Log.d(TAG, "Volunteer list " + volunteer.toString());

                }
            });
        }


        nameTV.setText(name);
        return convertView;
    }
}
