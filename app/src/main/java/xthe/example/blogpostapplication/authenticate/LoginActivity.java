package xthe.example.blogpostapplication.authenticate;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import xthe.example.blogpostapplication.R;
import xthe.example.blogpostapplication.helper.APICaller;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private static final int REQUEST_RESETPASSWORD = 1;

    EditText emailText;
    EditText passwordText;
    Button loginButton;
    TextView signupLink;
    TextView passwordResetLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailText = findViewById(R.id.input_email);
        passwordText = findViewById(R.id.input_password);
        loginButton = findViewById(R.id.btn_login);
        signupLink = findViewById(R.id.link_signup);
        passwordResetLink = findViewById(R.id.link_recoverpassword);

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

        passwordResetLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the PasswordReset activity
                Intent intent = new Intent(getApplicationContext(), PasswordResetActivity.class);
                startActivityForResult(intent, REQUEST_RESETPASSWORD);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check if user is already logged in
        SharedPreferences preferences=getSharedPreferences("BlogUserInfo",MODE_PRIVATE);
        String user_token = preferences.getString("user_token", "");
        if (!user_token.isEmpty()){
            finish();
        }

    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.Theme_AppCompat_DayNight_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        // Make call to API server to check if username and password is correct
        APICaller caller = new APICaller(null, this);
        final JSONObject response = caller.authenticateUser(email, password);

        // TODO: Remove test code after finish debugging
        //final JSONObject response = caller.getBlogPostList(0, 5, null);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        if (response == null){
                            onLoginFailed();
                        } else {
                            try {
                                String token = response.getString("token");
                                if (token == null || token.isEmpty()){
                                    onLoginFailed();
                                } else {
                                    onLoginSuccess(response);
                                }
                            } catch (JSONException e) {
                                Log.e(TAG, e.getMessage());
                                e.printStackTrace();
                                onLoginFailed();
                            }
                        }
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        } else if (requestCode == REQUEST_RESETPASSWORD) {
            if (resultCode == RESULT_OK) {
                // Password reset success, but nothing needs to be done
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess(JSONObject response) {
        try {
            String token = response.getString("token");

            SharedPreferences preferences=getSharedPreferences("BlogUserInfo",MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences.edit();

            editor.putString("user_token", token);
            editor.putBoolean("IsLogin",true);
            editor.commit();

        } catch (JSONException e){
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }

        loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }
}