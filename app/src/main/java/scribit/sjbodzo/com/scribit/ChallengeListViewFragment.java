package scribit.sjbodzo.com.scribit;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

public class ChallengeListViewFragment extends ListFragment {
    private ChallengeTaskAdapter challengeTaskAdapter;
    private ChallengeTaskDataAccessObject challDAO;
    private List<ChallengeTask> tasks;
    private Context c;
    private View v;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater lif, ViewGroup vg, Bundle b) {
        challengeTaskAdapter = new ChallengeTaskAdapter(c, tasks);
        setListAdapter(challengeTaskAdapter);

        v = lif.inflate(R.layout.journal_entry_fragment_holder, null);
        AdapterView<Adapter> adv = (AdapterView<Adapter>) v.findViewById(android.R.id.list);
        adv.setAdapter(challengeTaskAdapter);
        return adv;
    }

    @Override
    public void onListItemClick(ListView lv, View v, int position, long id) {
        final ChallengeTask task = (ChallengeTask) lv.getItemAtPosition(position);
        //Intent viewTaskIntent = new Intent(c, ViewTask.class);
        //TODO: create View Task fragment
        //TODO: create Parcelable interface for ChallengeTask
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        c = activity;

        challDAO = new ChallengeTaskDataAccessObject(c);
        challDAO.open();
        tasks = challDAO.getAllThemThereChallenges();
    }
}
