package xthe.example.blogpostapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import xthe.example.blogpostapplication.authenticate.LoginActivity;
import xthe.example.blogpostapplication.blogpost.BlogPost;
import xthe.example.blogpostapplication.blogpost.BlogPostAdapter;
import xthe.example.blogpostapplication.blogpost.BlogPostItemActivity;
import xthe.example.blogpostapplication.helper.APICaller;
import xthe.example.blogpostapplication.user.User;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final String authen_API_key =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhcHBfa2V5IjoiYmFzZTY0OlZvb2g1ZzdSNXBOQ29JT2VcL0J1N3JESVpSOEZiZVdpOHJaeU1LckFlV3d3PSIsImlzcyI6Imh0dHA6XC9cL2xvY2FsaG9zdCIsImlhdCI6MTQ4NzY4ODE4OSwiZXhwIjoxNDg3NzE2OTg5LCJuYmYiOjE0ODc2ODgxODksImp0aSI6IjBlYjgxMTkzNTk1Njk1YTFjZjkwNzUwMjMzNDc3ZTNlIn0.v0OW9CJt8Rq-vBMfIeCPVHSTUUlVsvIqZKN06MSmpK4"
            ;
    private static final int REQUEST_LOGIN = 0;
    private static final int REQUEST_SHOWPOST = 1;


    private String user_token = "";
    private ArrayList<User> userList = new ArrayList<User>();
    private User currentUser = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkUserLogin();

        if(!user_token.isEmpty()) {
            // User is already logged in, check if authentication is still valid
            APICaller caller = new APICaller(user_token,  this);
            JSONObject response = caller.getBlogPostList(0, 1, null);
            if (response == null){
                // If no response, token is probably invalid, and preference is probably reset in APICaller, check login again
                checkUserLogin();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if user is logged in
        checkUserLogin();
        if (!user_token.isEmpty()){
            // Inflate view with content if user is logged in
            inflateBlogList(user_token, null);
            // Populate the user list once the user is logged in
            getUserList();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();

        checkUserLogin();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.userprofile:
                // Launch user profile page
                return true;
            case R.id.refreshlist:
                // Refresh Blog Post List
                inflateBlogList(user_token, null);
                // Also refresh user list
                getUserList();
                return true;
            case R.id.signout:
                // Delete token
                SharedPreferences preferences=getSharedPreferences("BlogUserInfo",MODE_PRIVATE);
                SharedPreferences.Editor editor=preferences.edit();

                editor.putString("user_token", "");
                editor.putBoolean("IsLogin",false);
                editor.commit();

                user_token = "";

                // Launch login page
                checkUserLogin();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // Getter and Setters
    public static String getAuthen_API_key() {
        return authen_API_key;
    }


    // View related functions
    private void inflateBlogList(String user_token, Integer limit) {
        // Call to server to get blog post list
        if (limit == null) {
            limit = 0;
        }
        APICaller caller = new APICaller(user_token, this);
        JSONObject json = caller.getBlogPostList(0, limit, null);

        // inflate list view
        final ArrayList<BlogPost> blogPosts = BlogPost.parseBlogPostListFromJson(json);
        BlogPostAdapter blogPostAdapter = new BlogPostAdapter(getBaseContext(), blogPosts);
        ListView blogPostsListView = (ListView) findViewById(R.id.blogpost_view);
        blogPostsListView.setAdapter(blogPostAdapter);

        // set list item to load blog post when clicked
        blogPostsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                BlogPost post = blogPosts.get(position);
                showBlogPost(post);

            }
        });

        blogPostAdapter.notifyDataSetChanged();
    }


    public void showBlogPost(BlogPost post) {
        // Find the author of the post
        Integer userid = post.getAuthorId();
        User user = null;
        int i = 0;
        while(user == null && i < userList.size()) {
            if (userid == userList.get(i).getId()){
                user = userList.get(i);
            } else {
                i++;
            }
        }

        Intent intent = new Intent(this, BlogPostItemActivity.class);
        intent.putExtra("title", post.getTitle());
        if (user == null) {
            // User is null, use Unknown as author name
            intent.putExtra("author", "Unknown");
        } else {
            intent.putExtra("author", user.getName());
        }
        intent.putExtra("viewcount", post.getViews().toString());
        if (post.getImageItem() != null) {
            intent.putExtra("imageURL", post.getImageItem().getUrl());
//            intent.putExtra("imageHeight", post.getImageItem().getHeight());
//            intent.putExtra("imageWidth", post.getImageItem().getWidth());
        }
        intent.putExtra("content", post.getContent());

        startActivityForResult(intent, REQUEST_SHOWPOST);

    }


    // Other functions
    private void checkUserLogin() {

        SharedPreferences preferences=getSharedPreferences("BlogUserInfo",MODE_PRIVATE);

        if (user_token.isEmpty()) { // If user_token disappear at anytime
            // Try getting the token again from sharedperference
            user_token = preferences.getString("user_token", "");
        }

        // If still empty, or shared preference disappear at any time, the user is no longer logged in, go to login screen.
        if (user_token.isEmpty() || preferences.getString("user_token", "").isEmpty()){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivityForResult(intent, REQUEST_LOGIN);
        }
    }

    private void getUserList() {
        APICaller caller = new APICaller(user_token, this);
        JSONObject response = caller.getUserList(0, 0);

        // Clear the current user list and replace it with the new value
        userList.clear();
        userList = User.parseUserListFromJson(response);
    }


    // TODO: Remove test code later
    public void inflateTestArrayList() {
        try {
            JSONObject json = new JSONObject("{\"offset\":0,\"limit\":5,\"total\":5,\"remaining\":281,\"data\":[{\"id\":551,\"author\":86,\"title\":\"New Blog Title\",\"content\":\"New blog content\",\"created_at\":\"2019-03-04 21:40:23\",\"updated_at\":\"2019-04-18 22:01:15\",\"published\":1,\"views\":1,\"author_user\":{\"id\":86,\"role\":2,\"name\":\"User\",\"email\":\"user@iversoft.ca\",\"created_at\":\"2019-03-01 22:15:20\",\"updated_at\":\"2019-03-03 22:55:26\",\"gender\":null,\"biography\":\"\",\"date_of_birth\":null},\"image\":null},{\"id\":549,\"author\":87,\"title\":\"Combined Test\",\"content\":\"Logic combination - updated\",\"created_at\":\"2019-03-04 03:34:45\",\"updated_at\":\"2019-03-04 03:48:24\",\"published\":1,\"views\":0,\"author_user\":{\"id\":87,\"role\":2,\"name\":\"Andrew\",\"email\":\"andrew.burger25@gmail.com\",\"created_at\":\"2019-03-03 15:24:14\",\"updated_at\":\"2019-03-03 23:41:32\",\"gender\":null,\"biography\":\"\",\"date_of_birth\":\"2019-03-03\"},\"image\":null},{\"id\":547,\"author\":87,\"title\":\"Test - Updated\",\"content\":\"Test\",\"created_at\":\"2019-03-03 23:21:14\",\"updated_at\":\"2019-03-04 03:53:11\",\"published\":1,\"views\":0,\"author_user\":{\"id\":87,\"role\":2,\"name\":\"Andrew\",\"email\":\"andrew.burger25@gmail.com\",\"created_at\":\"2019-03-03 15:24:14\",\"updated_at\":\"2019-03-03 23:41:32\",\"gender\":null,\"biography\":\"\",\"date_of_birth\":\"2019-03-03\"},\"image\":null},{\"id\":546,\"author\":87,\"title\":\"Own Account\",\"content\":\"Test\",\"created_at\":\"2019-03-03 16:58:38\",\"updated_at\":\"2019-03-03 16:58:38\",\"published\":1,\"views\":0,\"author_user\":{\"id\":87,\"role\":2,\"name\":\"Andrew\",\"email\":\"andrew.burger25@gmail.com\",\"created_at\":\"2019-03-03 15:24:14\",\"updated_at\":\"2019-03-03 23:41:32\",\"gender\":null,\"biography\":\"\",\"date_of_birth\":\"2019-03-03\"},\"image\":null},{\"id\":545,\"author\":86,\"title\":\"First\",\"content\":\"No pic; testing update\",\"created_at\":\"2019-03-03 16:57:10\",\"updated_at\":\"2019-03-04 20:14:11\",\"published\":1,\"views\":0,\"author_user\":{\"id\":86,\"role\":2,\"name\":\"User\",\"email\":\"user@iversoft.ca\",\"created_at\":\"2019-03-01 22:15:20\",\"updated_at\":\"2019-03-03 22:55:26\",\"gender\":null,\"biography\":\"\",\"date_of_birth\":null},\"image\":null}]}");

            ArrayList<BlogPost> blogPosts = BlogPost.parseBlogPostListFromJson(json);
            BlogPostAdapter blogPostAdapter = new BlogPostAdapter(getBaseContext(), blogPosts);
            ListView blogPostsListView = (ListView) findViewById(R.id.blogpost_view);
            blogPostsListView.setAdapter(blogPostAdapter);

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }

    }
}
