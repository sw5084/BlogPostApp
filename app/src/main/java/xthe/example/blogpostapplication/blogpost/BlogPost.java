package xthe.example.blogpostapplication.blogpost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BlogPost {

    private static final String TAG = "BlogPost";

    private Integer id;
    private Integer authorId;
    private String title;
    private String content;
    private String created_date;
    private String updated_date;
    private Integer publish;
    private Integer view;

    public BlogPost(Integer id, Integer authorId, String title, String content, String created_date, String updated_date, Integer publish, Integer view) {
        this.id = id;
        this.authorId = authorId;
        this.title = title;
        this.content = content;
        this.created_date = created_date;
        this.updated_date = updated_date;
        this.publish = publish;
        this.view = view;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Integer getViews() {
        return view;
    }

    public static ArrayList<BlogPost> parseBlogPostItems(JSONObject jsonObject){
        ArrayList<BlogPost> blogPosts = new ArrayList<BlogPost>();

        try {
            JSONArray postArray = jsonObject.getJSONArray("data");
            BlogPost post;
            Integer id;
            Integer authorId;
            String title;
            String content;
            String created_date;
            String updated_date;
            Integer publish;
            Integer view;
            for (int i = 0; i < postArray.length(); i++) {
                id = postArray.getJSONObject(i).getInt("id");
                authorId = postArray.getJSONObject(i).getInt("author");
                title = postArray.getJSONObject(i).getString("title");
                content = postArray.getJSONObject(i).getString("content");
                created_date = postArray.getJSONObject(i).getString("created_at");
                updated_date = postArray.getJSONObject(i).getString("updated_at");
                publish = postArray.getJSONObject(i).getInt("published");
                view = postArray.getJSONObject(i).getInt("views");
                post = new BlogPost(id, authorId, title, content, created_date, updated_date, publish, view);
                blogPosts.add(post);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return blogPosts;
    }

}
