package team7.voluntime.Utilities;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/* Any common functions for the application should be stored here */
public class Utilities {
    public static DatabaseReference getCharityReference(FirebaseDatabase firebaseDatabase, String id) {
        return firebaseDatabase.getReference("Charities").child(id);
    }

    public static DatabaseReference getVolunteerReference(FirebaseDatabase firebaseDatabase, String id) {
        return firebaseDatabase.getReference("Volunteers").child(id);
    }
}
