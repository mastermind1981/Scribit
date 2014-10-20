package scribit.sjbodzo.com.scribit;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.List;

public class JournalEntries extends Activity {
    public static final String PREFS_SETTINGS = "TheSettingsFileYall";
    private JournalListViewFragment jlvf;
    private JournalGridViewFragment jgvf;
    private AdapterView<Adapter> adp;
    private FragmentManager fgm;
    private boolean hasList;
    private Context self;
    private View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater lif = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        v = lif.inflate(R.layout.activity_journal_entries, null);
        self = this;

        //hook in fragments to add layout representation dynamically
        jlvf = new JournalListViewFragment();
        jgvf = new JournalGridViewFragment();
        fgm = getFragmentManager();
        setupList();
        setContentView(v);

        Button addEntryButton = (Button) findViewById(R.id.add_new_entry_button);
        addEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent launchAdd = new Intent(getApplicationContext(), AddEntryWizard.class);
                startActivity(launchAdd);
            }
        });

        //TODO: animation for loading when busy grabbing list
        //TODO: animation for onClick
    }

    public void toggleViewAdapter() {
        FragmentTransaction ft = fgm.beginTransaction();
        if (hasList) {
            ft.remove(jlvf);
            ft.add(R.id.journalEnt_vg, jgvf);
        }
        else {
            ft.remove(jgvf);
            ft.add(R.id.journalEnt_vg, jlvf);
        }
        ft.commit();
        hasList = !hasList;
    }

    public void setupList() {
        FragmentTransaction ft = fgm.beginTransaction();
        SharedPreferences spRef = getSharedPreferences(PREFS_SETTINGS, 0);
        boolean prefersList = spRef.getBoolean("pref_key_prefersList", false);
        if (prefersList) {
            hasList = true;
            ft.add(R.id.journalEnt_vg, jlvf);
        }
        else {
            hasList = false;
            ft.add(R.id.journalEnt_vg, jgvf);
        }
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.journal_entries, menu);
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
        else if (id == R.id.new_post_entry) {
            Intent i = new Intent(getApplicationContext(), AddEntryWizard.class);
            startActivity(i);
            return true;
        }
        else if (id == R.id.view_toggle) {
            toggleViewAdapter();
        }
        else {}
        return super.onOptionsItemSelected(item);
    }
}
