package scribit.sjbodzo.com.scribit;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import com.facebook.widget.ProfilePictureView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 *  Performs Account actions. ex: changing your account's avatar on the device
 */

public class AccountUtilities {

    //Writes in file ref on device to localized copy of FB profile pic
    public void grabAvatarFromFB(String userID, Context c) {
        Log.e("ACCOUNT ID: ", userID);
        //ProfilePictureView FBPicView = new ProfilePictureView(c);
        //FBPicView.setPresetSize(ProfilePictureView.SMALL); //small so can use cache?
        //FBPicView.setProfileId(userID);
        //ImageView picView = (ImageView) FBPicView.getChildAt(0);

        //Call AsyncTask to trigger retrieval of profile picture & write new file path to settings.
        URL url = null;
        try {
            url = new URL("https://graph.facebook.com/" + userID + "/picture?type=large");
            new DownloadProfilePictureTask(c).execute(url);
        }
        catch (MalformedURLException malfoy) {
            Log.e("ERROR:", malfoy.toString());
            Log.e("ERROR:", "The profile ID may be bad. The picture couldn't be fetched.");
        }
    }

    private class DownloadProfilePictureTask extends AsyncTask<URL, Void, Void> {
        Bitmap profilePictureBitmap;
        FileOutputStream fOut;
        String path;
        Context c;

        private DownloadProfilePictureTask(Context c) {
            this.c = c;
        }

        protected Void doInBackground(URL... url) {
            //Set up local file in external storage path to save the image.
            path = "";
            FileOutputStream fOut = null;
            try {
                File file = new File(c.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "profPic.jpg");
                path = file.getPath();
                Log.d("FILE INFO:", "\n" + file.getPath() + "\n" + file.getAbsolutePath() + "\n" + file.getCanonicalPath());
                if (!file.exists()) file.createNewFile();
                fOut = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                Log.e("ERROR", e.toString());
            } catch (IOException ioe) {
                Log.e("ERROR", ioe.toString());
                Log.e("ERROR", "File could not be created.");
            }

            //Fetch Facebook profile picture from Graph API.
            try {
                URLConnection urlConnect = url[0].openConnection();
                HttpURLConnection con = (HttpURLConnection)(urlConnect);
                con.setInstanceFollowRedirects(true); //Graph API redirects endpoint
                InputStream iStream = con.getInputStream();
                BufferedInputStream buffer = new BufferedInputStream(iStream);
                profilePictureBitmap = BitmapFactory.decodeStream(buffer);
            }
            catch(IOException ioe) { Log.e("ASYNC ERROR:", ioe.toString()); }
            if (isCancelled()) Log.d("OUCH!", "CANCELLED FB PIC FETCH.");

            //Now compress the bitmap fetched so that we can store/work with it efficiently!
            try {
                profilePictureBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            }
            catch (NullPointerException npe) {
                Log.e("ERROR:", npe.toString());
                Log.e("ERROR:", "The bitmap is null. Cancelling attempt to set Facebook pic to profile.");
                return null;
            }
            try {
                fOut.flush();
            } catch (IOException e) {
                Log.e("ERROR", e.toString());
                Log.e("ERROR", "Could not flush output stream.");
            }
            try {
                fOut.close();
            } catch (IOException e) {
                Log.e("ERROR", e.toString());
                Log.e("ERROR", "Could not close output stream.");
            }

            SharedPreferences spRef = c.getSharedPreferences(LoginActivity.PREFS_SETTINGS, 0);
            SharedPreferences.Editor ed = spRef.edit();
            ed.putString("pref_key_avatar", path); //write path to pref for retrieval in Home activity
            ed.apply();
            Log.d("WROTE PATH", spRef.getString("pref_key_avatar", "NONE"));

            return null;
        }
    }
}
