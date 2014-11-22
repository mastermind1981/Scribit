package scribit.sjbodzo.com.scribit;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsFragment extends PreferenceFragment {
    SharedPreferences.OnSharedPreferenceChangeListener listener;
    private static final int PICK_PHOTO_REQUEST = 1;

    public SettingsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        listener = ((SharedPreferences.OnSharedPreferenceChangeListener)getActivity());
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settingprefs);
        Preference virgUser = findPreference("pref_key_virginal_ux");
        virgUser.setSelectable(false);

        //Dynamically populate list of title entries
        ListPreference lP = (ListPreference) findPreference("pref_key_titlename");
        ChallengeTaskDataAccessObject cDAO = new ChallengeTaskDataAccessObject(getActivity());
        cDAO.open();
        final String[] unlockedTitlesArray = cDAO.getUnlockedTitles();
        for (int i = 0; i < unlockedTitlesArray.length; i++) {
            Log.e("DBG", unlockedTitlesArray[i] + "");
        }
        lP.setEntries(unlockedTitlesArray);
        cDAO.close();
        lP.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                Log.e("BDGBGBD", unlockedTitlesArray[new Integer(o.toString())-1] + "");
                getActivity().getSharedPreferences("TheSettingsFileYall", 0).edit().putString("pref_key_titlename", unlockedTitlesArray[new Integer(o.toString())-1]).apply();
                Toast.makeText(getActivity(), "Successfully set title to "
                                            + getActivity().getSharedPreferences("TheSettingsFileYall", 0).getString("pref_key_titlename", "?????"),
                                              Toast.LENGTH_SHORT).show();
                return true;
            }
        });


        //setup onClick behavior for Avatar preference
        Preference avPref = findPreference("pref_key_avatar");
        avPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(); //Select the image via intent
                intent.setType("image/*"); //tell Android we want things that return img's
                intent.setAction(Intent.ACTION_GET_CONTENT); //specify action as content accessor only
                intent.addCategory(Intent.CATEGORY_OPENABLE); //only select filetypes that are openable
                startActivityForResult(intent, 1); // 1 is our rq code, so if successfully done, we should get 1 back
                return true;
            }
        });

        //setup onClick behavior for nuking Post DB
        Preference delPref = findPreference("pref_key_delete_all");
        delPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Are you sure?")
                        .setMessage("Deleting all of your journals posts is not a reversible action. Proceed?")
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                PostsDataAccessObject pDAO = new PostsDataAccessObject(getActivity());
                                pDAO.open();
                                pDAO.removeAllPosts();
                                pDAO.close();
                                Toast.makeText(getActivity(), "All posts in your journal have been deleted. A clean slate!", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
                return true;
            }
        });
    }

    @Override
    public void onActivityResult(int rqCode, int resultCode, Intent i) {
        if (resultCode == Activity.RESULT_OK) {
            if (rqCode == PICK_PHOTO_REQUEST) {
                String[] mediaPathCol = new String[1];
                mediaPathCol[0] = MediaStore.Images.Media.DATA;
                Cursor mediaCursor = getActivity().getContentResolver().query(i.getData(), mediaPathCol, null, null, null);
                mediaCursor.moveToFirst(); //this shifts cursor to data selected when our intent fired
                int columnIndex = mediaCursor.getColumnIndex(mediaPathCol[0]); //collects column in data structure of our media
                String pathToMedia = mediaCursor.getString(columnIndex); //path to media parsed at column in data structure
                mediaCursor.close(); //close Cursor after data projection no longer needed
                BitmapDrawable bitmapAV = new BitmapDrawable(getResources(), pathToMedia);

                //Update value for pref key to new string path
                getActivity().getSharedPreferences("TheSettingsFileYall", 0).edit().putString("pref_key_avatar", pathToMedia).apply();
                Toast.makeText(getActivity(), "Nice! Your avatar has successfully been changed.", Toast.LENGTH_SHORT).show();
                //pFrag.findPreference("pref_key_avatar").persist...;

                //try to set avatar ico
                LayoutInflater lif = getActivity().getLayoutInflater();
                View view = lif.inflate(findPreference("pref_key_avatar").getLayoutResource(), null);
                ImageView avatarImageView = (ImageView) view.findViewById(R.id.avatar_iv_pref);
                avatarImageView.setImageDrawable(bitmapAV);
                avatarImageView.setPadding(15, 15, 15, 15);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(listener);
    }
}
