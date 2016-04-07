package edu.asu.remindmenow.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import edu.asu.remindmenow.R;
import edu.asu.remindmenow.activities.HomeActivity;
import edu.asu.remindmenow.activities.MainActivity;

/**
 * Created by priyama on 4/5/2016.
 */
public class NotificationService {

    public void notify(String notificationTitle, String notificationMessage, Context ctx){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.drawable.home_icon_1)
                        .setContentTitle(notificationTitle)
                        .setContentText(notificationMessage)
                        .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });;

        Intent resultIntent = new Intent(ctx, HomeActivity.class);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
        stackBuilder.addParentStack(HomeActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(123, mBuilder.build());
    }
}
