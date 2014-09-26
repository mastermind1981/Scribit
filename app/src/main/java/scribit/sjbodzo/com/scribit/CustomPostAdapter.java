package scribit.sjbodzo.com.scribit;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

public class CustomPostAdapter extends ArrayAdapter<Post> implements AdapterView.OnItemClickListener {
    private final Context context;
    private List<Post> posts;

    public CustomPostAdapter(Context c, List<Post> posts) {
        super(c, R.layout.post_adapter, posts);
        this.posts = posts;
        context = c;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //TODO: cleanup method
        Post p = posts.get(position);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.post_adapter, parent, false);

        TextView monthTV = (TextView) rowView.findViewById(R.id.month_of_post_tv);
        TextView dayTV = (TextView) rowView.findViewById(R.id.day_of_post_tv);
        TextView titleTV = (TextView) rowView.findViewById(R.id.title_of_post_tv);
        TextView challTV = (TextView) rowView.findViewById(R.id.challengeUnlocked_tv);
        ImageView chalStarIV = (ImageView) rowView.findViewById(R.id.star_iv);

        Calendar cale = Calendar.getInstance();
        cale.setTime(p.getDate());
        int month = cale.get(Calendar.MONTH)+1;
        int day = cale.get(Calendar.DATE);
        Log.e("CALENDAR", "MONTH : " + month + "  DAY : " + day);
        monthTV.setText(parseDateMonth(month));
        titleTV.setText(p.getTitle());
        dayTV.setText(new Integer(day).toString());

        if (posts.get(position) instanceof Challenge) {
            chalStarIV.setVisibility(ImageView.VISIBLE);
            challTV.setVisibility(TextView.VISIBLE);
        }

        return rowView;
    }

    public void onItemClick(final AdapterView<?> parent, final View view, int position, long id) {
        final Post item = (Post) parent.getItemAtPosition(position);
        view.animate().setDuration(3000).alpha(0)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        //this.remove(item);
                        //adapter.notifyDataSetChanged();
                        view.setAlpha(1);
                        Intent viewPostIntent = new Intent(parent.getContext(), EditPost.class);
                    }
                });
    }

    public String parseDateMonth(int d) {
        if (d == 1) { return "Jan"; }
        else if (d == 2) { return "Feb"; }
        else if (d == 3) { return "Mar"; }
        else if (d == 4) { return "Apr"; }
        else if (d == 5) { return "May"; }
        else if (d == 6) { return "June"; }
        else if (d == 7) { return "July"; }
        else if (d == 8) { return "Aug"; }
        else if (d == 9) { return "Sept"; }
        else if (d == 10) { return "Oct"; }
        else if (d == 11) { return "Nov"; }
        else if (d == 12) { return "Dec"; }
        else return "NONE";
    }
}

