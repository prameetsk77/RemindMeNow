package edu.asu.remindmenow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import edu.asu.remindmenow.nearby.NearbyAPIManager;
import edu.asu.remindmenow.userManager.UserSession;


public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        NearbyAPIManager manager = new NearbyAPIManager(this, UserSession.getInstance().getLoggedInUser().getId());
        manager.startAPI();
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
