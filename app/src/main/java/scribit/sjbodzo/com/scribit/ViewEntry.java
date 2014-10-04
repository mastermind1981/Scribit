package scribit.sjbodzo.com.scribit;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.w3c.dom.Text;


public class ViewEntry extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_entry);

        Bundle bag = getIntent().getExtras();
        Post postEntry = (Post) bag.getParcelable("postEntry");

        //Grab all fields for the UI...
        TextView title_tv = (TextView) findViewById(R.id.title_post_view_entry_tv);
        TextView month_tv = (TextView) findViewById(R.id.date_month_view_entry_tv);
        TextView day_tv = (TextView) findViewById(R.id.date_day_view_entry_tv);
        TextView year_tv = (TextView) findViewById(R.id.date_year_view_entry_tv);
        TextView desc_tv = (TextView) findViewById(R.id.desc_view_entry_tv);

        //Set the fields...
        //TODO: set rest of UI fields
        desc_tv.setText(postEntry.getDescription());
        title_tv.setText(postEntry.getTitle());

        setContentView(R.layout.activity_view_entry);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_entry, menu);
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
