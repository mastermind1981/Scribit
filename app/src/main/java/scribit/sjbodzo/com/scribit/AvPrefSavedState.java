package scribit.sjbodzo.com.scribit;

import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;

public class AvPrefSavedState extends Preference.BaseSavedState {
    String value; //ref to path for Drawable / Bitmap

    public AvPrefSavedState(Parcelable superState) {
        super(superState);
    }

    public AvPrefSavedState(Parcel source) {
        super(source);
        value = source.readString(); //Get the current preference's value
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(value);  //Write the preference's value
    }

    public static final Parcelable.Creator<AvPrefSavedState> CREATOR =
            new Parcelable.Creator<AvPrefSavedState>() {

                public AvPrefSavedState createFromParcel(Parcel in) {
                    return new AvPrefSavedState(in);
                }

                public AvPrefSavedState[] newArray(int size) {
                    return new AvPrefSavedState[size];
                }
            };
}

