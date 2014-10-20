package scribit.sjbodzo.com.scribit;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextClock;
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
        //todo : create custom layout dynamically here b/c inflation impossible (?)
        //LayoutInflater lif = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //View v = lif.inflate(R.layout.post_grid_adapter_row, null);
        //ImageView iv = (ImageView) v.findViewById(R.id.post_gridrow_iv);
        FrameLayout fl = new FrameLayout(mContext);
        fl.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.WRAP_CONTENT, GridView.LayoutParams.WRAP_CONTENT));

        ImageView iv = new ImageView(mContext);
        iv.setLayoutParams(new FrameLayout.LayoutParams(375, 375));
        iv.setMinimumHeight(185);
        iv.setMinimumWidth(185);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //iv.setFocusable(false);
        //iv.setFocusableInTouchMode(false);
        fl.addView(iv);

        Post thePost = (Post) posts.get(position);
        String path = thePost.getMediaFilePath();
        if (path.equals("") || path == null || thePost.doesHazVid()) {
            iv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.pencilicon_iv));
            //use the default visual for the grid
        }
        else {
            Uri myUri = Uri.fromFile(new File(path));
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), myUri);
                iv.setImageBitmap(bitmap);
            }
            catch (IOException ioe) { ioe.printStackTrace(); }
        }

        TextView tv = new TextView(mContext);
        tv.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        tv.setText(thePost.getTitle());
        tv.setTextSize(25);
        tv.setBackgroundColor(Color.parseColor("#eaeaea"));
        tv.setAlpha((float)0.5);
        fl.addView(tv);

        //TODO: figure out what happens if I try to use a video still?

        return fl;
    }
}
