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

    public void notify(String reminderType, String notificationTitle, String notificationMessage, Context ctx){
        int imageName=0;
        switch (reminderType){
            case "U": imageName=R.drawable.user_icon_1;
                        break;
            case "L": imageName=R.drawable.map_icon_1;
                       break;
            case "Z": imageName=R.drawable.home_icon_1;
                      break;

        }


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
                        .setSmallIcon(imageName)
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
