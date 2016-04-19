package edu.asu.remindmenow.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * Created by Jithin Roy on 3/23/16.
 */
public class BluetoothReceiver {

    private Context mContext;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothReceiverInterface mInterface;

    public BluetoothReceiver(Context ctx, BluetoothReceiverInterface interf) {
        mContext = ctx;
        mInterface = interf;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void startDiscovery () {

        if (!isDiscovering()) {
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            mContext.registerReceiver(mReceiver, filter);
            filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            mContext.registerReceiver(mReceiver, filter);

            mBluetoothAdapter.startDiscovery();
        }

    }

    public void stopDiscovery () {
        if (isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
    }


    public boolean isDiscovering() {
        return  mBluetoothAdapter.isDiscovering();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String derp = device.getName();
                if (derp != null) {
                    Log.v("Bluetooth", derp);
                    if (derp.startsWith("RM_")) {
                        Log.v("Bluetooth", "Entered the Found " + derp);
                        mInterface.didFoundDevice(derp);

                    }
                }


            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.v("Bluetooth", "Entered the Finished ");
                mInterface.didFinishDiscovery();

            }
        }
    };

    public interface BluetoothReceiverInterface {

        void didFoundDevice(String deviceName);

        void didFinishDiscovery();

    }
}

