package edu.asu.remindmenow.alarm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import edu.asu.remindmenow.R;
import edu.asu.remindmenow.activities.SettingsActivity;
import edu.asu.remindmenow.services.NotificationService;
import edu.asu.remindmenow.util.ApplicationConstants;

/**
 * Created by Jithin Roy on 4/26/16.
 */
public class AlarmIntentService extends IntentService implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static String TAG = "AlarmIntentService";

    GoogleApiClient mGoogleApiClient = null;
    private LocationRequest mLocationRequest;

    public AlarmIntentService() {
        super("AlarmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Alarm Service running");

        SharedPreferences settings = getSharedPreferences(SettingsActivity.PREFS_NAME, 0);
        boolean isWeatherInfoOn = settings.getBoolean("weatherInfoOn",true);

        if (isWeatherInfoOn) {
            startLocation();

        }

    }

    //=======================================

    public void startLocation() {

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();


        }
        mGoogleApiClient.connect();
        createLocationRequest();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            Log.i(TAG, "On connection");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Could not connect to location services. Please enable location in settings to get weather updates.",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Could not connect to location services. Please enable location in settings to get weather updates.",
                Toast.LENGTH_SHORT).show();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "On location changed " + location.getLatitude() + " " + location.getLongitude());
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        fetchWeather(location);
    }



    //=======================================

    private void fetchWeather(Location loc) {
        //http://api.openweathermap.org/data/2.5/forecast/city?id=524901&APPID={APIKEY}
        String weatherURL = "http://api.openweathermap.org/data/2.5/weather?lat="+loc.getLatitude()+"&lon="+loc.getLongitude()+"&APPID=5bcc7382eb31d43a2c7f10e1739c5e04";
        Log.i(TAG, "Weather url = " + weatherURL);
        new RetrieveWeatherInfo().execute(weatherURL);
    }

    private void showNotificationBasedOnWeather(String weather, String description) {

        Log.i(TAG, "Response - " + weather);
        new NotificationService().notify("W", "Weather",  weather + ": " + description , this);
    }

    class RetrieveWeatherInfo extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String... urls) {
            try {
                URL url= new URL(urls[0]);
                BufferedReader reader = null;
                StringBuilder stringBuilder;

                try {
                    // create the HttpURLConnection

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(15 * 1000);
                    connection.connect();

                    int status = connection.getResponseCode();

                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    stringBuilder = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line + "\n");
                    }

                    Log.i(TAG, " " +stringBuilder.toString());
                    String response = stringBuilder.toString();
                    if (response != null) {
                        JSONObject jsonReader = new JSONObject(response);
                        JSONArray summaryJson = jsonReader.getJSONArray("weather");
                        JSONObject mainWeather = summaryJson.getJSONObject(0);
                        String weatherInfo = mainWeather.getString("main");
                        String description = mainWeather.getString("description");
                        showNotificationBasedOnWeather(weatherInfo, description);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    //throw e;
                } finally {

                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }
                    }
                }
                return null;

            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }

        protected void onPostExecute(String weatherInfo) {

        }
    }


}
