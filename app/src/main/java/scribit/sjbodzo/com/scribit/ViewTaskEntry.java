package scribit.sjbodzo.com.scribit;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class ViewTaskEntry extends DialogFragment {

    private ChallengeTask task;
    private Context c;
    private View v;

    //TODO: incorporate generic icons for specific challenge categories

    @Override
    public void onAttach(Activity a) {
        super.onAttach(a);
        c = a;
    }

    //convenience method to hook challengeTask into fragment init
    public static ViewTaskEntry newInstance(ChallengeTask ct) {
        ViewTaskEntry vte = new ViewTaskEntry();
        Bundle bag = new Bundle();
        bag.putParcelable("taskEntryFrag", ct);
        vte.setArguments(bag);
        return vte;
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        //Grab Post object from Parcelable in Bundle passed on open
        task = (ChallengeTask) getArguments().getParcelable("taskEntryFrag");
    }

    @Override
    public View onCreateView(LayoutInflater lif, ViewGroup vg, Bundle b) {
        v = lif.inflate(R.layout.fragment_view_task_entry, null);
        Log.e("TITLE?", task.getTitle());

        //TextView title = (TextView) v.findViewById(R.id.view_task_title_tv);
        TextView pts = (TextView) v.findViewById(R.id.view_task_pts_tv);
        TextView unlocks = (TextView) v.findViewById(R.id.view_task_unlocks_tv);
        TextView details = (TextView) v.findViewById(R.id.view_task_rq_details_tv);

        //title.setText(task.getTitle());
        pts.setText(task.getPoints() + " POINTS");
        unlocks.setText(task.getUnlocked());
        details.setText(task.getDescription());

        // Terminate fragment onclick of button.
        Button button = (Button)v.findViewById(R.id.view_task_got_it_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss(); //goodbye
            }
        });

        TextView dialogTitle = (TextView) getDialog().findViewById(android.R.id.title);
        dialogTitle.setText(task.getTitle());
        dialogTitle.setTextColor(Color.BLACK);
        dialogTitle.setAllCaps(true);
        Typeface tF = Typeface.createFromAsset(c.getAssets(), "fonts/Roboto-Regular.ttf");
        dialogTitle.setTypeface(tF);

        return v;
    }
}
