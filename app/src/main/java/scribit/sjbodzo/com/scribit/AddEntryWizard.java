package scribit.sjbodzo.com.scribit;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class AddEntryWizard extends Activity
                            implements DateEntryFragment.OnPostDateChangeListener,
                                       PostLocationFragment.OnPostLocationChangeListener,
                                       AddMediaFragment.OnAddMediaFileChangeListener {
    private String date, mediaFilePath;
    private double gpsx, gpsy;
    private EditText titleET, descET;
    private Context self;
    private boolean hazVid, hazImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry_wizard);
        gpsx = 0; gpsy = 0;
        hazVid = false; hazImg = false;
        self = this;

        ImageButton calendarIB = (ImageButton) findViewById(R.id.when_ib);
        calendarIB.setOnClickListener(calendarIBFragListener);

        ImageButton locationIB = (ImageButton) findViewById(R.id.location_ib);
        locationIB.setOnClickListener(mapLocationIBFragListener);

        ImageButton addMediaIB = (ImageButton) findViewById(R.id.add_media_ib);
        addMediaIB.setOnClickListener(addMediaIBFragListener);

        titleET = (EditText) findViewById(R.id.title_et);
        descET = (EditText) findViewById(R.id.entryDesc_et);

        Button newPostButton = (Button) findViewById(R.id.addEntry_submit_button);
        newPostButton.setOnClickListener(submitEntryListener);
    }

    public String fetchEntryTitle() {
        //TODO: validate title, such as duplicate title entry and null check
        return titleET.getText().toString();
    }

    public String fetchEntryDescription() {
        //TODO: validate description
        return descET.getText().toString();
    }

    public void onPostDateChange(int year, String month, int day) {
        StringBuilder sBob = new StringBuilder();
        sBob.append(month)
            .append(" ")
            .append(day)
            .append(", ")
            .append(year);
        date = sBob.toString();
    }

    public boolean checkForErrorsBeforeSubmission() {
        boolean hasErrors = false;
        if (fetchEntryTitle() == null) {
            //TODO: custom TOASTS!!
            Toast.makeText(self, "Please add a title to save your entry.", Toast.LENGTH_SHORT).show();
            hasErrors = true;
        }
        if (fetchEntryDescription() == null) {
            //TODO: show alert to user to ensure they don't want to put in a description
            Toast.makeText(self, "Please add a description to save your entry.", Toast.LENGTH_SHORT).show();
            hasErrors = true;
        }
        if (mediaFilePath == null) {
            //TODO: show alert to user to ensure they don't want to add media
            mediaFilePath = "";
            //hasErrors = true;
        }
        if (date == null || date.equals("")) {
            Toast.makeText(self, "Hey, you forgot to tell us when this happened!", Toast.LENGTH_SHORT).show();
            hasErrors = true;
        }
        if (gpsx == 0 && gpsy == 0) {
            Toast.makeText(self, "Don't you want to say where you were when this happened?", Toast.LENGTH_SHORT).show();
            //hasErrors = true;
        }
        return hasErrors;
    }

    View.OnClickListener submitEntryListener = new View.OnClickListener() {
        public void onClick(View v) {
            //TODO: animation / transition to show that your post added successfully

            //Check for bad values that werent validated on input (null values)
            if(checkForErrorsBeforeSubmission()) return;

            //Save GPS information for location if you can...
            SharedPreferences sp = getSharedPreferences(JournalEntries.PREFS_SETTINGS, 0);
            Boolean isGPSEnabled = sp.getBoolean("pref_key_toggle_gps", false);
            ImageGPSUtility iGPS = new ImageGPSUtility();
            if(isGPSEnabled) {
                iGPS.recordGPS(null, getApplicationContext());
                gpsx = iGPS.getLat(); //note x is actually lattitude
                gpsy = iGPS.getLon(); //note y is actually longitude
            }

            //Write data object to database for persistence in app
            PostsDataAccessObject DAO = new PostsDataAccessObject(self);
            DAO.open();
            Post newPost = DAO.createJournalPost(fetchEntryTitle(), fetchEntryDescription(),
                                                 gpsx, gpsy, mediaFilePath, date, hazImg, hazVid);
            DAO.close();

            //Fire off intent to get back to main journal page
            Intent launchNewEntryIntent = new Intent(self, JournalEntries.class);
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

    public void onAddMediaFileChange(String path, int i) {
        mediaFilePath = path;
        if (i == 1) hazImg = true;
        else if (i == 2) hazVid = true;
    }
}
