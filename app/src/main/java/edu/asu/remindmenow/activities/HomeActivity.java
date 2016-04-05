package edu.asu.remindmenow.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.login.LoginManager;

import edu.asu.remindmenow.R;
import edu.asu.remindmenow.bluetooth.BluetoothAdvertiser;
import edu.asu.remindmenow.exception.ApplicationBusinessException;
import edu.asu.remindmenow.exception.ApplicationRuntimeException;
import edu.asu.remindmenow.models.User;
import edu.asu.remindmenow.userManager.UserSession;
import edu.asu.remindmenow.util.DBConnection;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BluetoothAdvertiser adv = new BluetoothAdvertiser(this);
        adv.startAdvertising("RM_" + UserSession.getInstance().getLoggedInUser().getId());


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
        }else if (id==R.id.action_logOut) {

            LoginManager.getInstance().logOut();
            Intent intent=new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
