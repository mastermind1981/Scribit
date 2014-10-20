package scribit.sjbodzo.com.scribit;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SettingsActivity extends PreferenceActivity {
    Bitmap bp;
    ImageView avIV;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settingprefs);

        final SharedPreferences spRef = getSharedPreferences(JournalEntries.PREFS_SETTINGS, 0);
        boolean isFirstTimeUser = spRef.getBoolean("pref_key_virginal_ux", true);

        //make check for 1st time users invisible in settings menu
        PreferenceCategory pfc = (PreferenceCategory) findPreference("pref_key_acct_settings");
        CheckBoxPreference cbp = (CheckBoxPreference) findPreference("pref_key_virginal_ux");
        CheckBoxPreference gpsbp = (CheckBoxPreference) findPreference("pref_key_toggle_gps");
        pfc.removePreference(cbp);

        //hook in onClick listener event for custom Avatar Preference obj
        AvatarPreference avp = (AvatarPreference) findPreference("pref_key_avatar");
        //set initial imageview avatar
        LayoutInflater lif = getLayoutInflater();
        View v = lif.inflate(avp.getLayoutResource(), null);
        avIV = (ImageView) findViewById(R.id.av_pref_ref);
        //fetchIVContent(avIV);

        //Hook listener into Avatar image on Preferences apge.
        Preference.OnPreferenceClickListener pOpCL = new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference p) {
                selectImage();
                return true;
            }

            public void selectImage() {
                Intent intent = new Intent();
                intent.setType("image/*"); //tell Android we want things that return img's
                intent.setAction(Intent.ACTION_GET_CONTENT); //specify action as content accessor
                intent.addCategory(Intent.CATEGORY_OPENABLE); //only select filetypes that are openable
                startActivityForResult(intent, 1); // 1 is our rq code, so if successfully done, we should get 1 back
            }
        };
        avp.setOnPreferenceClickListener(pOpCL);

        Preference.OnPreferenceClickListener toggleListPref = new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences.Editor ed = spRef.edit();
                boolean curVal = spRef.getBoolean("pref_key_prefersList", true);
                ed.putBoolean("pref_key_prefersList", !curVal);
                ed.apply();
                CheckBoxPreference cbp = (CheckBoxPreference)preference;
                cbp.setChecked(!(cbp.isChecked()));
                return true;
            }
        };
        CheckBoxPreference cbp2 = (CheckBoxPreference) findPreference("pref_key_prefersList");
        cbp2.setOnPreferenceClickListener(toggleListPref);

        //Hook listener to update GPS pref accordingly
        Preference.OnPreferenceClickListener doGPSL = new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences.Editor ed = spRef.edit();
                boolean curVal = spRef.getBoolean("pref_key_toggle_gps", true);
                ed.putBoolean("pref_key_toggle_gps", !curVal);
                ed.apply();
                return true;
            }
        };
        gpsbp.setOnPreferenceClickListener(doGPSL);
    }

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
    }

    public void writeNewAvatar(Bitmap b) {
        //grab handle to app's drawable for layout resource to persist w/
         String extDir = Environment.getExternalStorageDirectory().toString();
         File file = new File(extDir, "ic_avatar.png");
         FileOutputStream outStream = null;
         try { outStream = new FileOutputStream(file); }
         catch (FileNotFoundException nfe) { nfe.printStackTrace(); }
         b.compress(Bitmap.CompressFormat.PNG, 100, outStream);
         try {
             outStream.flush();
             outStream.close();
         }
         catch (IOException ioe) { ioe.printStackTrace(); }
    }

    @Override
    protected void onActivityResult(int rqCode, int resultCode, Intent i) {
        if (resultCode == Activity.RESULT_OK) {
            if (rqCode == 1) {
                InputStream ips = null;
                try {
                    ips = getContentResolver()
                          .openInputStream(i.getData());
                }
                catch (FileNotFoundException nfe) { Log.e("FETCHING PIC!", "ERROR OPENING STREAM"); nfe.printStackTrace(); }
                //check to see if the bitmap object can be reused, otherwise lingers in garbage,
                //even if I instantiate to another Bitmap ref! x_X
                if (bp != null) { bp.recycle(); bp = null; }
                bp = BitmapFactory.decodeStream(ips);
                bp = Bitmap.createScaledBitmap(bp, 60, 60, false);
                BitmapDrawable bpd = new BitmapDrawable(getResources(), bp);
                writeNewAvatar(bp);

                try { ips.close(); } catch (IOException ioe) { Log.e("FETCHING PIC!", "ERROR CLOSING STREAM"); ioe.printStackTrace(); }

                //try to set avatar icon
                avIV.setImageDrawable(bpd);
                avIV.setPadding(15,15,15,15);
                avIV.invalidate();
                Log.e("IMAGESET\t", "image bitmap set to " + bp.toString());

                //TODO: store this avatar in settings to persist!
            }
        }
    }
}
