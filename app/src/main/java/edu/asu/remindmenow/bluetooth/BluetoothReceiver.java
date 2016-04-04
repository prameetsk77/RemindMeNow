package edu.asu.remindmenow.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Jithin Roy on 3/23/16.
 */
public class BluetoothReceiver {


    private Context context;

    private BluetoothAdapter mBluetoothAdapter;

    public BluetoothReceiver(Context ctx) {
        context = ctx;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    public void startDiscovery () {
        mBluetoothAdapter.startDiscovery();

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(mReceiver, filter);
    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.v("Bluetooth", "On receive " + action);
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //do something

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a Toast
                String derp = device.getName() + " - " + device.getAddress();

                if (derp.startsWith("RM_")) {
                    Toast.makeText(BluetoothReceiver.this.context, derp, Toast.LENGTH_LONG).show();
                }
                Log.v("Bluetooth", "On r " + derp);

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.v("Bluetooth", "Entered the Finished ");

            }
        }
    };
}
