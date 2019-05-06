package team7.voluntime.Activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team7.voluntime.Fragments.Charities.ViewEventsFragment;
import team7.voluntime.Fragments.Charities.CharityViewEventsFragment;
import team7.voluntime.Fragments.Common.UserProfileFragment;
import team7.voluntime.Fragments.Volunteers.VolunteerEventsListFragment;
import team7.voluntime.R;
import team7.voluntime.Utilities.Utilities;

/**
 * Class: MainActivity
 * Extends:  {@link AppCompatActivity}
 * Author:  Carlos Tirado < Carlos.TiradoCorts@uts.edu.au>, and YOU!
 * Description:
 * <p>
 * For this project I encourage you to use Fragments. It is up to you to build up the app as
 * you want, but it will be a good practice to learn on how to use Fragments. A very good tutorial
 * on how to use fragments can be found on this site:
 * http://www.vogella.com/tutorials/AndroidFragments/article.html
 * <p>
 * I basically chose to use fragments because of the design of the app, again, you can choose to change
 * completely the design of the app, but for this design specifically I will use Fragments.
 * <p>
 */
public class MainActivity extends AppCompatActivity {

    /**
     * A basic Drawer layout that helps you build the side menu. I followed the steps on how to
     * build a menu from this site:
     * https://developer.android.com/training/implementing-navigation/nav-drawer
     * I recommend you to have a read of it if you need to do any changes to the code.
     */
    private DrawerLayout mDrawerLayout;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private Fragment fragment;
    private NavigationView navigationView;

    /**
     * A reference to the toolbar
     */
    private Toolbar toolbar;

    /**
     * Helps to manage the fragment that is being used in the main view.
     */
    private FragmentManager fragmentManager;

    private final static String TAG = "MainActivity";
    private static String accountType;

    /**
     * I am using this enum to know which is the current fragment being displayed, you will see
     * what I mean with this later in this code.
     */
    private enum MenuStates {
        EVENT, VOLUNTEER_EVENTS_LIST, LOGOUT, PROFILE
    }

    /**
     * The current fragment being displayed.
     */
    private MenuStates currentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Gets extra from either login activity or setupActivity
        // TODO: Determine whether we need to handle null accountType being passed in
        Bundle extra = getIntent().getExtras();
        accountType = extra.getString("accountType");

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        if (accountType != null && accountType.equals("Volunteer")) {
            reference = Utilities.getVolunteerReference(database, mUser.getUid());
            Log.d(TAG, "User " + mUser.getEmail() + "is registered as a: " + accountType);
        } else if (accountType != null && accountType.equals("Charity")) {
            Log.d(TAG, "User " + mUser.getEmail() + "is registered as a: " + accountType);
            reference = Utilities.getCharityReference(database, mUser.getUid());
        } else {
            // TODO: Could log the user out if the account type isnt correct and post a toast message
            Log.d(TAG, "Incorrect account type: " + accountType);
        }

        ButterKnife.bind(this);

        // go look for the main drawer layout
        mDrawerLayout = findViewById(R.id.main_drawer_layout);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    finish();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
            }
        };

        // The default fragment on display is the volunteer information
        currentState = MenuStates.PROFILE;

        // Go look for the main drawer layout
        mDrawerLayout = findViewById(R.id.main_drawer_layout);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Set up the menu button
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().clear();

        if (accountType.equals("Volunteer")) {
            initVolunteer();
        } else if (accountType.equals("Charity")) {
            initCharity();
        } else {
            // TODO: Handle this case
            Log.d(TAG, "Issue with initialising main activity based on account, accountType is: " + accountType);
        }
                    
        // Setup the navigation drawer, most of this code was taken from:
        // https://developer.android.com/training/implementing-navigation/nav-drawer
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Using a switch to see which item on the menu was clicked
                        switch (menuItem.getItemId()) {
                            // You can find these id's at: res -> menu -> drawer_view.xml
                            case R.id.nav_event:
                                if (currentState != MenuStates.EVENT) {
                                    ChangeFragment(new CharityViewEventsFragment());
                                    currentState = MenuStates.EVENT;
                                }
                                break;
                            case R.id.nav_profile:
                                if (currentState != MenuStates.PROFILE) {
                                    ChangeFragment(new UserProfileFragment());
                                    currentState = MenuStates.PROFILE;
                                }
                                break;
                            case R.id.nav_volunteer_events:
                                if (currentState != MenuStates.VOLUNTEER_EVENTS_LIST) {
                                    ChangeFragment(new VolunteerEventsListFragment());
                                    currentState = MenuStates.VOLUNTEER_EVENTS_LIST;
                                }
                                break;
                            case R.id.nav_logout:
                                if (currentState != MenuStates.LOGOUT) {
                                    Log.d(TAG, "Logging out now");
                                    FirebaseAuth.getInstance().signOut();
                                    // TODO: Potentially put a check around this to logout only once we've verified user has logged out
                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                    finish();
                                }
                                break;
                        }

                        return true;
                    }
                });

        // If you need to listen to specific events from the drawer layout.
        mDrawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Respond when the drawer is opened
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        // Respond when the drawer is closed
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                    }
                }
        );


        // More on this code, check the tutorial at http://www.vogella.com/tutorials/AndroidFragments/article.html
        fragmentManager = getFragmentManager();

        // Add the default Fragment once the user logged in
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(R.id.fragment_container, new UserProfileFragment());
        ft.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    /**
     * Called when one of the items in the toolbar was clicked, in this case, the menu button.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This function changes the title of the fragment.
     *
     * @param newTitle The new title to write in the
     */
    public void ChangeTitle(String newTitle) {
        toolbar.setTitle(newTitle);
    }


    /**
     * This function allows to change the content of the Fragment holder
     * @param fragment The fragment to be displayed
     */
    private void ChangeFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void initCharity() {
        navigationView.inflateMenu(R.menu.drawer_view_charity);
        navigationView.setCheckedItem(R.id.nav_profile);
    }

    public void initVolunteer() {
        navigationView.inflateMenu(R.menu.drawer_view_volunteer);
        navigationView.setCheckedItem(R.id.nav_profile);
    }

    @OnClick(R.id.searchCharityTV)
    public void onClick() {
        Intent intent = new Intent(this, SearchCharityActivity.class);

    public static String getAccountType() {
        return accountType;
    }
}
