package scribit.sjbodzo.com.scribit;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.List;

public class JournalGridViewFragment extends Fragment {
    private PostGridAdapter postGridAdapter;
    private PostsDataAccessObject postsDAO;
    private List<Post> posts;
    private Context c;
    private View v;

    @Override
    public void onAttach(Activity a) {
        super.onAttach(a);
        c = a;

        postsDAO = new PostsDataAccessObject(c);
        postsDAO.open();
        posts = postsDAO.getAllOfThePostyThings();
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
    }

    @Override
    public View onCreateView(LayoutInflater lif, ViewGroup vg, Bundle b) {
        postGridAdapter = new PostGridAdapter(c, posts);
        v = lif.inflate(R.layout.journal_grid_entry_fragment_holder, null);
        GridView adv = (GridView) v.findViewById(R.id.list);
        adv.setAdapter(postGridAdapter);
        adv.setHorizontalSpacing(10);
        adv.setNumColumns(GridView.AUTO_FIT);
        adv.setAdapter(postGridAdapter);
        adv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, int position, long id) {
                final Post item = (Post) parent.getItemAtPosition(position);
                Intent viewPostIntent = new Intent(c, ViewPostEntry.class);
                viewPostIntent.putExtra("postEntry", item);
                c.startActivity(viewPostIntent);
            }
        });
        return adv;
    }
}