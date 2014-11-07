package scribit.sjbodzo.com.scribit;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import java.util.List;

public class ChallengeTrackerIntentService extends IntentService {
    public static final String ACTION_USER_FOREGROUND = "android.intent.action.USER_FOREGROUND";

    //default constructor required w/ super call and unique string ID
    public ChallengeTrackerIntentService() {
        super("ChallengeTrackerIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            if (LocationClient.hasError(intent))
                Log.e("ReceiveTransitionsIntentService", "Location Services error: " + Integer.toString(LocationClient.getErrorCode(intent)));
            if (!(ACTION_USER_FOREGROUND.equals(intent.getAction()))) Log.e("scribit.sjbodzo.com.scribit", "USER ACTION MISMSATCH!");
            final String action = intent.getAction();
            if (ACTION_USER_FOREGROUND.equals(action) && !(LocationClient.hasError(intent))) {
                int transitionType = LocationClient.getGeofenceTransition(intent);
                Log.e("scribit.sjbodzo.com.scribit", "TRANSITION TYPE " + transitionType + "");
                //if (!(transitionType == Geofence.GEOFENCE_TRANSITION_ENTER)) return;
                List<Geofence> triggerList = LocationClient.getTriggeringGeofences(intent);
                String[] triggerIds = new String[triggerList.size()];
                for (int i = 0; i < triggerIds.length; i++) {
                    // Store the Id of each geofence
                    triggerIds[i] = triggerList.get(i).getRequestId();
                }

                Log.e("scribit.sjbodzo.com.scribit", " NOTIFYING THE USER!");
                Notification.Builder notificationBuilder = new Notification.Builder(this)
                                                                 .setSmallIcon(R.drawable.trophyicon_iv)
                                                                 .setContentTitle("Scribit")
                                                                 .setContentText("Nearby Challenge Detected! ID: " + triggerIds[0]);
                Intent resultIntent = new Intent(this, Home.class); //where the notification takes you

                //Preserve back nav to Android Home Screen
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                stackBuilder.addParentStack(Home.class);  //back stack for Intent
                stackBuilder.addNextIntent(resultIntent); //adds Intent to start Activity on top
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                notificationBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(0xf, notificationBuilder.build());
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
