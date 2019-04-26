package team7.voluntime.Fragments.Common;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

import butterknife.ButterKnife;
import butterknife.OnClick;
import team7.voluntime.Activities.EditCharityActivity;
import team7.voluntime.Activities.EditVolunteerActivity;
import team7.voluntime.Activities.LoginActivity;
import team7.voluntime.Activities.MainActivity;
import team7.voluntime.Activities.SetupActivity;
import team7.voluntime.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserProfileFragment extends Fragment {

    private final static String TAG = "UserProfileFragment";


    public UserProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user_profile, container, false);
        ButterKnife.bind(this, v);


        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @OnClick(R.id.userProfileEditDetailsTV)
    public void editVolunteerOnClick() {
        Intent intent = new Intent(getContext(), getEditActivity());
        startActivity(intent);


    }

    public Class getEditActivity() {
        String type = MainActivity.getAccountType();
        if (type.equals("Volunteer")) {
            return EditVolunteerActivity.class;
        } else {
            return EditCharityActivity.class;
        }
    }

}
