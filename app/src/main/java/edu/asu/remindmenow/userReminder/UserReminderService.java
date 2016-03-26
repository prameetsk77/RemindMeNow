package edu.asu.remindmenow.userReminder;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import edu.asu.remindmenow.bluetooth.BluetoothAdvertiser;
import edu.asu.remindmenow.bluetooth.BluetoothReceiver;
import edu.asu.remindmenow.wifi.WifiAdvertiser;
import edu.asu.remindmenow.wifi.WifiReceiver;

/**
 * Created by Jithin Roy on 3/23/16.
 */
public class UserReminderService extends Service {


    private BluetoothAdvertiser advertiser;
    private BluetoothReceiver receiver;

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("Service", "On Service created");
        startAdvertiseService();
      //  startReceiverService();


    }

    private void startAdvertiseService() {
        String userID = "FB_USER_ID";
//        advertiser = new BluetoothAdvertiser(this);
//        advertiser.startAdvertising(userID);

        WifiAdvertiser adv = new WifiAdvertiser(this);
        adv.startRegistration();
        //adv.discoverService();
    }

//    private void startReceiverService() {
//        WifiReceiver rec = new WifiReceiver();
//        rec.discoverService();
//    }

    public class LocalBinder extends Binder {

        public UserReminderService getService() {
            return UserReminderService.this;
        }
    }
}
