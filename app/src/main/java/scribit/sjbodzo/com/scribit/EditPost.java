package scribit.sjbodzo.com.scribit;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditPost extends Activity {
    private EditText titleET, locET, timeET, descET;
    private Uri attachedMedia;
    private boolean isChallenge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        titleET = (EditText) findViewById(R.id.title_tv);
        locET = (EditText) findViewById(R.id.location_tv);
        timeET = (EditText) findViewById(R.id.time_tv);
        descET = (EditText) findViewById(R.id.inputdesc_et);

        //TODO: create popup Spinner/Picker of Challenge

        Button submitButton = (Button) findViewById(R.id.submit_button);
        submitButton.setOnClickListener(submitPostListener);
    }

    View.OnClickListener submitPostListener = new View.OnClickListener() {
        public void onClick(View v) {
            submitPostToStorage();
            Intent submitPostIntent = new Intent(getApplicationContext(), JournalEntries.class);
            startActivity(submitPostIntent);
        }
    };

    private void submitPostToStorage() {
        if (titleET.getText() != null) {
            if (descET.getText() != null) {
                //use postopenhelper instead
            }
            else Toast.makeText(getBaseContext(), "You must include a description", Toast.LENGTH_LONG).show();
        }
        else  Toast.makeText(getBaseContext(), "You must include a title", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_post, menu);
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
