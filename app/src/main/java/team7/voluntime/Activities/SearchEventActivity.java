package team7.voluntime.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.SearchEvent;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import team7.voluntime.R;

public class SearchEventActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDB;
    private DatabaseReference databaseRF;
    private ArrayAdapter adapter;
    private ListView eventListView;
    private ArrayList<String> eventname = new ArrayList<>();
    private static String TAG = "Search Event Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_charity);
        /** fire base reference */
        firebaseDB = FirebaseDatabase.getInstance();
        databaseRF = firebaseDB.getReference("Event");
        Log.d(TAG, "onCreate: Started EventSerach.");

        /** Array Adapter takes the arraylist and puts the arraylist into a listview */
        eventListView = (ListView) findViewById(R.id.eventListView);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, eventname);
        eventListView.setAdapter(arrayAdapter);
        /** Child event listener Which checks for changes in the firebase*/
        databaseRF.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String value = dataSnapshot.getValue(String.class);
                eventname.add(value);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {


            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                eventname.remove(value);
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        EditText charityFilter = (EditText) findViewById(R.id.eventFilter);
        charityFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                (SearchEventActivity.this).adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }



    }


