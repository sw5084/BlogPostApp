package xthe.example.blogpostapplication.blogpost;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import xthe.example.blogpostapplication.helper.ImageItem;

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
    private ImageItem imageItem;

    public BlogPost(Integer id, Integer authorId, String title, String content, String created_date, String updated_date, Integer publish, Integer view, ImageItem imageItem) {
        this.id = id;
        this.authorId = authorId;
        this.title = title;
        this.content = content;
        this.created_date = created_date;
        this.updated_date = updated_date;
        this.publish = publish;
        this.view = view;
        this.imageItem = imageItem;
    }

    public Integer getAuthorId() {
        return authorId;
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

    public ImageItem getImageItem() {
        return imageItem;
    }

    public static ArrayList<BlogPost> parseBlogPostListFromJson(JSONObject jsonObject){
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
            ImageItem imageItem;

            JSONObject tempImage;
            for (int i = 0; i < postArray.length(); i++) {
                id = postArray.getJSONObject(i).getInt("id");
                authorId = postArray.getJSONObject(i).getInt("author");
                title = postArray.getJSONObject(i).getString("title");
                content = postArray.getJSONObject(i).getString("content");
                created_date = postArray.getJSONObject(i).getString("created_at");
                updated_date = postArray.getJSONObject(i).getString("updated_at");
                publish = postArray.getJSONObject(i).getInt("published");
                view = postArray.getJSONObject(i).getInt("views");
                if ((postArray.getJSONObject(i).getString("image")) != "null"){
                    tempImage = postArray.getJSONObject(i).getJSONObject("image");
                    imageItem = new ImageItem(tempImage.getInt("id"),
                            tempImage.getInt("blog_post_id"),
                            tempImage.getString("file_location"),
                            tempImage.getString("created_at"),
                            tempImage.getString("updated_at"),
                            tempImage.getJSONObject("file_sizes").getJSONObject("original").getString("url"),
                            tempImage.getJSONObject("file_sizes").getJSONObject("original").getJSONObject("size").getInt("height"),
                            tempImage.getJSONObject("file_sizes").getJSONObject("original").getJSONObject("size").getInt("width"));
                } else {
                    imageItem = null;
                }
                post = new BlogPost(id, authorId, title, content, created_date, updated_date, publish, view, imageItem);
                blogPosts.add(post);
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }


        return blogPosts;
    }

}
