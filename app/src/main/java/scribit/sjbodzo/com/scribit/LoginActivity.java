package scribit.sjbodzo.com.scribit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationStatusCodes;
import com.google.android.gms.maps.LocationSource;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends Activity implements GooglePlayServicesClient.OnConnectionFailedListener,
                                                       GooglePlayServicesClient.ConnectionCallbacks,
                                                       LocationClient.OnAddGeofencesResultListener,
                                                       LocationListener {

    public static final String PREFS_SETTINGS = "TheSettingsFileYall";
    private PostsDataAccessObject postsTableDAO;
    private ChallengeTaskDataAccessObject challTableDAO;
    private LocationClient locationClient;
    private boolean isRequestCurrentlyInProgress;
    private PendingIntent geofenceRequestIntent;
    public enum REQUEST_TYPE {ADD}
    private REQUEST_TYPE requestType;
    private LocationRequest locReq;
    private UiLifecycleHelper uiHelper;

    private static final int MILLISECONDS_PER_SECOND = 1000;
    public static final int UPDATE_INTERVAL_IN_SECONDS = 3;
    private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;

    //TODO: better design for service logic that triggers geofence tracking
    //TODO: create check of preference before enabling geofence tracking

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //create listener to respond to facebook lifecycle changes
        Session.StatusCallback callback = new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                onSessionStateChange(session, state, exception);
            }
        };
        //hook listener into uiHelper obj (Facebook's management obj)
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);

        //for handling async geo tracking
        isRequestCurrentlyInProgress = false;
        setContentView(R.layout.activity_launch);

        Button FBLoginButton = (Button) findViewById(R.id.launchpage_loginButton);
        FBLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchFacebookLogin(true);
            }
        });

        Button noFBLoginButton = (Button) findViewById(R.id.launchpage_noFBloginButton);
        noFBLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Home.class);
                startActivity(i);
            }
        });

        //perform necessary checks if this is first app load
        firstLaunchCheck();
    }

    //deals w/ handoff from Facebook after login attempted
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    //Method for session state changes in application
    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Toast.makeText(this, "Logged in!!!!!!", Toast.LENGTH_SHORT);
            Intent i = new Intent(this, Home.class);
            startActivity(i);
        } else if (state.isClosed()) {
            Toast.makeText(this, "Logged out!!!!!!", Toast.LENGTH_SHORT);
        }
    }

    //Triggered when geofence transition happens, fires IntentService
    private PendingIntent getTransitionPendingIntent() {
        Intent intent = new Intent(this, ChallengeTrackerIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void createLocationRequest() {
        locReq = LocationRequest.create();
        locReq.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locReq.setInterval(UPDATE_INTERVAL);
        locReq.setFastestInterval(FASTEST_INTERVAL);
    }

    /*
  * Provide the implementation of ConnectionCallbacks.onConnected()
  * Once the connection is available, send a request to add the
  * Geofences
  */
    @Override
    public void onConnected(Bundle dataBundle) {
        boolean hasLocServices = isLocationEnabled(this);
        //Check if first launch of app or not
        SharedPreferences sp = getSharedPreferences(PREFS_SETTINGS, 0);
        boolean isFirstLaunch = sp.getBoolean("pref_key_virginal_ux", false);
        if (!hasLocServices && isFirstLaunch) {
            triggerPromptForGPS();
        }
        else if (hasLocServices) {
            locationClient.requestLocationUpdates(locReq, this);
            Location l = locationClient.getLastLocation();
            String msg = "Updated Location: " +
                    Double.toString(l.getLatitude()) + "," +
                    Double.toString(l.getLongitude());
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            switch (requestType) {
                case ADD:
                    geofenceRequestIntent = getTransitionPendingIntent();
                    locationClient.addGeofences(parseChallengeTasksAsGeofencesList(),
                            geofenceRequestIntent, this);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        boolean hasLocServices = isLocationEnabled(this);
        if (!hasLocServices) {
            triggerPromptForGPS();
        }

        // Report to the UI that the location was updated
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAddGeofencesResult(int statusCode, String[] geofenceRequestIds) {
        // Here is where we check if our geofences were added correctly. Check statusCode
        // to see if they did. (works similar to codes for onactivityresult & w/ intents)
        if (LocationStatusCodes.SUCCESS == statusCode) {
            Toast.makeText(this, "Geofences added successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Geofences FAILED to add!!!!!", Toast.LENGTH_SHORT).show();
            if (LocationStatusCodes.GEOFENCE_NOT_AVAILABLE == statusCode) {
                boolean hasLocServices = isLocationEnabled(this);
                if (!hasLocServices) {
                    triggerPromptForGPS();
                }
            }
        }
        // Turn off the in progress flag and disconnect the client
        isRequestCurrentlyInProgress = false;
        locationClient.disconnect();
    }

    public void triggerPromptForGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Location Services Not Setup");
        builder.setMessage("It appears that the GPS service is not enabled. Please check your settings.");
        builder.setPositiveButton("Show Me", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        builder.setNegativeButton("No", null);
        builder.create().show();
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Turn off the request flag
        isRequestCurrentlyInProgress = false;

        /*
         * If the error has a resolution, start a Google Play services
         * activity to resolve it.
         */
        if (connectionResult.hasResolution()) {
            try { connectionResult.startResolutionForResult(this,9000); //maps 9000 to connection failed request
            } catch (IntentSender.SendIntentException e) { e.printStackTrace(); }
        } else {
            int errorCode = connectionResult.getErrorCode();
            Toast.makeText(this, "onConnectionFailed, ERROR CODE: " + errorCode, Toast.LENGTH_SHORT).show();
            //TODO: treat individual error codes appropriately, ex: if location services turned off prompt user
        }
    }

    public void addGeofences() {
        requestType = REQUEST_TYPE.ADD; //starts request
        /** TODO: check if google play services is available
        if (!servicesConnected()) {
            return;
        }
        **/

        locationClient = new LocationClient(this, this, this);
        // If a request is not already underway
        if (!isRequestCurrentlyInProgress) {
            // Indicate that a request is underway
            isRequestCurrentlyInProgress = true;
            // Request a connection from the client to Location Services
            locationClient.connect();
        } else {
            /*
             * A request is already underway. You can handle
             * this situation by disconnecting the client,
             * re-setting the flag, and then re-trying the
             * request.
             */
        }
    }

    @Override
    public void onDisconnected() {
        isRequestCurrentlyInProgress = false;
        locationClient = null;
    }

    //TODO: incorporate inviting tutorial to first time app user?

    public void firstLaunchCheck() {
        SharedPreferences spRef = getSharedPreferences(PREFS_SETTINGS, 0);
        boolean isFirstTimeUser = spRef.getBoolean("pref_key_virginal_ux", true);
        if (isFirstTimeUser) {
            //populate initial posts in application
            //prepare data access object to write to DBs
            postsTableDAO = new PostsDataAccessObject(this); //associate to current Context
            postsTableDAO.open(); //inherited method, sets DB ref

            postsTableDAO.createJournalPost("Foo 1", "insane post bruh", 23.2, 99.42, "", "January 2, 2010", false, false);
            postsTableDAO.createJournalPost("Foo 2", "insane aehkpost bruh", 200.0, 99.42, "", "February 22, 2010", false, false);
            postsTableDAO.createJournalPost("Foo 3", "insakjhkhjkhjkhne post bruh", -100, 99.42, "", "December 17, 2010", false, false);
            postsTableDAO.createJournalPost("Foo 4", "insakjhkjhkjhne post bruh", 33.99, 99.42, "", "November 14, 2010", false, false);
            postsTableDAO.close();

            challTableDAO = new ChallengeTaskDataAccessObject(this);
            challTableDAO.open();
            //challTableDAO.createChallengeTaskEntry("Visit Point West Apt! TEST CHALL", "VISIT IT FORREAL", 50, "VISITOR", "", 29.647083,-82.37345, false, "Other");
            challTableDAO.createChallengeTaskEntry("Rock Climbing", getString(R.string.rockGymChall), 50, "Boulder King", "", 29.645456, -82.3248483, false, "Sports &amp; Rec");
            challTableDAO.createChallengeTaskEntry("Mmm Satchels", getString(R.string.satchelsChall), 35, "Satch <3-er", "", 29.673988, -82.301908, false, "Foodie");
            challTableDAO.createChallengeTaskEntry("No Paynes No Gains", getString(R.string.paynesPrairieChall), 50, "Nature Lover", "", 29.577939, -82.312629, false, "Outdoorsy");
            challTableDAO.createChallengeTaskEntry("Is It A Potato or Not?!", getString(R.string.potatoChall), 35, "Potato Pal", "", 29.6490924,-82.3434982, false, "Sightseeing Campus");
            challTableDAO.createChallengeTaskEntry("Roads? Where we're going we don't need roads", getString(R.string.oneLessCarChall), 95, "the Transporter", "", 0, 0, false, "Outdoorsy");
            challTableDAO.createChallengeTaskEntry("Work 'Em Silly Gators!", getString(R.string.gatorSportGameChall), 65, "Swamp Gator", "", 29.649465, -82.350190, false, "Sports &amp; Rec");
            challTableDAO.createChallengeTaskEntry("Rock Show", getString(R.string.rockShowChall), 50, "the Dude", "", 29.64941, -82.324247, false, "Nightlife");
            challTableDAO.createChallengeTaskEntry("Confusing words only please", getString(R.string.researchSemChall), 20, "the Academic", "", 0, 0, false, "Academic");
            challTableDAO.createChallengeTaskEntry("let's like, go, and like, eat", getString(R.string.theTopChall), 40, "Too Mainstream", "", 29.652536, -82.325243, false, "Nightlife");
            challTableDAO.createChallengeTaskEntry("Do You Even Lift Bro?", getString(R.string.swRecGymChall), 30, "Swole Daddy", "", 29.6436325, -82.3549302, false, "Sports &amp; Rec");
            challTableDAO.close();

            //set status to already visited
            SharedPreferences.Editor eddy = spRef.edit();
            eddy.putBoolean("pref_key_virginal_ux", false);
            eddy.apply();

            //attempt to activate geofences
            createLocationRequest();
            addGeofences();
        }
        else {
            //attempt to activate geofences
            createLocationRequest();
            addGeofences();

            launchFacebookLogin(false);
        }
    }

    //Parse ChallengeTasks objs to yield Geofences from them
    private List<Geofence> parseChallengeTasksAsGeofencesList() {
        List<Geofence> list = new ArrayList<Geofence>();
        if(challTableDAO == null) challTableDAO = new ChallengeTaskDataAccessObject(this);
        challTableDAO.open();
        List<ChallengeTask> challengeTaskList = challTableDAO.getAllThemThereChallenges();
        challTableDAO.close();

        ChallengeTask cTask;
        double latitude, longitude;
        Geofence geographicChallengeTaskFence;
        float radius = 1610; //in meters & ~1610 meters in a mile
        final int transitionStep = Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL;
        final long duration = 180000; //in milliseconds & 180000 ~= 3 minutes
        String requestID; //uniquely IDs the geofence, will use unique ID in SQLITE
        Geofence.Builder builder = new Geofence.Builder();
        //TODO: set loitering delay

        for (int i = 0; i < challengeTaskList.size(); i++) {
            cTask = challengeTaskList.get(i);
            latitude = cTask.getLat();
            longitude = cTask.getLon();
            requestID = "geofence_id_" + cTask.getID();
            geographicChallengeTaskFence = builder.setRequestId(requestID)
                                                   .setTransitionTypes(transitionStep)
                                                   .setLoiteringDelay(1)
                                                   .setCircularRegion(latitude, longitude, radius)
                                                   .setExpirationDuration(duration)
                                                   .build();
            list.add(geographicChallengeTaskFence);
        }
        return list;
    }

    /**
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }
    **/

    public void launchFacebookLogin(final boolean isFirstTime) {
        Session.openActiveSession(this, true, new Session.StatusCallback() {
            // callback when session changes state
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                if (session.isOpened()) {
                    // make request to the /me API
                    Request.newMeRequest(session, new Request.GraphUserCallback() {
                        // callback after Graph API response with user object
                        @Override
                        public void onCompleted(GraphUser user, Response response) {
                            if (user != null) {
                                Toast.makeText(getApplicationContext(), "User " + user.getName() + " is now logged in. Nice!", Toast.LENGTH_LONG);
                                Log.e("USERNAME: ", user.getName());
                                if(isFirstTime) {
                                    new AccountUtilities().grabAvatarFromFB(user.getId(), getApplicationContext());
                                    SharedPreferences sp = getSharedPreferences(LoginActivity.PREFS_SETTINGS, 0);
                                    SharedPreferences.Editor sped = sp.edit();
                                    sped.putString("pref_key_username", user.getName()).apply();

                                    //Now lets modify the UI so that it shows to the user we're logged in ok!
                                    Button FBLoginButton = (Button) findViewById(R.id.launchpage_loginButton);
                                    Button noFBLoginButton = (Button) findViewById(R.id.launchpage_noFBloginButton);
                                    FBLoginButton.setVisibility(View.GONE);
                                    noFBLoginButton.setVisibility(View.GONE);
                                    TextView launchMainText = (TextView) findViewById(R.id.app_main_pitch_tv);
                                    TextView launchTapText = (TextView) findViewById(R.id.tap_button_fb_tv);
                                    launchMainText.setVisibility(View.GONE);
                                    launchTapText.setText("Nice! It looks like your login was successful. Go ahead and tap below so we can get started");

                                    //Create button to move fwd...
                                    Button continueButton = (Button) findViewById(R.id.buttonToContinue);
                                    continueButton.setText("Get Started");
                                    continueButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent i = new Intent(getApplicationContext(), Home.class);
                                            startActivity(i);
                                        }
                                    });
                                    continueButton.setVisibility(View.VISIBLE);
                                }
                                else {
                                    Intent i = new Intent(getApplicationContext(), Home.class);
                                    startActivity(i);
                                }
                            }
                        }
                    }).executeAsync();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.launch, menu);
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
