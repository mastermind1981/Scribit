package scribit.sjbodzo.com.scribit;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;
import java.io.File;
import java.io.IOException;
import java.util.GregorianCalendar;

public class ViewPostEntry extends Activity {
    private Post postEntry;
    private View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //inflate layout to modify with Entry information before showing
        LayoutInflater lif = getLayoutInflater();
        v = lif.inflate(R.layout.activity_view_entry, null);

        //Grab Post object from Parcelable in Bundle passed on open
        Bundle bag = getIntent().getExtras();
        postEntry = (Post) bag.getParcelable("postEntry");

        //format MONTH to str for layout
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(postEntry.getDate());
        String monthStr = CustomPostAdapter.parseDateMonth(c.get(c.MONTH)+1);
        Log.e("DAYOFMONTH\t", Integer.valueOf(c.get(c.DAY_OF_MONTH)).toString());

        //Grab all fields for the UI...
        TextView title_tv = (TextView) v.findViewById(R.id.title_post_view_entry_tv);
        TextView month_tv = (TextView) v.findViewById(R.id.date_month_view_entry_tv);
        TextView day_tv = (TextView)   v.findViewById(R.id.date_day_view_entry_tv);
        TextView year_tv = (TextView)  v.findViewById(R.id.date_year_view_entry_tv);
        TextView desc_tv = (TextView)  v.findViewById(R.id.desc_view_entry_tv);

        //Set the fields for title & description of journal entry...
        desc_tv.setText(postEntry.getDescription());
        title_tv.setText(postEntry.getTitle());
        month_tv.setText(monthStr);
        day_tv.setText(c.get(c.DAY_OF_MONTH) + ", ");
        year_tv.setText(c.get(c.YEAR) + "");

        //call helper method to associate MediaPlayer if there is media associated w/ this Journal Entry (Post obj)
        boolean hasMediaAssoc = associateMediaWithPostView();

        //TODO: add logic to show if is a challenge
        //TODO: add logic to show gps location information
        //TODO: add logic to handle displaying of media (if any)
        //TODO: hook in actionbar item to edit posting

        //Show the view
        setContentView(v);
    }

    private boolean associateMediaWithPostView() {
        String mediaPath = postEntry.getMediaFilePath();
        if (mediaPath.equals("")) return false; //there is no media associated
        else {
            Uri myUri = Uri.fromFile(new File(postEntry.getMediaFilePath()));
            if (postEntry.doesHazVid()) { //rq's MediaPlayer obj present for playback
                Log.e("VIDEO FILEPATH VAL:\t", postEntry.getMediaFilePath());

                //TODO: put in loading transition to show work being done while waiting, use AsyncTask?

                //Create VideoView ref, couple to MediaController for playback options
                final VideoView vv = (VideoView) v.findViewById(R.id.video_view_entry);
                final MediaController mc = new MediaController(this);
                vv.setVisibility(View.VISIBLE);
                myUri = Uri.fromFile(new File(postEntry.getMediaFilePath()));
                vv.setVideoURI(myUri);

                //use FrameLayout to nest playback controls over the top of the video
                FrameLayout frameLayout = (FrameLayout) vv.getParent();
                FrameLayout.LayoutParams flParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                                                                           FrameLayout.LayoutParams.WRAP_CONTENT);
                ViewGroup vg = (ViewGroup) mc.getParent(); //grab ref to parent view of playback controls
                vg.removeView(mc); //remove playback controls to add them to vv's parent layout, avoids IllegalStateException
                flParams.gravity = Gravity.BOTTOM; //sets playback controls to bottom of FrameLayout
                mc.setLayoutParams(flParams); //associate parameters for layout to playback controls
                mc.setMediaPlayer(vv); //couple media playback controls to video view
                vv.setMediaController(mc); //couple mechanisms for playback to video control
                frameLayout.addView(mc);
            }
            else if (postEntry.doesHazImg()) { //rq's ImageView obj present for viewing
                Log.e("IMG FILEPATH VAL:\t", postEntry.getMediaFilePath());
                final ImageView iv = (ImageView) v.findViewById(R.id.post_img_iv_view_entry);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), myUri);
                    iv.setImageBitmap(bitmap);
                    iv.setVisibility(View.VISIBLE);
                    iv.setAdjustViewBounds(true);
                    iv.setMaxHeight(300);
                    iv.setMaxWidth(300);
                }
                catch (IOException ioe) { ioe.printStackTrace(); }
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_entry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
