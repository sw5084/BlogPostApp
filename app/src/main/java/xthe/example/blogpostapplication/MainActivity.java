package xthe.example.blogpostapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    protected String authen_API_key;
    protected String user_token = "";
    private static final int REQUEST_SIGNLOGIN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (user_token.isEmpty()){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivityForResult(intent, REQUEST_SIGNLOGIN);
        }
    }



    // API URL and Message generators
    private String generateURL() {
        String URL = "";

        return URL;
    }

    private String generateMessage() {
        String message = "";

        return message;
    }
}
