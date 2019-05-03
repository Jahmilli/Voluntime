package team7.voluntime.Utilities;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/* Any common functions for the application should be stored here */
public class Utilities {
    private static final String TAG = "Utilities";

    // Return DatabaseReference to specific Charity
    public static DatabaseReference getCharityReference(FirebaseDatabase firebaseDatabase, String id) {
        return firebaseDatabase.getReference("Charities").child(id);
    }

    // Return DatabaseReference to specific Volunteer
    public static DatabaseReference getVolunteerReference(FirebaseDatabase firebaseDatabase, String id) {
        return firebaseDatabase.getReference("Volunteers").child(id);
    }

    // Return DatabaseReference to Events
    public static DatabaseReference getEventsReference(FirebaseDatabase firebaseDatabase) {
        return firebaseDatabase.getReference("Events");
    }

    public static String getCurrentDate() {
        Timestamp currentTime = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
        String s = new SimpleDateFormat("dd/MM/yyyy").format(currentTime);
        return s;
    }

    // The return value of this can be used to get information about specific location you pass in as GPS coordinates
    public static List<Address> getLocation(Context mContext, double latitude, double longitude) {
        Geocoder geocoder;
        geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            return geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            /*
            A few of the possible values which can be used:
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
             */
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, e.toString());
            Toast.makeText(mContext, "An error occurred when setting the location", Toast.LENGTH_SHORT);
        }
        // TODO: Determine whether we can pass back something better than null :p
        return null;
    }

    public static HashMap<String, String> getVolunteers(DataSnapshot dataSnapshot, String TAG) {
        HashMap<String, String> volunteers = new HashMap<>();
        for (DataSnapshot volunteersChild : dataSnapshot.getChildren()) {
            String volunteerID = Objects.requireNonNull(volunteersChild.getKey());
            String status = Objects.requireNonNull(volunteersChild.getValue()).toString();
            if (volunteersChild.exists()) {
                if (!StringUtils.isEmpty(volunteerID)) {
                    volunteers.put(volunteerID, status);
                } else {
                    Log.d(TAG, "Volunteer is Empty???");
                }
            }
        }
        return volunteers;
    }

}