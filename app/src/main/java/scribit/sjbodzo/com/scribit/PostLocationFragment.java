package scribit.sjbodzo.com.scribit;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

public class PostLocationFragment extends DialogFragment
                                  implements DialogInterface.OnDismissListener {
    OnPostLocationChangeListener hostLocationChangeListener;

    @Override
    public void onAttach(Activity activity) {
        // Necessary lifecycle method to hook in host activity, for preserving Date information for Post after fragment apoptosis
        super.onAttach(activity);
        try {
            hostLocationChangeListener = (OnPostLocationChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnPostLocationChangeListener to write location in post!");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_post_location, container, false);
        Button OKbutton = (Button) v.findViewById(R.id.ok_postlocfrag_button);
        OKbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        getDialog().setTitle("Add Location Info To Journal Entry");
        return v;
    }

    // Interface created to allow comm between host Activity and DatePickerDialog fragment.
    // This preserves the date so that when the post is written the proper date is written.
    // Note: ClassCastException thrown if host Activity does not implement interface properly
    public interface OnPostLocationChangeListener {
        public void onPostLocationChange(double gpsx, double gpsy);
    }

    public void OnDismiss(DialogInterface dialog) {
        //TODO: fix with google map so that it shows location (map?) and stores it; also allow photos sans location
        hostLocationChangeListener.onPostLocationChange(51.0817, -4.058);
    }
}
