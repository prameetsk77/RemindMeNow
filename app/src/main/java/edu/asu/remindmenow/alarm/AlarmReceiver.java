package edu.asu.remindmenow.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Jithin Roy on 4/26/16.
 */
public class AlarmReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 12345;
    private static String TAG = "AlarmReceiver";

    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Alarm Service running");
        Intent i = new Intent(context, AlarmIntentService.class);
        context.startService(i);
    }
}
