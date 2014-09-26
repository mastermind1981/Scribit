package scribit.sjbodzo.com.scribit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class Home extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Hook up onClickListener's for Buttons
        Button journalViewButton = (Button) findViewById(R.id.journalEnt_button);
        journalViewButton.setOnClickListener(launchJournalMainView);

        Button challengeTrackerButton = (Button) findViewById(R.id.challengeTracker_button);
        challengeTrackerButton.setOnClickListener(launchChallengeTrackerView);

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

    /**
    View.OnClickListener launchOptionsView = new View.OnClickListener() {
        public void onClick(View v) {
            Intent launchOptionsIntent = new Intent(getApplicationContext(), Options.class);
            startActivity(Options);
        }
    };
    **/

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
