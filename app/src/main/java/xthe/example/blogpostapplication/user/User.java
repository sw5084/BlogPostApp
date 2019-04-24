package xthe.example.blogpostapplication.user;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class User {

    private static final String TAG = "User";

    private Integer id;
    private Integer role;
    private String name;
    private String email;
    private String created_date;
    private String updated_date;

    public User(Integer id, Integer role, String name, String email, String created_date, String updated_date) {
        this.id = id;
        this.role = role;
        this.name = name;
        this.email = email;
        this.created_date = created_date;
        this.updated_date = updated_date;
    }


    public Integer getId() {
        return id;
    }

    public String getName () {
        return name;
    }

    public static ArrayList<User> parseUserListFromJson(JSONObject jsonObject){
        ArrayList<User> userList = new ArrayList<User>();

        try {
            JSONArray postArray = jsonObject.getJSONArray("data");
            User user;
            Integer id;
            Integer role;
            String name;
            String email;
            String created_date;
            String updated_date;
            for (int i = 0; i < postArray.length(); i++) {
                id = postArray.getJSONObject(i).getInt("id");
                role = postArray.getJSONObject(i).getInt("role");
                name = postArray.getJSONObject(i).getString("name");
                email = postArray.getJSONObject(i).getString("email");
                created_date = postArray.getJSONObject(i).getString("created_at");
                updated_date = postArray.getJSONObject(i).getString("updated_at");
                user = new User(id, role, name, email, created_date, updated_date);
                userList.add(user);
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }

        return userList;
    }
}
