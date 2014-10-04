package scribit.sjbodzo.com.scribit;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class ViewEntry extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //inflate layout to modify with Entry information before showing
        LayoutInflater lif = getLayoutInflater();
        View v = lif.inflate(R.layout.activity_view_entry, null);

        //Grab Post object from Parcelable in Bundle passed on open
        Bundle bag = getIntent().getExtras();
        Post postEntry = (Post) bag.getParcelable("postEntry");

        //format MONTH to str for layout
        Calendar c = new GregorianCalendar();
        c.setTime(postEntry.getDate());
        String monthStr = CustomPostAdapter.parseDateMonth(c.MONTH);

        //Grab all fields for the UI...
        TextView title_tv = (TextView) v.findViewById(R.id.title_post_view_entry_tv);
        TextView month_tv = (TextView) v.findViewById(R.id.date_month_view_entry_tv);
        TextView day_tv = (TextView)   v.findViewById(R.id.date_day_view_entry_tv);
        TextView year_tv = (TextView)  v.findViewById(R.id.date_year_view_entry_tv);
        TextView desc_tv = (TextView)  v.findViewById(R.id.desc_view_entry_tv);

        //Set the fields for title & description of journal entry...
        desc_tv.setText(postEntry.getDescription());
        title_tv.setText(postEntry.getTitle());
        month_tv.setText(monthStr);

        //TODO: add logic to show if is a challenge
        //TODO: add logic to show gps location information
        //TODO: add logic to handle displaying of media (if any)
        //TODO: hook in actionbar item to edit posting

        //Show the view
        setContentView(v);
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
