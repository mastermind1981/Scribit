package scribit.sjbodzo.com.scribit;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AvatarPreference extends Preference {
    public String s;
    private Context c;
    private static final String DEFAULT_VALUE = "";

    public AvatarPreference(Context c, AttributeSet aSet) {
        super(c, aSet);
        this.c = c;
        setLayoutResource(R.layout.pref_avatar_layout);


        SharedPreferences spRef = c.getSharedPreferences("TheSettingsFileYall", 0);
        String path = spRef.getString("pref_key_avatar", "R.drawable.ic_avatar");
        if (!path.equals("R.drawable.ic_avatar")) {
            
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    /**
    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            // Restore existing state
            s = this.getPersistedString(DEFAULT_VALUE);
        } else {
            s = "";
            //Drawable mustachedManAvatar = c.getResources().getDrawable(R.drawable.ic_avatar);
            //InputStream inputStream = (InputStream) c.getResources().openRawResource(R.drawable.ic_avatar);

            /**
            // Set default state from the XML attribute
            String val = null;
            Bitmap bp = null;
            try
            {
                File file = new File("def_av_mustache");
                bp = BitmapFactory.decodeStream(ips);
                bp = Bitmap.createScaledBitmap(bp, 60, 60, false);
                BitmapDrawable bpd = new BitmapDrawable(getResources(), bp);
                writeNewAvatar(bp);
                InputStream inputStream = c.getResources().openRawResource(R.id.);
                OutputStream out = new FileOutputStream(file);
                byte buf[] = new byte[1024];
                int len;
                while((len = inputStream.read(buf))>0) out.write(buf,0,len);
                out.close();
                inputStream.close();
            }
            catch (IOException e){}
            persistString(val);

        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        // Check whether this Preference is persistent (continually saved)
        if (isPersistent()) {
            // No need to save instance state since it's persistent,
            // use superclass state
            return superState;
        }

        // Create instance of custom BaseSavedState
        final AvPrefSavedState myState = new AvPrefSavedState(superState);
        // Set the state's value with the class member that holds current
        // setting value
        myState.value = mNewValue;
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        // Check whether we saved the state in onSaveInstanceState
        if (state == null || !state.getClass().equals(AvPrefSavedState.class)) {
            // Didn't save the state, so call superclass
            super.onRestoreInstanceState(state);
            return;
        }

        // Cast state to custom BaseSavedState and pass to superclass
        AvPrefSavedState myState = (AvPrefSavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());

        // Set this Preference's widget to reflect the restored state
        mNumberPicker.setValue(myState.value);
    }
    **/
}
