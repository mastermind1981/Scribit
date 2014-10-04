package scribit.sjbodzo.com.scribit;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

public class DateEntryFragment extends DialogFragment
                               implements DatePickerDialog.OnDateSetListener{
    OnPostDateChangeListener hostDateChangeListener;

    @Override
    public void onAttach(Activity activity) {
        // Necessary lifecycle method to hook in host activity, for preserving Date information for Post after fragment apoptosis
        super.onAttach(activity);
        try {
            hostDateChangeListener = (OnPostDateChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnPostDateChangeListener to write Date obj in post!");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(getActivity(), this, year, month, day);
        dpd.setTitle("Add Date Info To Journal Entry");
        return dpd;
    }

    // Interface created to allow comm between host Activity and DatePickerDialog fragment.
    // This preserves the date so that when the post is written the proper date is written.
    // Note: ClassCastException thrown if host Activity does not implement interface properly
    public interface OnPostDateChangeListener {
        public void onPostDateChange(int year, String month, int day);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        hostDateChangeListener.onPostDateChange(year, CustomPostAdapter.parseDateMonth(month+1), day);
    }
}
