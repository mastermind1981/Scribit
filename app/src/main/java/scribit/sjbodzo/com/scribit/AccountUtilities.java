package scribit.sjbodzo.com.scribit;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import com.facebook.widget.ProfilePictureView;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *  Performs Account actions. ex: changing your account's avatar on the device
 */

public class AccountUtilities {

    //Writes in file ref on device to localized copy of FB profile pic
    public static void grabAvatarFromFB(String userID, Context c) {
        Log.e("ACCOUNT ID: ", userID);
        ProfilePictureView FBPicView = new ProfilePictureView(c);
        FBPicView.setPresetSize(ProfilePictureView.SMALL); //small so can use cache?
        FBPicView.setProfileId(userID);
        ImageView picView = (ImageView)FBPicView.getChildAt(0);
        Bitmap b = picView.getDrawingCache();

        //Bitmap image_saved=BitmapFactory.decodeResource(c);
        String path = Environment.getExternalStorageDirectory()+ "/scribit/prof/pic.jpg";
        FileOutputStream fOut= null;
        try {
            fOut = new FileOutputStream(path);
        } catch (FileNotFoundException e) {
            Log.e("ERROR", e.toString());
        }
        b.compress(Bitmap.CompressFormat.JPEG,100,fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            Log.e("ERROR", e.toString());
        }
        try {
            fOut.close();
        } catch (IOException e) {
            Log.e("ERROR", e.toString());
        }

        SharedPreferences spRef = c.getSharedPreferences(LoginActivity.PREFS_SETTINGS, 0);
        SharedPreferences.Editor ed = spRef.edit();
        ed.putString("pref_key_avatar", path); //write path to pref for retrieval in Home activity
        ed.commit();
    }
}
