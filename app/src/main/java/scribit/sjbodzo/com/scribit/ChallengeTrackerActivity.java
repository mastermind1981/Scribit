package scribit.sjbodzo.com.scribit;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;


public class ChallengeTrackerActivity extends Activity {
    OnSpinnerFilterSelectionListener spinnerChangeListener;
    FragmentManager fgm;

    //Using interface for pattern of communicating between decoupled Activity & Fragment
    public interface OnSpinnerFilterSelectionListener {
        public void onSpinnerChange(String selection);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_tracker);
        fgm = getFragmentManager();
        Fragment f = fgm.findFragmentByTag("challTrackListFrag");
        try {
            spinnerChangeListener = (OnSpinnerFilterSelectionListener)f;
        }
        catch (ClassCastException cle) {}

        Spinner spinner = (Spinner) findViewById(R.id.chall_track_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) spinnerChangeListener.onSpinnerChange("Score");
                else if (i ==1) spinnerChangeListener.onSpinnerChange("Distance");
            }
            @Override
            public void onNothingSelected(AdapterView<?> a) { }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.challenge_tracker, menu);
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
