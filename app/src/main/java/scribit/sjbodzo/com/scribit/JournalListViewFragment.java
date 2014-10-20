package scribit.sjbodzo.com.scribit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.app.ListFragment;

import java.util.List;

public class JournalListViewFragment extends ListFragment implements AdapterView.OnItemClickListener {
    private CustomPostAdapter postListAdapter;
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
        postListAdapter = new CustomPostAdapter(c, posts);
        setListAdapter(postListAdapter);

        v = lif.inflate(R.layout.journal_entry_fragment_holder, null);
        AdapterView<Adapter> adv = (AdapterView<Adapter>) v.findViewById(android.R.id.list);
        adv.setAdapter(postListAdapter);
        adv.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        adv.setOnItemClickListener(this);
        return v;
    }

    public void onItemClick(final AdapterView<?> parent, final View view, int position, long id) {
        final Post item = (Post) parent.getItemAtPosition(position);
        Intent viewPostIntent = new Intent(c, ViewEntry.class);
        viewPostIntent.putExtra("postEntry", item);
        c.startActivity(viewPostIntent);
    }
}