package edu.asu.remindmenow.nearby;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.os.Bundle;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.PublishCallback;
import com.google.android.gms.nearby.messages.PublishOptions;
import com.google.android.gms.nearby.messages.SubscribeCallback;
import com.google.android.gms.nearby.messages.SubscribeOptions;

/**
 * Created by Jithin Roy on 3/26/16.
 */
public class NearbyAPIManager implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{


    private GoogleApiClient mGoogleApiClient;
    private static String TAG = "NearByAPIManager";
    private Context mContext;
    private boolean mResolvingError = false;
    private Message mDeviceInfoMessage;
    private String mUserId;


    public NearbyAPIManager (Context ctx, String userId) {

        mContext = ctx;
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
        mUserId = userId;
    }


    public void startAPI() {
        publish();
        subscribe();
    }

    MessageListener mMessageListener = new MessageListener() {
        @Override
        public void onFound(final Message message) {
            final String nearbyMessageString = new String(message.getContent());

            // Do something with the message string.
            Log.i(TAG, "On found" + nearbyMessageString);
        }

        // Called when a message is no longer detectable nearby.
        public void onLost(final Message message) {
            final String nearbyMessageString = new String(message.getContent());
            // Take appropriate action here (update UI, etc.)

            Log.i(TAG, "On lost" + nearbyMessageString);
        }
    };

    // Subscribe to receive messages.
    private void subscribe() {

        Log.i(TAG, "Trying to subscribe.");
        // Cannot proceed without a connected GoogleApiClient.
        // Reconnect and execute the pending task in onConnected().
        if (!mGoogleApiClient.isConnected()) {

            Log.i(TAG, "No client connected");
            if (!mGoogleApiClient.isConnecting()) {
                Log.i(TAG, "is not connecting");
                mGoogleApiClient.connect();
            } else {
                Log.i(TAG, "Attempting connect");
            }
        } else {
            SubscribeOptions options = new SubscribeOptions.Builder()
                    .setCallback(new SubscribeCallback() {
                        @Override
                        public void onExpired() {
                            Log.i(TAG, "No longer subscribing.");
                        }
                    }).build();

            Nearby.Messages.subscribe(mGoogleApiClient, mMessageListener, options)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                Log.i(TAG, "Subscribed successfully.");
                            } else {
                                Log.i(TAG, "Could not subscribe.");
                                // Check whether consent was given;
                                // if not, prompt the user for consent.
                               // handleUnsuccessfulNearbyResult(status);
                            }
                        }
                    });
        }
    }



    private void publish() {
        Log.i(TAG, "Trying to publish.");
        // Set a simple message payload.
        String strMsg = "RemindNowMe-" + mUserId;
        mDeviceInfoMessage = new Message(strMsg.getBytes());

        // Cannot proceed without a connected GoogleApiClient.
        // Reconnect and execute the pending task in onConnected().
        if (!mGoogleApiClient.isConnected()) {
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        } else {
            PublishOptions options = new PublishOptions.Builder()
                    .setCallback(new PublishCallback() {
                        @Override
                        public void onExpired() {
                            Log.i(TAG, "No longer publishing.");
                        }
                    }).build();

            Nearby.Messages.publish(mGoogleApiClient, mDeviceInfoMessage, options)
                    .setResultCallback(new ResultCallback<Status>() {


                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                Log.i(TAG, "Published successfully.");
                            } else {
                                Log.i(TAG, "Could not publish.");
                                // Check whether consent was given;
                                // if not, prompt the user for consent.
                               handleUnsuccessfulNearbyResult(status);
                            }
                        }
                    });
        }
    }

    private void handleUnsuccessfulNearbyResult(Status status) {
        Log.i(TAG, "Processing error, status = " + status);
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (status.hasResolution()) {
            try {
                mResolvingError = true;
                status.startResolutionForResult((Activity)mContext,100);
            } catch (IntentSender.SendIntentException e) {
                mResolvingError = false;
                Log.i(TAG, "Failed to resolve error status.", e);
            }
        } else {
            if (status.getStatusCode() == CommonStatusCodes.NETWORK_ERROR) {
                Toast.makeText(mContext.getApplicationContext(),
                        "No connectivity, cannot proceed. Fix in 'Settings' and try again.",
                        Toast.LENGTH_LONG).show();
            } else {
                // To keep things simple, pop a toast for all other error messages.
                Toast.makeText(mContext.getApplicationContext(), "Unsuccessful: " +
                        status.getStatusMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    //          ConnectionCallbacks
    //----------------------------------------------------------------------------------------------

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "On connected");
        startAPI();

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "On connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "On connection failed " + connectionResult.toString());
    }
}
