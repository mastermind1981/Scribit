package scribit.sjbodzo.com.scribit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class AddEntryWizard extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry_wizard);

        Button newPostButton = (Button) findViewById(R.id.add_new_entry_button);
        newPostButton.setOnClickListener(launchAddNewEntryListener);
    }

    View.OnClickListener launchAddNewEntryListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent launchNewEntryIntent = new Intent(getApplicationContext(), AddEntryWizard.class);
            startActivity(launchNewEntryIntent);
        }
    };


    @Override
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
}
