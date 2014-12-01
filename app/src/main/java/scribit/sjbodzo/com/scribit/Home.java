package scribit.sjbodzo.com.scribit;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;

public class Home extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        LayoutInflater lif = getLayoutInflater();
        View v = lif.inflate(R.layout.activity_home, null);

        //Hook up onClickListener's for Buttons
        Button journalViewButton = (Button) v.findViewById(R.id.journalEnt_button);
        journalViewButton.setOnClickListener(launchJournalMainView);
        Button challengeTrackerButton = (Button) v.findViewById(R.id.challengeTracker_button);
        challengeTrackerButton.setOnClickListener(launchChallengeTrackerView);
        Button settingsButton = (Button) v.findViewById(R.id.options_button);
        settingsButton.setOnClickListener(launchOptionsView);
        Button challengesDirViewButton = (Button) v.findViewById(R.id.challengeDirViewButton);
        challengesDirViewButton.setOnClickListener(launchChallengeDirView);
        TextView usernameText = (TextView) v.findViewById(R.id.username_tv);

        //dynamically set text to one set in prefs
        TextView titleTV = (TextView) v.findViewById(R.id.titleName_tv);
        SharedPreferences sp = getSharedPreferences(LoginActivity.PREFS_SETTINGS, 0);
        String listPrefStr = sp.getString("pref_key_titlename", "the Big Deal");
        titleTV.setText(listPrefStr);

        //populate username with one snatched from Facebook, otherwise default to saying journaler
        String usernamePref = sp.getString("pref_key_username", "JOURNALER");
        usernameText.setText(usernamePref);

        //query to find and num challenges completed
        ChallengeOpenHelper ChallengeTaskTableHelper = new ChallengeOpenHelper(this);
        SQLiteDatabase dB = ChallengeTaskTableHelper.getReadableDatabase();
        Cursor c = dB.query(ChallengeTaskTableHelper.CHALL_TABLE_NAME,
                null, ChallengeTaskTableHelper.COLUMN_STATUS + " = " + 1,
                null, null, null, null);
        int numCompletedChalls = c.getCount();
        TextView chCompNumTV = (TextView) v.findViewById(R.id.challengeNum_tv);
        chCompNumTV.setText(numCompletedChalls + "");

        //query to find num posts
        PostOpenHelper PostTableOpenHelper = new PostOpenHelper(this);
        SQLiteDatabase pdB = PostTableOpenHelper.getReadableDatabase();
        Cursor pc = pdB.query(PostTableOpenHelper.POSTS_TABLE_NAME,
                null, null, null, null, null, null);
        int numPostsInJournal = pc.getCount();
        TextView tvPostNum = (TextView) v.findViewById(R.id.postNum_tv);
        tvPostNum.setText(numPostsInJournal + "");

        ImageView avView = (ImageView) v.findViewById(R.id.avatar_iv);
        SharedPreferences spRef = getSharedPreferences(LoginActivity.PREFS_SETTINGS, 0);
        String avFileRef = spRef.getString("pref_key_avatar", "R.drawable.ic_avatar"); //drawable ref, else path to media on device
        Log.d("HOME PRINT", avFileRef);
        if (avFileRef.equals("R.drawable.ic_avatar")) avView.setImageDrawable(getResources().getDrawable(R.drawable.ic_avatar));
        else {
            try {
                Uri myURI = Uri.fromFile(new File(avFileRef));
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), myURI);
                avView.setImageBitmap(bitmap);
                avView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                avView.setMaxHeight(140);
                avView.setMaxWidth(140);
            }
            catch (IOException ioe) {
                avView.setImageDrawable(getResources().getDrawable(R.drawable.ic_avatar));
            }
        }
        v.invalidate();
        setContentView(v);
    }

    View.OnClickListener launchJournalMainView = new View.OnClickListener() {
        public void onClick(View v) {
            Intent launchJournalMainIntent = new Intent(getApplicationContext(), JournalEntries.class);
            startActivity(launchJournalMainIntent);
        }
    };

    View.OnClickListener launchChallengeTrackerView = new View.OnClickListener() {
        public void onClick(View v) {
            Intent launchChallengeTrackerIntent = new Intent(getApplicationContext(), ChallengeTrackerActivity.class);
            startActivity(launchChallengeTrackerIntent);
        }
    };

    View.OnClickListener launchOptionsView = new View.OnClickListener() {
        public void onClick(View v) {
            Intent launchOptionsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(launchOptionsIntent);
        }
    };

    View.OnClickListener launchChallengeDirView = new View.OnClickListener() {
        public void onClick(View v) {
            Intent launchChallDirViewIntent = new Intent(getApplicationContext(), ChallengeDirectory.class);
            startActivity(launchChallDirViewIntent);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(i);
            return true;
        }
        else if (id == R.id.new_post_entry) {
            Intent i = new Intent(this, AddEntryWizard.class);
            startActivity(i);
            return true;  //direct launch to wizard, skips backstack step on journal entries
        }
        return super.onOptionsItemSelected(item);
    }
}
