package team7.voluntime.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team7.voluntime.R;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static final int RC_SIGN_IN = 1;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    /**
     * Use the @BindView annotation so Butter Knife can search for that view, and cast it for you
     * (in this case it will get casted to Edit Text)
     */
    @BindView(R.id.logoMain)
    ImageView logoMain;

    @BindView(R.id.loginEmailET)
    EditText loginEmailET;

    @BindView(R.id.emailInputLayout)
    TextInputLayout emailInputLayout;

    @BindView(R.id.passwordInputLayout)
    TextInputLayout passwordInputLayout;

    @BindView(R.id.loginPasswordET)
    EditText loginPasswordET;

    @BindView(R.id.resetPasswordTV)
    TextView resetPasswordTV;

    /**
     * It is helpful to create a tag for every activity/fragment. It will be easier to understand
     * log messages by having different tags on different places.
     */
    private static String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // You need this line on your activity so Butter Knife knows what Activity-View we are referencing
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        try {
            database.setPersistenceEnabled(true);
        } catch (Exception e){
            // TODO: Handle any Exception thrown by database.setPersistenceEnabled
        }

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null && isUserVerified()) {
                    if (!LoginActivity.this.isFinishing()) {
                        Log.d(TAG, "Logging in " + firebaseAuth.getCurrentUser().getEmail());
                        progressDialog.setMessage(getString(R.string.login_progress_dialog));
                        progressDialog.show();
                    }
                    mAuth.removeAuthStateListener(mAuthStateListener);
                    setupCompletedCheck();
                }
            }
        };

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail() // TODO: Verify this is needed
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        emailInputLayout.setHintEnabled(false);
        passwordInputLayout.setHintEnabled(false);
        setTitle(R.string.log_in_txt);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        String logoName = "heart.png";
        try {
            InputStream stream = getAssets().open(logoName);
            Drawable d = Drawable.createFromStream(stream, null);
            logoMain.setImageDrawable(d);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, e.toString());
        }
    }

    // Using this so the activity isn't recreated on orientation change
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.

        // TODO: Figure out what to do with this
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        updateUI(account);
    }

    // Navigation method to 'CreateAccountActivity'
    @OnClick(R.id.createAccountTV)
    public void navToCreateAccount() {
        startActivity(new Intent(this, CreateAccountActivity.class));
    }

    // Navigation method to 'ForgotPasswordFragment'
    @OnClick(R.id.resetPasswordTV)
    public void navToForgotPassword() {
        startActivity(new Intent(this, ForgotPasswordActivity.class));
    }

    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private boolean isUserVerified() {
        if(mAuth.getCurrentUser() != null) {
            final String email = mAuth.getCurrentUser().getEmail();
            if(mAuth.getCurrentUser().isEmailVerified()) {
                Log.d(TAG,email + " has been verified");
                mAuth.getCurrentUser().reload();
                return true;
            } else {
                Log.d(TAG,email + " has not been verified");
                Snackbar.make(findViewById(R.id.login_layout),
                        email + getString(R.string.user_not_verified_message), 5000)
                        .setAction("Resend", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.d(TAG, "Send verification button clicked");
                                sendVerificationEmail();
                            }
                        }).show();
                return false;
            }
        }
        return false;
    }

    // Sends a verification email to logged in user
    private void sendVerificationEmail() {
        mAuth.getCurrentUser()
                .sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Authentication email sent successfully " + task.getResult());
                            Toast.makeText(LoginActivity.this, R.string.email_authentication_message_success, Toast.LENGTH_LONG).show();
                        } else {
                            Log.d(TAG, "Authentication email failed to send " + task.getException());
                            Toast.makeText(LoginActivity.this, R.string.email_authentication_message_failure, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Hides the keyboard if a text field is focused
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // Authenticates the user's email and password with Firebase
    @OnClick(R.id.loginBtn)
    public void logIn() {
        String email = loginEmailET.getText().toString();
        String password = loginPasswordET.getText().toString();
        hideKeyboard();

        if (!isValidEmail(email)) {
            Toast.makeText(this, getString(R.string.email_check_toast), Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, getString(R.string.password_check_toast), Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage(getString(R.string.login_progress_dialog));
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail: sucess");
                            if (isUserVerified()) {
                                mAuth.removeAuthStateListener(mAuthStateListener);
                                setupCompletedCheck();
                            }
                        } else {
                            Log.d(TAG, "signInWithEmail: failure", task.getException());
                            String invalidUser = "com.google.firebase.auth.FirebaseAuthInvalidUserException";
                            String invalidCredentials = "com.google.firebase.auth.FirebaseAuthInvalidCredentialsException";
                            String exceptionString = task.getException().toString();

                            // TODO: Clean this up to compare against actual exception, not the strings with the exceptions above...
                            if(exceptionString.startsWith(invalidUser) || exceptionString.startsWith(invalidCredentials)) {
                                Toast.makeText(LoginActivity.this, R.string.invalid_email_and_password,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, R.string.exception_during_login,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
        Log.d(TAG, "username: " + email + " password: " + password);
    }

    // The initial method called when clicking 'sign in with google' and verifies the google account
    @OnClick(R.id.googleBtn)
    public void signInWithGoogle() {
        progressDialog.setMessage(getString(R.string.login_progress_dialog));
        progressDialog.show();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    // Makes a call to firebase to sign in with the google account passed in
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.login_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void setupCompletedCheck() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = database.getReference("Users").child(user.getUid());
        reference.keepSynced(true);

        reference.child("SetupComplete").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null && (boolean) dataSnapshot.getValue()) {
                        Log.d(TAG, "LOGGING IN: In the correct statement");
                        startMain();
                    } else {
                        Log.d(TAG, "LOGGING IN: In the wrong statement");
                        startSetup();
                    }
                } catch (Exception e) {
                    // TODO: Handle Exception and maybe catch more specific Exceptions
//                    startSetup();
                    Log.d(TAG, "LOGGING IN: An exception occurred during setupCompleteCheck");
                    Log.d(TAG, e.toString());
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // TODO: Handle database error here
            }
        });
    }

    public void startMain() {
        reference.child("AccountType").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "START MAIN: " + dataSnapshot.getValue() );
                if (dataSnapshot.getValue() == null) {
                    startSetup();
                } else {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class)
                            .putExtra("AccountType", dataSnapshot.getValue().toString()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // TODO: Handle databaseError here
            }
        });
    }

    public void startSetup() {
        startActivity(new Intent(LoginActivity.this, SetupActivity.class));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finishAffinity();
    }

    // This listens to any requests made in the activity and deals with them appropriately
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // If request code matches to Google Sign in will try and authenticate with Firebase
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.d(TAG, "Google sign in failed", e);
                Toast.makeText(this, getString(R.string.google_authentication_message_failure), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }
    }
}
