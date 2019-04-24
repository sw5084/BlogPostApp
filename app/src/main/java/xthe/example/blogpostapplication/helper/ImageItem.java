package xthe.example.blogpostapplication.helper;

public class ImageItem {
    private static final String TAG = "ImageItem";

    private Integer id;
    private Integer postId;
    private String filelocation;
    private String created_at;
    private String updated_at;
    private String url;
    private Integer height;
    private Integer width;

    public ImageItem (Integer id, Integer postId, String filelocation, String created_at, String updated_at, String url, Integer height, Integer width){
        this.id = id;
        this.postId = postId;
        this.filelocation = filelocation;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.url = url;
        this.height = height;
        this.width = width;
    }

    public Integer getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public Integer getHeight() {
        return height;
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getPostId() {
        return postId;
    }
}
