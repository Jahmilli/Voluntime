package team7.voluntime.Activities;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team7.voluntime.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.io.InputStream;

public class ForgotPasswordActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @BindView(R.id.forgotPasswordET)
    EditText forgotPasswordET;

    @BindView(R.id.logoForgot)
    ImageView logoForgot;

    private static String TAG = "ForgotPasswordActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        String logoName = "health_icon_1.png";
        try {
            InputStream stream = getAssets().open(logoName);
            Drawable d = Drawable.createFromStream(stream, null);
            logoForgot.setImageDrawable(d);
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

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if(view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @OnClick(R.id.forgotPasswordBtn)
    public void sendPasswordResetEmail() {
        String email = forgotPasswordET.getText().toString().trim();
        hideKeyboard();
        if(isValidEmail(email)) {
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            // TODO: Handle possible exceptions with this
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Email successfully sent");
                                Toast.makeText(ForgotPasswordActivity.this, getString(R.string.email_success), Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            } else {
                                Toast.makeText(ForgotPasswordActivity.this, getString(R.string.email_failure), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(ForgotPasswordActivity.this, getString(R.string.email_check_toast), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}