package xthe.example.blogpostapplication.user;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import xthe.example.blogpostapplication.R;

public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "UserProfileActivity";

    private EditText username;
    private EditText useremail;
    private Boolean editing;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        username = (EditText) findViewById(R.id.user_name);
        useremail = (EditText) findViewById(R.id.user_email);

        username.setText(getIntent().getExtras().getString("user_name"));
        useremail.setText(getIntent().getExtras().getString("user_email"));

        setUnEditable(username);
        setUnEditable(useremail);
        editing = false;
    }

    private void setUnEditable(EditText editText) {
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        editText.setClickable(false); // user navigates with wheel and selects widget
    }

    private void setEditable(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setClickable(true);
    }
}
