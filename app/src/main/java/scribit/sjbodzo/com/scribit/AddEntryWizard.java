package scribit.sjbodzo.com.scribit;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

public class AddEntryWizard extends Activity implements DateEntryFragment.OnPostDateChangeListener, PostLocationFragment.OnPostLocationChangeListener {
    private String date;
    private double gpsx, gpsy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry_wizard);
        date = "November 5, 1955"; //Back to the Future Easter Egg in init ;)
        gpsx = 0; gpsy = 0;

        ImageButton calendarIB = (ImageButton) findViewById(R.id.when_ib);
        calendarIB.setOnClickListener(calendarIBFragListener);

        ImageButton locationIB = (ImageButton) findViewById(R.id.location_ib);
        locationIB.setOnClickListener(mapLocationIBFragListener);

        ImageButton addMediaIB = (ImageButton) findViewById(R.id.add_media_ib);
        addMediaIB.setOnClickListener(addMediaIBFragListener);

        //Button newPostButton = (Button) findViewById(R.id.add_new_entry_button);
        //newPostButton.setOnClickListener(launchAddNewEntryListener);
    }

    //TODO: make method that checks inputs before it submits on button click

    public void onPostDateChange(int year, int month, int day) {
        StringBuilder sBob = new StringBuilder();
        sBob.append(year)
            .append(month)
            .append(" ")
            .append(day)
            .append(", ")
            .append(year);
        date = sBob.toString();
    }

    View.OnClickListener launchAddNewEntryListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent launchNewEntryIntent = new Intent(getApplicationContext(), AddEntryWizard.class);
            startActivity(launchNewEntryIntent);
        }
    };

    ImageButton.OnClickListener calendarIBFragListener = new ImageButton.OnClickListener() {
        public void onClick(View v) {
            DialogFragment newFragment = new DateEntryFragment();
            newFragment.show(getFragmentManager(), "datePickerFrag");
        }
    };

    ImageButton.OnClickListener mapLocationIBFragListener = new ImageButton.OnClickListener() {
        public void onClick(View v) {
            DialogFragment newFragment = new PostLocationFragment();
            newFragment.show(getFragmentManager(), "mapLocationPickerFrag");
        }
    };


    ImageButton.OnClickListener addMediaIBFragListener = new ImageButton.OnClickListener() {
        public void onClick(View v) {
            AddMediaFragment newFragment = new AddMediaFragment();
            newFragment.show(getFragmentManager(), "addMediaFrag");
        }
    };

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_entry_wizard, menu);
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

    @Override
    public void onPostLocationChange(double gpsx, double gpsy) {
        this.gpsx = gpsx;
        this.gpsy = gpsy;
    }
}
