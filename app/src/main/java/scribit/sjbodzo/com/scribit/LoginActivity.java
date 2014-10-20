package scribit.sjbodzo.com.scribit;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
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
import com.facebook.model.GraphUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends Activity {
    public static final String PREFS_SETTINGS = "TheSettingsFileYall";
    private PostsDataAccessObject postsTableDAO;
    private ChallengeTaskDataAccessObject challTableDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        Button FBLoginButton = (Button) findViewById(R.id.launchpage_loginButton);
        FBLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchFacebookLogin();
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


    //TODO: incorporate inviting tutorial to first time app user?
    public void firstLaunchCheck() {
        SharedPreferences spRef = getSharedPreferences(PREFS_SETTINGS, 0);
        boolean isFirstTimeUser = spRef.getBoolean("pref_key_virginal_ux", true);
        if (isFirstTimeUser) {
            //populate initial posts in application
            //prepare data access object to write to DBs
            postsTableDAO = new PostsDataAccessObject(this); //associate to current Context
            postsTableDAO.open(); //inherited method, sets DB ref
            challTableDAO = new ChallengeTaskDataAccessObject(this);
            challTableDAO.open();
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
        else {
            Intent i = new Intent(getApplicationContext(), Home.class);
            startActivity(i);
        }
    }

    /**
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }
    **/

    public void launchFacebookLogin() {
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
                                Log.e("USERNAME: ", user.getName());
                                AccountUtilities.grabAvatarFromFB(user.getId(), getApplicationContext());
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
