package xthe.example.blogpostapplication;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class PasswordResetActivity extends AppCompatActivity {
    private static final String TAG = "PasswordResetActivity";

    EditText emailText;
    Button resetButton;
    TextView loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passwordreset);

        emailText = findViewById(R.id.input_email);
        loginLink = findViewById(R.id.link_login);
        resetButton = findViewById(R.id.btn_resetpassword);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void resetPassword() {
        Log.d(TAG, "resetPassword");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        resetButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(PasswordResetActivity.this,
                R.style.Theme_AppCompat_DayNight_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String email = emailText.getText().toString();

        // TODO: Implement password reset logic here.
        APICaller caller = new APICaller(null);
        final JSONObject response = caller.recoverPassword(email);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        if (response == null){
                            onSignupFailed();
                        } else {
                            onSignupSuccess();
                        }
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    public void onSignupSuccess() {
        Toast.makeText(getBaseContext(), "Reset email sent!", Toast.LENGTH_LONG).show();
        resetButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Password reset failed", Toast.LENGTH_LONG).show();

        resetButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = emailText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }

        return valid;
    }

}
