package edu.asu.remindmenow.userReminder;

import android.app.Service;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import android.util.Log;

import edu.asu.remindmenow.bluetooth.BluetoothAdvertiser;
import edu.asu.remindmenow.bluetooth.BluetoothReceiver;
import edu.asu.remindmenow.userManager.UserSession;


/**
 * Created by Jithin Roy on 3/23/16.
 */
public class UserReminderService extends Service implements BluetoothReceiver.BluetoothReceiverInterface {

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    private BluetoothAdvertiser mAdvertiser;
    private BluetoothReceiver mReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("Service", "On Service created");
        startAdvertiseService();
    }

    private void startAdvertiseService() {
        Log.i("Service", "startAdvertiseService");
        String userID = UserSession.getInstance().getLoggedInUser().getId();
        String advId = "RM_"+userID;
        mAdvertiser = new BluetoothAdvertiser(this);

    }

    private void startDiscovery () {
        mReceiver = new BluetoothReceiver(this,this);
    }

    public class LocalBinder extends Binder {

        public UserReminderService getService() {
            return UserReminderService.this;
        }
    }

    @Override
    public void didFoundDevice(String deviceName) {

    }

    @Override
    public void didFinishDiscovery() {

    }
}
