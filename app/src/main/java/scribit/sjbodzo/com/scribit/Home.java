package scribit.sjbodzo.com.scribit;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Home extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater lif = getLayoutInflater();
        View v = lif.inflate(R.layout.activity_home, null);

        //Hook up onClickListener's for Buttons
        Button journalViewButton = (Button) v.findViewById(R.id.journalEnt_button);
        journalViewButton.setOnClickListener(launchJournalMainView);
        Button challengeTrackerButton = (Button) v.findViewById(R.id.challengeTracker_button);
        challengeTrackerButton.setOnClickListener(launchChallengeTrackerView);
        Button settingsButton = (Button) v.findViewById(R.id.options_button);
        settingsButton.setOnClickListener(launchOptionsView);

        //Set title dynamically to one set in preferences
        TextView titleTV = (TextView) v.findViewById(R.id.titleName_tv);
        SharedPreferences sp = getSharedPreferences(JournalEntries.PREFS_SETTINGS, 0);
        String listPrefStr = sp.getString("pref_key_titlename", "the Noodle");
        titleTV.setText(listPrefStr);

        setContentView(v);

        /**
        Button optionsButton = (Button) findViewById(R.id.options_button);
        optionsButton.setOnClickListener(launchOptionsView);
        **/
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
        return super.onOptionsItemSelected(item);
    }
}
