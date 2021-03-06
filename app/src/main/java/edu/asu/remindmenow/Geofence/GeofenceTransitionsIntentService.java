package edu.asu.remindmenow.geofence;

import android.app.IntentService;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationServices;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.asu.remindmenow.R;
import edu.asu.remindmenow.activities.MainActivity;
import edu.asu.remindmenow.models.ZoneReminder;
import edu.asu.remindmenow.services.NotificationService;
import edu.asu.remindmenow.util.DBConnection;
import edu.asu.remindmenow.util.DatabaseManager;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * Created by Prameet Singh on 4/12/2016.
 */
public class GeofenceTransitionsIntentService extends IntentService {

    ///////////

    String TAG = "GeoFence Transition Service";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public GeofenceTransitionsIntentService(String name) {
        super(name);
    }

    public  GeofenceTransitionsIntentService() {
        super(null);
    }
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    geofencingEvent.getErrorCode());
            new NotificationService().notify("Z","GEOFENCE", "ERROR " + errorMessage , this);
            Log.e(TAG, errorMessage);
            return;
        }

        ZoneReminder zoneReminder = new ZoneReminder();

        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        String ID = geofenceList.get(0).getRequestId();
        //find from database function and delete

        SQLiteDatabase db = DBConnection.getInstance().openWritableDB();
        DatabaseManager dbManager = new DatabaseManager();
        zoneReminder = dbManager.getZoneReminderFromReqID(db, ID);
        DBConnection.getInstance().closeDB(db);

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        Date startDate = null;
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        try {
            startDate = sdf.parse(zoneReminder.getStartDate()+" "+ zoneReminder.getStartTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long startDatemillis = startDate.getTime();

        Date endDate = null;
        try {
            endDate = sdf.parse(zoneReminder.getEndDate()+" "+ zoneReminder.getEndTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long endDatemillis = endDate.getTime();

        // Test that the reported transition was of interest.
        if ( geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT  && System.currentTimeMillis() > startDatemillis && System.currentTimeMillis() < endDatemillis) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    this,
                    geofenceTransition,
                    triggeringGeofences
            );
            new NotificationService().notify("Z" , "Zone Reminder",  zoneReminder.getReminderTitle() , this);
            // Send notification and log the transition details.
            //sendNotification(geofenceTransitionDetails);
            Log.i(TAG, geofenceTransitionDetails);
        }
        else if ( System.currentTimeMillis() < startDatemillis) {
            Toast.makeText(this, TAG + " Transition before Start Date", Toast.LENGTH_SHORT).show();
            //delete from dB
        }
        else if (System.currentTimeMillis() > endDatemillis) {
            Toast.makeText(this, TAG + " Transition after end Date", Toast.LENGTH_SHORT).show();
            //delete from dB
        }
        else {
            // Log the error.
            Log.e(TAG, "Invalid Transition");
        }

    }

    private String getGeofenceTransitionDetails(
            Context context,
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList triggeringGeofencesIdsList = new ArrayList();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType    A transition type constant defined in Geofence
     * @return                  A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);
            default:
                return getString(R.string.unknown_geofence_transition);
        }
    }


}
