package team7.voluntime.Fragments.Volunteers;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import team7.voluntime.Activities.LocationMapActivity;
import team7.voluntime.R;


public class VolunteerEventsMapFragment extends Fragment {


    private final static String TAG = "VolunteerEventsMap";


    public VolunteerEventsMapFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Events Map");
        loadMap();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_volunteer_events_map, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @OnClick(R.id.volEventsViewMapBtn)
    public void loadMap() {
        Intent intent = new Intent(getActivity(), LocationMapActivity.class);
        startActivity(intent);
    }

}
