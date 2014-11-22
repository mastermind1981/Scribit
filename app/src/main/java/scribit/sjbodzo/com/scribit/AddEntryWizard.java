package scribit.sjbodzo.com.scribit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import java.util.List;

public class AddEntryWizard extends Activity
                            implements DateEntryFragment.OnPostDateChangeListener,
                                       PostLocationFragment.OnPostLocationChangeListener,
                                       AddMediaFragment.OnAddMediaFileChangeListener {
    private String date, mediaFilePath;
    private double gpsx, gpsy;
    private EditText titleET, descET;
    private ChallengeTask theTask;
    private Switch isChallengeSwitch;
    private Context self;
    private boolean hazVid, hazImg, isForChall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry_wizard);
        gpsx = 0; gpsy = 0;
        hazVid = false; hazImg = false;
        isForChall = false;
        self = this;

        ImageButton calendarIB = (ImageButton) findViewById(R.id.when_ib);
        calendarIB.setOnClickListener(calendarIBFragListener);

        ImageButton locationIB = (ImageButton) findViewById(R.id.location_ib);
        locationIB.setOnClickListener(mapLocationIBFragListener);

        ImageButton addMediaIB = (ImageButton) findViewById(R.id.add_media_ib);
        addMediaIB.setOnClickListener(addMediaIBFragListener);

        titleET = (EditText) findViewById(R.id.title_et);
        descET = (EditText) findViewById(R.id.entryDesc_et);

        isChallengeSwitch = (Switch) findViewById(R.id.ischallenge_switch);
        isChallengeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                if (isChecked) {
                    //Set to prompt user with dialog asking which Challenge they're completing
                    AlertDialog.Builder builder = new AlertDialog.Builder(self);
                    builder.setTitle("Which Challenge is this for?");
                    //builder.setMessage("Select the Challenge from the list below."); <- cant set both adapter AND message

                    //fetch Challenge Tasks from sqlite db to pass to adapter
                    ChallengeTaskDataAccessObject chDAO = new ChallengeTaskDataAccessObject(self);
                    chDAO.open();
                    final List<ChallengeTask> list = chDAO.getAllThemThereChallenges();
                    chDAO.close();

                    DialogInterface.OnClickListener dLI = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(self, new Integer(which).toString(), Toast.LENGTH_SHORT).show();
                            theTask = list.get(which);
                            isForChall = true;
                        }
                    };
                    builder.setAdapter(new ChallengeTaskAdapter(self, list), dLI);
                    builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            isForChall = true;
                            dialogInterface.dismiss();
                        }
                    });
                    builder.setNegativeButton("Never Mind", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            isForChall = false;
                            isChallengeSwitch.setChecked(false);
                            dialogInterface.dismiss();
                        }
                    });
                    builder.create().show();
                } else {
                    //Do nothing here because it is irrelevant.
                }
            }
        });

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

            if (isForChall) {
                ChallengeTaskDataAccessObject chDAO = new ChallengeTaskDataAccessObject(self);
                chDAO.open();
                chDAO.setTaskAsComplete(theTask);
                chDAO.close();
                Log.e("SET IT", "Set " + theTask.getTitle() + " is marked as done!");
            }

            //Fire off intent to get back to Home page
            Intent launchNewEntryIntent = new Intent(self, Home.class);
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
