package scribit.sjbodzo.com.scribit;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

import java.util.Comparator;
import java.util.List;

public class ChallengeTrackerListViewFragment extends ListFragment
                                              implements ChallengeTrackerActivity.OnSpinnerFilterSelectionListener,
                                                         GooglePlayServicesClient.OnConnectionFailedListener,
                                                         GooglePlayServicesClient.ConnectionCallbacks {

        private ChallengeTrackerAdapter challengeTrackerAdapter;
        private ChallengeTaskDataAccessObject challTrackDAO;
        private FragmentManager fgm;
        private List<ChallengeTask> list;
        private Context c;
        private String choice;
        private Location l;
        private LocationClient lc;
        private View v;

        //Define Comparators to use for sorting ListView elements in Fragment
        Comparator<ChallengeTask> scoreOrder =  new Comparator<ChallengeTask>() {
            public int compare(ChallengeTask c1, ChallengeTask c2) {
                return (c1.getPoints()>c2.getPoints() ? -1 : (c1.getPoints()==c2.getPoints() ? 0 : 1));
            }
        };
        Comparator<ChallengeTask> distanceOrder =  new Comparator<ChallengeTask>() {
            public int compare(ChallengeTask c1, ChallengeTask c2) {
                double d1, d2;
                if (c1.getLat() == 0 && c1.getLon() == 0) d1 = 999999;
                else d1 = GeographicUtility.haversineFunction(c1.getLat(), l.getLatitude(), c1.getLon(), l.getLongitude());
                if (c2.getLat() == 0 && c2.getLon() == 0) d2 = 999999;
                else d2 = GeographicUtility.haversineFunction(c2.getLat(), l.getLatitude(), c2.getLon(), l.getLongitude());
                return (d2 > d1 ? -1 : (d1 == d2 ? 0 : 1));
            }
        };

        @Override
        public void onConnected(Bundle dataBundle) {
            l = lc.getLastLocation();
        }

        @Override
        public void onDisconnected() {
            Toast.makeText(c, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            /*
             * If the error has a resolution, start a Google Play services
             * activity to resolve it.
             */
            if (connectionResult.hasResolution()) {
                try { connectionResult.startResolutionForResult((Activity)c,9000); //maps 9000 to connection failed request
                } catch (IntentSender.SendIntentException e) { e.printStackTrace(); }
            } else {
                int errorCode = connectionResult.getErrorCode();
                Toast.makeText(c, "onConnectionFailed, ERROR CODE: " + errorCode, Toast.LENGTH_SHORT).show();
                //TODO: treat individual error codes appropriately, ex: if location services turned off prompt user
            }
        }

        @Override
        public void onAttach(Activity a) {
            super.onAttach(a);
            c = a;

            challTrackDAO = new ChallengeTaskDataAccessObject(c);
            challTrackDAO.open();
            list = challTrackDAO.getAllThemThereChallenges();
            challengeTrackerAdapter = new ChallengeTrackerAdapter(c, list);
            challTrackDAO.close();
            lc = new LocationClient(c, this, this);
            lc.connect();
            fgm = getFragmentManager();

            //TODO: set choice to whatever it is set as in preferences (create preference), and set a default
            choice = "Score";
        }

        @Override
        public void onCreate(Bundle b) {
            super.onCreate(b);
        }

        @Override
        public void onListItemClick(ListView lv, View v, int position, long id) {
            final ChallengeTask task = (ChallengeTask) lv.getItemAtPosition(position);
            //Intent viewTaskIntent = new Intent(c, ViewTask.class);
            //TODO: create View Task fragment
            final ChallengeTask item = (ChallengeTask) lv.getItemAtPosition(position);
            Log.e("PRINTING CTASK\t", item.getTitle());
            //FragmentTransaction ft = fgm.beginTransaction();
            ViewTaskEntry vte = ViewTaskEntry.newInstance(item);
            vte.show(fgm, "taskEntryFrag");
        }

        @Override
        public View onCreateView(LayoutInflater lif, ViewGroup vg, Bundle b) {
            v = lif.inflate(R.layout.chall_tracker_fragment_entry, null);
            //Spinner choiceSelection = (Spinner) v.findViewById(R.id.chall_track_spinner);
            AdapterView<Adapter> adv = (AdapterView<Adapter>) v.findViewById(android.R.id.list);

            //String choice = (String) choiceSelection.getSelectedItem();
            Log.e("CHOICE SELECTION: ", choice);
            if (choice.equals("Score")) {
                challengeTrackerAdapter.setFilterByPoints(true);
                challengeTrackerAdapter.sort(scoreOrder);
            }
            else if (choice.equals("Distance")) {
                challengeTrackerAdapter.setFilterByPoints(false);
                challengeTrackerAdapter.sort(distanceOrder);
            }

            setListAdapter(challengeTrackerAdapter);
            adv.setAdapter(challengeTrackerAdapter);
            return adv;
        }

    @Override
    public void onSpinnerChange(String selection) {
        choice = selection;
        if (choice.equals("Score")) challengeTrackerAdapter.sort(scoreOrder);
        else if (choice.equals("Distance")) challengeTrackerAdapter.sort(distanceOrder);
        challengeTrackerAdapter.notifyDataSetChanged();
        Log.e("CHOICE ", selection);
    }
}
