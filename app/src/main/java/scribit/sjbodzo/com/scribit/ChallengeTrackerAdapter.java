package scribit.sjbodzo.com.scribit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationClient;

import java.util.List;

public class ChallengeTrackerAdapter extends ArrayAdapter<ChallengeTask> implements
                                GooglePlayServicesClient.OnConnectionFailedListener,
                                GooglePlayServicesClient.ConnectionCallbacks   {
    private List<ChallengeTask> tasks;
    private Context context;
    private boolean filterByPoints;
    private LocationClient locationClient;
    private Location location;

    public ChallengeTrackerAdapter(Context c, List<ChallengeTask> tasks) {
        super(c, R.layout.activity_challenge_tracker_adapter, tasks);
        this.tasks = tasks;
        context = c;
        locationClient = new LocationClient(context, this, this);
        locationClient.connect();
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    @Override
    public void onConnected(Bundle dataBundle) {
        boolean hasLocServices = LoginActivity.isLocationEnabled(context);
        if (!hasLocServices) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Location Services Not Setup");
            builder.setMessage("It appears that the GPS service is not enabled. Please check your settings.");
            builder.setPositiveButton("Show Me", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    context.startActivity(intent);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(context, Home.class);
                    context.startActivity(intent);
                }
            });
            builder.create().show();
        }
        else {
            location = locationClient.getLastLocation();
            notifyDataSetChanged();
            Log.e("LOCATION FOUND!!!! ", Double.toString(location.getLatitude()) + " " + Double.toString(location.getLongitude()));
            Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDisconnected() {
        Toast.makeText(context, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * If the error has a resolution, start a Google Play services
         * activity to resolve it.
         */
        if (connectionResult.hasResolution()) {
            try { connectionResult.startResolutionForResult((Activity)context,9000); //maps 9000 to connection failed request
            } catch (IntentSender.SendIntentException e) { e.printStackTrace(); }
        } else {
            int errorCode = connectionResult.getErrorCode();
            Toast.makeText(context, "onConnectionFailed, ERROR CODE: " + errorCode, Toast.LENGTH_SHORT).show();
            //TODO: treat individual error codes appropriately, ex: if location services turned off prompt user
        }
    }

    public void setFilterByPoints(boolean b) { filterByPoints = b; }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ChallengeTask task = tasks.get(position);
        LayoutInflater lif = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView;

        //set text fields to those of specific challenge
        String points = Integer.toString(task.getPoints());
        double x = (location == null) ? 0 : location.getLatitude();
        double y = (location == null) ? 0 : location.getLongitude();
        double distance = GeographicUtility.haversineFunction(x, task.getLat(),
                                                              y, task.getLon());
        distance = round(distance, 2);
        String distStr = Double.toString(distance);
        Log.e("D to " + task.getTitle(), distStr);

        if (position > 0) {
            rowView = lif.inflate(R.layout.activity_challenge_tracker_adapter, parent, false);

            TextView leftDetail = (TextView) rowView.findViewById(R.id.chall_track_adapter_leftdetail_tv);
            TextView underDetail = (TextView) rowView.findViewById(R.id.chall_track_adapter_underdetail_tv);
            TextView titleDetail = (TextView) rowView.findViewById(R.id.chall_track_adapter_title_tv);

            titleDetail.setText(task.getTitle());
            if (filterByPoints) {
                leftDetail.setText(points + " PTS");
                if (task.getLon() == 0 && task.getLat() == 0) underDetail.setText("No associated location");
                else underDetail.setText(distStr + " miles away");
            }
            else {
                if (task.getLon() == 0 && task.getLat() == 0) leftDetail.setText("No associated location");
                else leftDetail.setText(distStr + " MI");
                underDetail.setText(points + "points up for grabs");
            }
        }

        //Special case where 1st row is displayed prominently
        else {
            rowView = lif.inflate(R.layout.activity_challenge_tracker_adapter_first_row, parent, false);
            TextView leftDetail = (TextView) rowView.findViewById(R.id.chall_track_adapter_leftdetail_tv);
            TextView underDetail = (TextView) rowView.findViewById(R.id.chall_track_adapter_underdetail_tv);
            TextView titleDetail = (TextView) rowView.findViewById(R.id.chall_track_adapter_title_tv);

            titleDetail.setText(task.getTitle());
            if (filterByPoints) {
                ImageView leftDetPriceTagIV = (ImageView) rowView.findViewById(R.id.leftDetailPriceTagIcon);
                leftDetPriceTagIV.setVisibility(View.VISIBLE);
                ImageView rightDetLocIconIV = (ImageView) rowView.findViewById(R.id.rightDetailLocIcon);
                rightDetLocIconIV.setVisibility(View.VISIBLE);
                leftDetail.setText(points + " PTS");
                if (task.getLat() == 0 && task.getLon() == 0) underDetail.setText("N/A");
                else underDetail.setText(distStr + " MILES");
            }
            else {
                ImageView leftDetLocIconIV = (ImageView) rowView.findViewById(R.id.leftDetailLocIcon);
                leftDetLocIconIV.setVisibility(View.VISIBLE);
                ImageView rightDetPriceTagIV = (ImageView) rowView.findViewById(R.id.rightDetailPriceTagIcon);
                rightDetPriceTagIV.setVisibility(View.VISIBLE);
                leftDetail.setText(distStr + " MI");
                underDetail.setText(points + "POINTS");
            }
        }

        return rowView;
    }
}
