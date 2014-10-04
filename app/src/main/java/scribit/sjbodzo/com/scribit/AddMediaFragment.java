package scribit.sjbodzo.com.scribit;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class AddMediaFragment extends DialogFragment {
    private View v;
    private Activity hostActivity;
    public static final int IMAGE_RQ = 0x1;
    public static final int VID_RQ = 0x2;

    @Override
    public void onAttach(Activity a) {
        super.onAttach(a);
        hostActivity = a; //store ref in fragment for later media handling w/ content provider
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_v_add_media, container, false);
        getDialog().setTitle("Add Media To Your Journal Entry");
        return v;
    }

    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);

        //Associate listeners/functionality for buttons
        ImageButton addImageButton = (ImageButton) v.findViewById(R.id.add_image_ib);
        ImageButton addVidButton = (ImageButton) v.findViewById(R.id.add_video_ib);
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, IMAGE_RQ);
            }
        });
        addVidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, VID_RQ);
            }
        });
    }

    //This method handles the intents to fetch an image or video, filtering by the intent request code, responding accordingly.
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Check if the recent fetch of a media asset was successful before proceeding any further.
        if (resultCode == Activity.RESULT_OK) {
            Uri mediaContentScheme = data.getData(); // gets scheme of data, likely content: managed by content provider

            //Associates data structure column reference to the proper column for the type of media we want.
            String[] mediaPathCol = new String[1];
            if (requestCode == IMAGE_RQ) mediaPathCol[0] = MediaStore.Images.Media.DATA; //defines data structure col as the image col
            else if (requestCode == VID_RQ) mediaPathCol[0] = MediaStore.Video.Media.DATA; //defines data structure col as the video col
            else {} //do nothing for now, because the request was malformed

            //Content Resolve acts like Mediator between structured data & clients who wants to access it, decouples data design from
            //class needs to fetch that data. Data accessed as SQL-like query (projection) of data structure iterated through by cursor.
            //The code below fetches data from the mediator, associates it to a cursor, and grabs the last referenced media (the one we want).
            Cursor mediaCursor = hostActivity.getContentResolver().query(mediaContentScheme, mediaPathCol, null, null, null);
            mediaCursor.moveToFirst(); //this shifts cursor to data selected when our intent fired
            int columnIndex = mediaCursor.getColumnIndex(mediaPathCol[0]); //collects column in data structure of our media
            String pathToMedia = mediaCursor.getString(columnIndex); //path to media parsed at column in data structure
            mediaCursor.close(); //close Cursor after data projection no longer needed
            ((OnAddMediaFileChangeListener) hostActivity).onAddMediaFileChange(pathToMedia); //store data in host activity for DB post write
        }
        else {
            // something went wrong fetching the request (might be that the user has uninstalled media handling apps
            //TODO: provide some sort of feedback that the op in question has failed
        }
    }

    public interface OnAddMediaFileChangeListener {
        public void onAddMediaFileChange(String path);
    }
}
