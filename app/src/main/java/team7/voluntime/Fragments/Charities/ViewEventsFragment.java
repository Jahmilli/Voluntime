package team7.voluntime.Fragments.Charities;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team7.voluntime.Activities.CreateEventActivity;
import team7.voluntime.Domains.Charity;
import team7.voluntime.R;
import team7.voluntime.Utilities.Utilities;


public class ViewEventsFragment extends Fragment {
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseUser mUser;
    private Charity charity;

    @BindView(R.id.createEventTitleTV)
    TextView createEventTitleTV;

    @BindView(R.id.createEventIV)
    ImageView createEventIV;

    private final static String TAG = "ViewEventsFragment";


    public ViewEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//
        getActivity().setTitle("View Events");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_view_events, container, false);
        ButterKnife.bind(this, v);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = Utilities.getCharityReference(database, mUser.getUid());

        // Attach a listener to read the data at our posts reference
        reference.child("Profile").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                charity = dataSnapshot.getValue(Charity.class);
                charity.setId(mUser.getUid());
                Log.d(TAG, charity.toString());
                if (charity.getName() != null) {
                    createEventTitleTV.setText(charity.getName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "The read failed: " + databaseError.getCode());
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @OnClick(R.id.createEventIV)
    public void createEventOnClick() {
        Log.d(TAG, "Clicked");
        Intent createEventIntent = new Intent(getActivity(), CreateEventActivity.class);
        createEventIntent.putExtra("id", charity.getId());
        startActivity(createEventIntent);
    }

}
