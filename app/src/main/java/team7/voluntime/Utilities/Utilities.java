package team7.voluntime.Utilities;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/* Any common functions for the application should be stored here */
public class Utilities {
    private static final String TAG = "Utilities";
    public static DatabaseReference getCharityReference(FirebaseDatabase firebaseDatabase, String id) {
        return firebaseDatabase.getReference("Charities").child(id);
    }

    public static DatabaseReference getVolunteerReference(FirebaseDatabase firebaseDatabase, String id) {
        return firebaseDatabase.getReference("Volunteers").child(id);
    }

    public static DatabaseReference getEventsReference(FirebaseDatabase firebaseDatabase) {
        return firebaseDatabase.getReference("Events");
    }

    public static String getCurrentDate() {
        Timestamp currentTime = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
        String s = new SimpleDateFormat("dd/MM/yyyy").format(currentTime);
        return s;
    }

    public static List<Address> getLocation(Context mContext, double latitude, double longitude) {
        Geocoder geocoder;
        geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            return geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            /*
            Possible values to get
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
        return null;
    }
}