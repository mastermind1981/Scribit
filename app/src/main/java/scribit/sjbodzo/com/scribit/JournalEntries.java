package scribit.sjbodzo.com.scribit;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.List;

public class JournalEntries extends ListActivity {
    private PostsDataAccessObject postsTableDAO;
    public static final String PREFS_SETTINGS = "TheSettingsFileYall";
    private View v;
    private ListView lv;
    private GridView gv;
    private Context self;
    private ListAdapter listAdapter;
    private List<Post> posts;
    private PostGridAdapter postsGrid;
    private CustomPostAdapter postsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater lif = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        v = lif.inflate(R.layout.activity_journal_entries, null);
        setContentView(v);
        lv = (ListView) v.findViewById(android.R.id.list);
        gv = (GridView) v.findViewById(R.id.gridview_entries);
        self = this;
        setupDAOAndCursorAdapter(); //hook app into stored DB of entries

        Button addEntryButton = (Button) findViewById(R.id.add_new_entry_button);
        addEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent launchAdd = new Intent(getApplicationContext(), AddEntryWizard.class);
                startActivity(launchAdd);
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(final AdapterView<?> parent, final View view, int position, long id) {
                final Post item = (Post) parent.getItemAtPosition(position);
                /**view.animate().setDuration(200).translationX(140)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                //this.remove(item);
                                //adapter.notifyDataSetChanged();
                                Intent viewPostIntent = new Intent(self, EditPost.class);
                                self.startActivity(viewPostIntent);
                            }
                        });
                 **/
                Intent viewPostIntent = new Intent(self, ViewEntry.class);
                viewPostIntent.putExtra("postEntry", item);
                self.startActivity(viewPostIntent);
            }
        });

        //TODO: animation for loading when busy grabbing list
        //TODO: write onClick
        //TODO: animation for onClick
        //TODO: link to addJournalEntry page
    }

    public void toggleViewAdapter() {
        if (listAdapter instanceof CustomPostAdapter) {
            gv.setVisibility(View.VISIBLE);
            lv.setVisibility(View.GONE);
            this.setListAdapter(postsGrid);
        }
        else {
            lv.setVisibility(View.VISIBLE);
            gv.setVisibility(View.GONE);
            this.setListAdapter(postsList);
        }
        listAdapter = getListAdapter(); //update ref to activity var
        v.invalidate();
    }

    public void setupDAOAndCursorAdapter() {
        postsTableDAO = new PostsDataAccessObject(this); //associate to current Context
        postsTableDAO.open(); //inherited method, sets DB ref
        posts = postsTableDAO.getAllOfThePostyThings();
        postsGrid = new PostGridAdapter(this, posts);
        postsList = new CustomPostAdapter(this, posts);

        SharedPreferences spRef = getSharedPreferences(PREFS_SETTINGS, 0);
        boolean prefersList = spRef.getBoolean("pref_key_prefersList", true);
        if (prefersList) {
            lv.setVisibility(View.VISIBLE);
            gv.setVisibility(View.GONE);
            this.setListAdapter(postsList);
        }
        else {
            gv.setVisibility(View.VISIBLE);
            lv.setVisibility(View.GONE);
            this.setListAdapter(postsGrid);
        }
        listAdapter = getListAdapter();
        v.invalidate();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.journal_entries, menu);
        return true;
    }

    /**
     * onResume & onPause are lifecycle management methods that help make sure
     * that the cursor refs are handled properly if/when user leaves this page
     * of the app for whatever reason (i.e: phone call received or back call to
     * Activity stack
     */

    @Override
    protected void onResume() {
        postsTableDAO.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        postsTableDAO.close();
        super.onPause();
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
