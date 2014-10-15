package scribit.sjbodzo.com.scribit;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.*;
import com.facebook.model.*;

public class LoginActivity extends Activity {
    public static final String PREFS_SETTINGS = "TheSettingsFileYall";
    private PostsDataAccessObject postsTableDAO;
    private ChallengeTaskDataAccessObject challTableDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //hook up listeners to my UI elements
        Button launchHome_button = (Button) findViewById(R.id.continue_button);
        launchHome_button.setOnClickListener(launchHomeListener);

        // start Facebook Login
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
                                TextView welcome = (TextView) findViewById(R.id.welcome);
                                welcome.setText("Hello " + user.getName() + "!");
                            }
                        }
                    }).executeAsync();
                }
            }
        });

        //prepare data access object to write to DBs
        postsTableDAO = new PostsDataAccessObject(this); //associate to current Context
        postsTableDAO.open(); //inherited method, sets DB ref
        challTableDAO = new ChallengeTaskDataAccessObject(this);
        challTableDAO.open();

        //perform necessary checks if this is first app load
        firstLaunchCheck();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    View.OnClickListener launchHomeListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent launchHomeView = new Intent(getApplicationContext(), Home.class);
            startActivity(launchHomeView);
        }
    };

    //TODO: incorporate inviting tutorial to first time app user?
    public void firstLaunchCheck() {
        SharedPreferences spRef = getSharedPreferences(PREFS_SETTINGS, 0);
        boolean isFirstTimeUser = spRef.getBoolean("pref_key_virginal_ux", true);
        if (isFirstTimeUser) {
            //populate initial posts in application
            Post newPost = postsTableDAO.createJournalPost("Foo 1", "insane post bruh", 23.2, 99.42, "", "January 2, 2010", false, false);
            newPost = postsTableDAO.createJournalPost("Foo 2", "insane aehkpost bruh", 200.0, 99.42, "", "February 22, 2010", false, false);
            newPost = postsTableDAO.createJournalPost("Foo 3", "insakjhkhjkhjkhne post bruh", -100, 99.42, "", "December 17, 2010", false, false);
            newPost = postsTableDAO.createJournalPost("Foo 4", "insakjhkjhkjhne post bruh", 33.99, 99.42, "", "November 14, 2010", false, false);

            ChallengeTask challengeTask = challTableDAO.createChallengeTaskEntry("Post an Entry", "For your first challenge you will post an entry!",
                                                                                 10, "the Newbie", "", false, "Other");

            //set status to already visited
            SharedPreferences.Editor eddy = spRef.edit();
            eddy.putBoolean("pref_key_virginal_ux", false);
            eddy.apply();
        }
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
