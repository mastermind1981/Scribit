package scribit.sjbodzo.com.scribit;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;

public class AvatarPreference extends Preference {

    public AvatarPreference(Context c, AttributeSet aSet) {
        super(c, aSet);
        setLayoutResource(R.layout.pref_avatar_layout);
    }
}
