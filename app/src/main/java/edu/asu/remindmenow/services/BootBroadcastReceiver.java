package edu.asu.remindmenow.services;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Called when the phone is booted. Use this broadcast Receiver to
 * start the service.
 *
 * Created by Jithin Roy on 3/24/16.
 */
public class BootBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // Check if user is logged in.
        // If yes start the bluetooth service.


//        // Launch the specified service when this message is received
//        Intent startServiceIntent = new Intent(context, MyTestService.class);
//        startWakefulService(context, startServiceIntent);
    }
}
