package edu.asu.remindmenow.userReminder;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import edu.asu.remindmenow.bluetooth.BluetoothAdvertiser;
import edu.asu.remindmenow.bluetooth.BluetoothReceiver;

/**
 * Created by Jithin Roy on 3/23/16.
 */
public class UserReminderService extends Service {


    private BluetoothAdvertiser advertiser;
    private BluetoothReceiver receiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    private void startAdvertiseService() {
        String userID = "FB_USER_ID";
        advertiser = new BluetoothAdvertiser(this);
        advertiser.startAdvertising(userID);
    }

    private void startReceiverService() {

    }
}
