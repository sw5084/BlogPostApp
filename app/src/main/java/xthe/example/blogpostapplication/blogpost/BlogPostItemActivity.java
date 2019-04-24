package xthe.example.blogpostapplication.blogpost;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import xthe.example.blogpostapplication.R;
import xthe.example.blogpostapplication.helper.ImageDownloader;

public class BlogPostItemActivity extends AppCompatActivity {

    public static final String TAG = "BlogPostItemActivity";

    private TextView title;
    private TextView author;
    private TextView viewcount;
    private ImageView image;
    private TextView content;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);

        title = (TextView)findViewById(R.id.show_post_title);
        author = (TextView)findViewById(R.id.show_post_author);
        viewcount = (TextView)findViewById(R.id.show_post_viewcount);
        image = (ImageView)findViewById(R.id.show_post_image);
        content = (TextView)findViewById(R.id.show_post_content);
        // Fetch value from bundle
        Intent intent = getIntent();
        title.setText(intent.getExtras().getString("title", ""));
        author.setText("Author: " + intent.getExtras().getString("author", ""));
        viewcount.setText("Views: " + intent.getExtras().getString("viewcount", ""));
        content.setText(intent.getExtras().getString("content", ""));

        // Now populate the imageView if there is an image
        if (!intent.getExtras().getString("imageURL", "").isEmpty() ) {
            new ImageDownloader(image).execute(intent.getExtras().getString("imageURL"));
        }
    }
}
