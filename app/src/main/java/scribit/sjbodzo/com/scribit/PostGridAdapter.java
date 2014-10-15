package scribit.sjbodzo.com.scribit;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class PostGridAdapter extends BaseAdapter {
    private Context mContext;
    private List posts;

    public PostGridAdapter(Context c, List<Post> posts) {
        mContext = c;
        this.posts = posts;
    }

    public int getCount() {
        return posts.size();
    }

    public Object getItem(int position) {
        return posts.get(position);
    }

    public long getItemId(int position) {
        return ((Post)posts.get(position)).getID();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater lif = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = lif.inflate(R.layout.post_grid_adapter_row, null);
        ImageView iv = (ImageView) v.findViewById(R.id.post_gridrow_iv);
        Post thePost = (Post) posts.get(position);
        String path = thePost.getMediaFilePath();
        TextView tv = (TextView) v.findViewById(R.id.post_gridrow_tv);
        tv.setText(thePost.getTitle());
        if (path.equals("") || path == null || thePost.doesHazVid()) {
            iv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.pencilicon_iv));
            //use the default visual for the grid
        }
        else {
            Uri myUri = Uri.fromFile(new File(path));
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), myUri);
                iv.setImageBitmap(bitmap);
                iv.setAdjustViewBounds(true);
                iv.setMaxHeight(100);
                iv.setMaxWidth(100);
            }
            catch (IOException ioe) { ioe.printStackTrace(); }
        }
        //TODO: figure out what happens if I try to use a video still?
        return v;
    }

    // images to use for each post

}
