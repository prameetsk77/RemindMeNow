package edu.asu.remindmenow.userReminder;

import android.app.Service;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import android.util.Log;

import edu.asu.remindmenow.nearby.NearbyAPIManager;
import edu.asu.remindmenow.userManager.UserSession;

/**
 * Created by Jithin Roy on 3/23/16.
 */
public class UserReminderService extends Service {

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

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
        String userID = "FB_USER_ID";

    }


    public class LocalBinder extends Binder {

        public UserReminderService getService() {
            return UserReminderService.this;
        }
    }
}
