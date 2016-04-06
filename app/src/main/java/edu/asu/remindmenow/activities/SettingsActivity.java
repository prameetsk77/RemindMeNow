package edu.asu.remindmenow.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Switch;

import edu.asu.remindmenow.R;

/**
 * Created by priyama on 3/25/2016.
 */
public class SettingsActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "MyReminderPrefsFile";
    Switch soundOn;
    Switch vibOn;
    Switch weatherInfoOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        soundOn = (Switch)findViewById(R.id.switch1);
        vibOn = (Switch)findViewById(R.id.switch2);
        weatherInfoOn = (Switch)findViewById(R.id.switch3);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean isSoundOn = settings.getBoolean("soundOn", true);
        boolean isVibOn = settings.getBoolean("vibOn",true);
        boolean isWeatherInfoOn = settings.getBoolean("weatherInfoOn",true);
        soundOn.setChecked(isSoundOn);
        vibOn.setChecked(isVibOn);
        weatherInfoOn.setChecked(isWeatherInfoOn);
    }


    @Override
    protected void onStop(){
        super.onStop();

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("soundOn", soundOn.isChecked());
        editor.putBoolean("vibOn", vibOn.isChecked());
        editor.putBoolean("weatherInfoOn", weatherInfoOn.isChecked());

        editor.commit();
    }
}
