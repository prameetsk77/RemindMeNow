package edu.asu.remindmenow.userReminder;

import android.app.Service;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import android.util.Log;

import java.text.ParseException;

import edu.asu.remindmenow.bluetooth.BluetoothAdvertiser;
import edu.asu.remindmenow.bluetooth.BluetoothReceiver;
import edu.asu.remindmenow.models.Reminder;
import edu.asu.remindmenow.models.UserReminder;
import edu.asu.remindmenow.services.NotificationService;
import edu.asu.remindmenow.userManager.UserSession;
import edu.asu.remindmenow.util.DBConnection;
import edu.asu.remindmenow.util.DatabaseManager;
import edu.asu.remindmenow.util.DateUtilities;


/**
 * Created by Jithin Roy on 3/23/16.
 */
public class UserReminderService extends Service implements BluetoothReceiver.BluetoothReceiverInterface {

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    private BluetoothAdvertiser mAdvertiser;
    private BluetoothReceiver mReceiver;
    private static String TAG = "UserReminderService";

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("Service", "On Service created");
        startAdvertiseService();
        startDiscovery();
    }

    private void startAdvertiseService() {
        Log.i(TAG, "startAdvertiseService");
        String userID = UserSession.getInstance().getLoggedInUser().getId();
        String advId = "RM_"+userID;
        mAdvertiser = new BluetoothAdvertiser(this);

    }

    private void startDiscovery () {
        Log.i(TAG, "startDiscovery");
        mReceiver = new BluetoothReceiver(this,this);
        mReceiver.startDiscovery();
    }

    public class LocalBinder extends Binder {

        public UserReminderService getService() {
            return UserReminderService.this;
        }
    }


    //==============================================================================================

    @Override
    public void didFoundDevice(String deviceName) {
        String userId = "10209187059889895"; //deviceName.replaceAll("RM_", "");
        Log.i(TAG, "Found user - " + userId);


        SQLiteDatabase db = DBConnection.getInstance().openWritableDB();
        DatabaseManager dbManager = new DatabaseManager();
        long remId = dbManager.isUserPresentInReminder(db,userId);

        try {
            // Get the reminder
            if (remId > 0) {
                UserReminder reminder = dbManager.getReminder(db, remId);
                if (DateUtilities.isPastDate(reminder.getEndDate()) == false &&
                        DateUtilities.isFutureDate(reminder.getStartDate()) == false) {

                    Log.i(TAG, "Reminder dates are in range. Should show notification");
                    //new NotificationService().notify(reminder.getReminderTitle()," ", this);

                }
            }

        } catch (ParseException ex) {

        }
        DBConnection.getInstance().closeDB(db);

    }

    @Override
    public void didFinishDiscovery() {
        startDiscovery();
    }
}
