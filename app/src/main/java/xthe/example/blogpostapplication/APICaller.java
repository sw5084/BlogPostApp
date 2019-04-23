package xthe.example.blogpostapplication;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;


public class APICaller {

    private static final String TAG = "APICaller";

    // Static values
    private static final String BASE_URL = "http://cms.iversoft.ca";
    private static final String CONNECT_SYMBOL = "/";
    private static final String AUTHENTICATE = "api/authenticate";
    private static final String NEWUSER = "api/users/new";
    private static final String RECOVERPASSWORD = "api/users/resetpassword";
    private static final String BLOGLIST = "api/blog/list";
    private static final String BLOGPOST = "api/blog/single";

    private static final int REQUEST_API_KEY = 0;
    private static final int REQUEST_USER_TOKEN = 1;

    private static final int REQUEST_TYPE_GET = 10;
    private static final int REQUEST_TYPE_POST = 11;

    // Other global variables
    private int requestMode = 0;
    private int requestType = 0;
    private String user_token;
    private HttpConnectionAsyncTask connect;


    // Constructor
    public APICaller (String user_token) {
        if (user_token != null && !user_token.isEmpty()){
            //User initialized, use this user to authenticate future request.
            this.user_token = user_token;
            this.requestMode = REQUEST_USER_TOKEN;
        } else {
            // No login user yet
            this.requestMode = REQUEST_API_KEY;
        }

    }


    // Connection methods
    public JSONObject authenticateUser(String email, String password) {
        if (requestMode != REQUEST_API_KEY){ // User already logged in
            return null;
        }
        requestType = REQUEST_TYPE_POST;

        JSONObject responseMsg = null;

        try {
            String url = generateURL(AUTHENTICATE);
            String params = "";
            params = params + "email=" + email;
            params = params + "&password=" + password;
            connect = new HttpConnectionAsyncTask();
            responseMsg = connect.execute(url, params).get();

        } catch (ExecutionException ee) {
            ee.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        return responseMsg;
    }


    public JSONObject signup(String name, String email, String password){
        if (requestMode != REQUEST_API_KEY){ // User already logged in
            return null;
        }
        requestType = REQUEST_TYPE_POST;

        JSONObject responseMsg = null;

        try {
            String url = generateURL(NEWUSER);
            String params = "";
            params = params + "name=" + name;
            params = params + "&email=" + email;
            params = params + "&password=" + password;
            connect = new HttpConnectionAsyncTask();
            responseMsg = connect.execute(url, params).get();

        } catch (ExecutionException ee) {
            ee.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        return responseMsg;
    }


    public JSONObject recoverPassword(String email) {
        if (requestMode != REQUEST_API_KEY){ // User already logged in
            return null;
        }
        requestType = REQUEST_TYPE_POST;

        JSONObject responseMsg = null;

        try {
            String url = generateURL(RECOVERPASSWORD);
            String params = "";
            params = params + "email=" + email;
            connect = new HttpConnectionAsyncTask();
            responseMsg = connect.execute(url, params).get();

        } catch (ExecutionException ee) {
            ee.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        return responseMsg;
    }


    public JSONObject getBlogPostList(Integer offset, Integer limit, Integer userId) {
        requestType = REQUEST_TYPE_GET;

        JSONObject responseMsg = null;

        try {
            String url = generateURL(BLOGLIST);
            String params = "";
            // Method is get, key value directly append to url
            String urlparams = "?offset=" + offset + "&limit=" + limit;
            if (userId != null){
                urlparams = urlparams + "&user_id=" + userId;
            }
            url = url + urlparams;
            connect = new HttpConnectionAsyncTask();
            responseMsg = connect.execute(url, params).get();

        } catch (ExecutionException ee) {
            ee.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        return responseMsg;
    }


    // Use these methods to create URL and message for each call
    private String generateURL(String base, String connecter, String url, Integer id) {
        String fullURL;

        if (id == null){
            // No id specified
            fullURL = base + connecter + url;
        } else {
            // URL require ID, add it to the end with a connect symbol
            fullURL = base + connecter + url + connecter + id;
        }

        return fullURL;
    }

    private String generateURL(String base, String connecter, String url) {
        return generateURL(base, connecter, url, null);
    }

    private String generateURL(String url, Integer id) {
        return generateURL(BASE_URL, CONNECT_SYMBOL, url, id);
    }

    private String generateURL(String url) {
        return generateURL(BASE_URL, CONNECT_SYMBOL, url, null);
    }


    // Create a private Async task class to do the connection work
    private class HttpConnectionAsyncTask extends AsyncTask<String, String, JSONObject> {

        private static final String TAG = "HttpConnectionAsyncTask";

        public HttpConnectionAsyncTask(){
            // Constructor
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject responseMsg = null;
            String urlString = params[0]; // URL to call
            String data = params[1]; //data to post
            OutputStream out = null;

            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Accept", " application/json");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                switch (requestMode){
                    case REQUEST_API_KEY:// API authentication mode
                        urlConnection.setRequestProperty("Authorization", (" Bearer " + MainActivity.getAuthen_API_key()));
                        break;
                    case REQUEST_USER_TOKEN:// User Token authentication mode
                        urlConnection.setRequestProperty("Authorization", (" Bearer " + user_token));
                        break;
                }
                urlConnection.setDoInput(true);
                urlConnection.setInstanceFollowRedirects(false);
                if (requestType == REQUEST_TYPE_GET){
                    urlConnection.setRequestMethod("GET");
                } else {// request type is post
                    urlConnection.setRequestMethod("POST");

                    // Write data if method is post
                    out = new BufferedOutputStream(urlConnection.getOutputStream());

                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                    writer.write(data);
                    writer.flush();
                    writer.close();
                    out.close();
                }

                urlConnection.connect();

//                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK)
                if (urlConnection.getResponseCode() != 0)
                {
                    StringBuilder response  = new StringBuilder();
                    BufferedReader input = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()),8192);
                    String strLine = null;
                    while ((strLine = input.readLine()) != null)
                    {
                        response.append(strLine);
                    }
                    input.close();
                    responseMsg = new JSONObject(response.toString());
                }

                urlConnection.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return responseMsg;
        }
    }

}
