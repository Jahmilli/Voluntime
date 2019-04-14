package team7.voluntime.Utilities;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/* Any common functions for the application should be stored here */
public class Utilities {
    public static DatabaseReference getCharityReference(FirebaseDatabase firebaseDatabase, String id) {
        return firebaseDatabase.getReference("Charities").child(id);
    }

    public static DatabaseReference getVolunteerReference(FirebaseDatabase firebaseDatabase, String id) {
        return firebaseDatabase.getReference("Volunteers").child(id);
    }

    public static String getCurrentDate() {
        Timestamp currentTime = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
        String s = new SimpleDateFormat("dd/MM/yyyy").format(currentTime);
        return s;
    }
}