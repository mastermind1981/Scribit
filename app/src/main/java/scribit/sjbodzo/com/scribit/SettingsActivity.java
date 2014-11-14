package scribit.sjbodzo.com.scribit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SettingsActivity extends PreferenceActivity
                              implements SharedPreferences.OnSharedPreferenceChangeListener {
    PreferenceFragment pFrag;
    SharedPreferences spRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pFrag = new SettingsFragment();
        getFragmentManager().beginTransaction()
                            .replace(android.R.id.content, pFrag)
                            .commit();

        spRef = getSharedPreferences("TheSettingsFileYall", 0);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("pref_key_toggle_gps")) {
            Preference gpsPref = pFrag.findPreference(key);
            String disabled = "GPS services within the app are now disabled. Location data will not be" +
                    " recorded when saving a journal post.";
            String enabled = "GPS services within the app are now enabled. Location data will be recorded" +
                    " when posting journal entries in the app.";
            boolean isGPS = spRef.getBoolean("pref_key_toggle_gps", false);
            spRef.edit().putBoolean("pref_key_toggle_gps", !isGPS).apply();
            if (spRef.getBoolean("pref_key_toggle_gps", false)) gpsPref.setSummary(enabled);
            else gpsPref.setSummary(disabled);
        } else if (key.equals("pref_key_prefersList")) {
            Preference journalLayoutPref = pFrag.findPreference(key);
            String prefGrid = "Current user preference is set to prefer showing journal entries in a list.";
            String prefList = "Current user preference is set to prefer showing journal entries in a grid.";
            boolean prefersList = spRef.getBoolean("pref_key_prefersList", true);
            spRef.edit().putBoolean("pref_key_prefersList", !prefersList).apply();
            if (spRef.getBoolean("pref_key_prefersList", true)) journalLayoutPref.setSummary(prefList);
            else journalLayoutPref.setSummary(prefGrid);
        } else if (key.equals("pref_key_avatar")) { }
        else if (key.equals("pref_key_delete_all")) { }
    }

    /**
    public void fetchIVContent(ImageView iv) {
        String fileLoc = Environment.getExternalStorageDirectory().toString();
        File fileo = new File(fileLoc, "ic_avatar.png");
        if (fileo.exists()) {
            Log.e("DEBUG LOG\t", "FILE.GETABSPATH =" + fileo.getAbsolutePath());
            Log.e("DEBUG LOG\t\n\n\n", "FILE.FILELOC + /ic_avatar.png =" + fileLoc + "/ic_avatar.png");
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap myB = BitmapFactory.decodeFile(fileo.getAbsolutePath(), options);
            BitmapDrawable bpd = new BitmapDrawable(getResources(), myB);
            avIV.setImageDrawable(bpd);
            avIV.setPadding(15,15,15,15);
            avIV.invalidate();
        }
     } **/
}
