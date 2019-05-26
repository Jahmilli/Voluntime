package team7.voluntime.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team7.voluntime.R;


public class CreateAccountActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @BindView(R.id.createAccEmailET)
    EditText createEmailET;

    @BindView(R.id.createAccPasswordET)
    EditText createPasswordET;

    @BindView(R.id.createEmailInputLayout)
    TextInputLayout createEmailInputLayout;

    @BindView(R.id.createPasswordInputLayout)
    TextInputLayout createPasswordInputLayout;

    private static String TAG = "CreateAccountActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();

        createEmailInputLayout.setHintEnabled(false);
        createPasswordInputLayout.setHintEnabled(false);

        progressDialog = new ProgressDialog(this);

        String logoName = "health_icon_1.png";
        try {
            InputStream stream = getAssets().open(logoName);
            Drawable d = Drawable.createFromStream(stream, null);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, e.toString());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private boolean isValidPassword(CharSequence target) {
        return target.length() >= 6;
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if(view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void checkExceptions(Task<AuthResult> task) {
        String exception = task.getException().getClass().toString();
        if(exception.equals(FirebaseAuthUserCollisionException.class.toString())) {
            Log.d(TAG, task.getException().toString());
            Toast.makeText(CreateAccountActivity.this, R.string.exception_email_already_exists, Toast.LENGTH_LONG).show();
        } else if(exception.equals(FirebaseNetworkException.class.toString())) {
            Log.d(TAG, task.getException().toString());
            Toast.makeText(this, R.string.exception_network_connectivity, Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.createAccBtn)
    public void createAccount() {
        String email = createEmailET.getText().toString().trim();
        String password = createPasswordET.getText().toString().trim();
        hideKeyboard();

        if(!isValidEmail(email)) {
            Toast.makeText(this, getString(R.string.email_check_toast), Toast.LENGTH_SHORT).show();
            return;
        }
        if(!isValidPassword(password)) {
            Toast.makeText(this, getString(R.string.password_check_length_toast), Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage(getString(R.string.create_account_txt));
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail: success");
                            sendVerificationEmail();
                        } else {
                            checkExceptions(task);
                        }
                    }
                });
    }

    private void sendVerificationEmail() {
        mAuth.getCurrentUser()
                .sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Authentication email sent successfully " + task.getResult());
                            Toast.makeText(CreateAccountActivity.this, R.string.email_authentication_message_success, Toast.LENGTH_LONG).show();
                            createEmailET.setText("");
                            createPasswordET.setText("");
                            finish();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        } else {
                            Log.d(TAG, "Authentication email failed to send " + task.getException());
                            Toast.makeText(CreateAccountActivity.this, R.string.email_authentication_message_failure, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}