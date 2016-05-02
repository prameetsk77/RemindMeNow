package edu.asu.remindmenow.activities;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.login.LoginManager;

import edu.asu.remindmenow.R;
import edu.asu.remindmenow.alarm.AlarmReceiver;
import edu.asu.remindmenow.bluetooth.BluetoothAdvertiser;
import edu.asu.remindmenow.models.Reminder;
import edu.asu.remindmenow.services.NotificationService;
import edu.asu.remindmenow.userManager.UserSession;
import edu.asu.remindmenow.userReminder.UserReminderService;
import edu.asu.remindmenow.util.DBConnection;
import edu.asu.remindmenow.util.DatabaseManager;

public class HomeActivity extends BaseActivity {

    private static String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.i(TAG, "Create home activity");


        Log.i(TAG, "On new intent");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            long remId = extras.getLong("reminderId");
            Log.i(TAG,"Started from notification - " + remId);
                if (remId > 0) {
                    SQLiteDatabase db = DBConnection.getInstance().openWritableDB();
                    DatabaseManager dbManager = new DatabaseManager();
                    Reminder reminder = dbManager.getReminder(db, remId);
                    dbManager.deleteReminder(db,reminder.getId());
                    DBConnection.getInstance().closeDB(db);

                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.cancelAll();
                }

        }

        // Start bluetooth service
        startService(new Intent(this, UserReminderService.class));
        scheduleAlarm();

    }

    @Override
    protected void onNewIntent(Intent intent) {

    }

    public void mapIconClicked(View v){
        Intent intent=new Intent(this, LocationReminderActivity.class);
        startActivity(intent);
    }

    public void userIconClicked(View v){
        Intent intent=new Intent(this, UserReminderActivity.class);
        startActivity(intent);
    }
    public void zoneIconClicked(View v){
        Intent intent=new Intent(this, GeofenceReminderActivity.class);
        startActivity(intent);
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)  {
       int id=item.getItemId();
        if (id==R.id.action_settings){
            Intent intent=new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }else if (id==R.id.action_reminder_history){
            Intent intent=new Intent(this, ReminderListViewLoader.class);
            startActivity(intent);
            return true;
        }else if (id==R.id.action_logOut) {

            LoginManager.getInstance().logOut();
            Intent intent=new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //============================================================================

    // Setup a recurring alarm every half hour
    public void scheduleAlarm() {


        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },12 );
        }

        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, AlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis();
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                AlarmManager.INTERVAL_DAY, pIntent);
    }
}
