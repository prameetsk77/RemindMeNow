package edu.asu.remindmenow.Geofence;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import edu.asu.remindmenow.activities.GeofenceReminderActivity;

/**
 * Created by Prameet Singh on 4/17/2016.
 */

public class GeofenceIntentService implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, ResultCallback<Status> {

    ArrayList<Geofence> mGeofenceList;
    PendingIntent mGeofencePendingIntent = null;

    String TAG = "GeoFence Service";

    private GoogleApiClient mGoogleApiClient;

    GeofenceReminderActivity caller ;

    public void addGeofence(LatLng coordinates, long endTime ,GoogleApiClient mGoogleApiClient, GeofenceReminderActivity activity) {

        caller = activity ;
        this.mGoogleApiClient = mGoogleApiClient;
        mGeofenceList = new ArrayList<Geofence>();

        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(String.valueOf(coordinates.hashCode()))
                .setCircularRegion(
                        coordinates.latitude,
                        coordinates.longitude,
                        100                                         // defines the geofence radius
                )
                .setExpirationDuration(endTime)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());

        // TO DO: Figure it out

        //mGoogleApiClient.connect();

        if (!mGoogleApiClient.isConnected()) {
            Log.e(TAG, "Google API Not Connected");
            Toast.makeText(caller, "Geofence Client not Connected", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }

    private void logSecurityException(SecurityException securityException) {
        Log.e(TAG, "Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(caller.getApplicationContext(), GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        return PendingIntent.getService(caller.getApplicationContext(), 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Connected to GoogleApiClient");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
    }

    @Override
    public void onResult(Status status) {
        if (status.isSuccess()) {
            Log.e(TAG, "Result Added");
            Toast.makeText(
                    caller.getApplicationContext(),
                    "Geofence Added",
                    Toast.LENGTH_SHORT
            ).show();

        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(caller.getApplicationContext(),
                    status.getStatusCode());
            Log.e(TAG, errorMessage);
            //Log.e(TAG, "Error at On Result");
        }
    }
}