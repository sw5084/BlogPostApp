package xthe.example.blogpostapplication.blogpost;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import xthe.example.blogpostapplication.R;

public class BlogPostAdapter extends ArrayAdapter {
    public static final String TAG = "BlogPostAdapter";

    public BlogPostAdapter (Context context, ArrayList<BlogPost> postArray) {
        super(context, 0, postArray);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        BlogPost post = (BlogPost) getItem(position);

        // Lookup view for data population
        View view = convertView;
        BlogListViewHolder viewHolder = null;

        if (view == null) {
            viewHolder = new BlogListViewHolder();
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            view = inflater.inflate(R.layout.list_item_blogpost, null);

            viewHolder.title = (TextView) view.findViewById(R.id.blogpostview_item_title);
            viewHolder.description = (TextView) view.findViewById(R.id.blogpostview_item_description) ;
            viewHolder.views = (TextView) view.findViewById(R.id.blogpostview_item_views);
            //viewHolder.leftImage = (ImageView) view.findViewById(R.id.blogpostview_image); // For ImageView

            view.setTag(viewHolder);
        }
        else {
            viewHolder=(BlogListViewHolder)view.getTag();
        }

        // Populate the data into the template view using the data object
        viewHolder.title.setText(post.getTitle());
        viewHolder.description.setText(post.getContent().substring(0, Math.min(post.getContent().length(), 50)));
        viewHolder.views.setText("Views: " + post.getViews().toString());

        // Return the completed view to render on screen
        return view;
    }

    public static class BlogListViewHolder
    {
        public TextView title;
        public TextView description;
        public TextView views;
        //public ImageView leftImage;
    }

}
