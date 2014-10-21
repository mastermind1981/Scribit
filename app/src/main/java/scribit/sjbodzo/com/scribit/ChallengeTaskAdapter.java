package scribit.sjbodzo.com.scribit;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ChallengeTaskAdapter extends ArrayAdapter<ChallengeTask>  {
    private Context context;
    private List<ChallengeTask> tasks;

    public ChallengeTaskAdapter(Context c, List<ChallengeTask> tasks) {
        super(c, R.layout.challengetask_adapter, tasks);
        this.tasks = tasks;
        context = c;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ChallengeTask task = tasks.get(position);
        LayoutInflater lif = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = lif.inflate(R.layout.challengetask_adapter, parent, false);

        TextView points = (TextView) rowView.findViewById(R.id.challTaskadapter_points_tv);
        TextView title = (TextView) rowView.findViewById(R.id.challTaskadapter_title_tv);
        TextView category = (TextView) rowView.findViewById(R.id.challTaskadapter_cat_tv);
        ImageView doneIcon = (ImageView) rowView.findViewById(R.id.challTaskadapter_clipboard_done_iv);
        ImageView incompleteIcon = (ImageView) rowView.findViewById(R.id.challTaskadapter_clipboard_inc_iv);

        //show icon for whether or not challenge is done yet
        if (task.getStatus() == 1) doneIcon.setVisibility(View.VISIBLE);
        else incompleteIcon.setVisibility(View.VISIBLE);

        Log.e("POINTS?" , task.getPoints() + "\t");

        //set text fields to those of specific challenge
        String pointsy = task.getPoints() + "";
        points.setText(pointsy);
        title.setText(task.getTitle());
        category.setText(task.getCategory());

        return rowView;
    }
}
