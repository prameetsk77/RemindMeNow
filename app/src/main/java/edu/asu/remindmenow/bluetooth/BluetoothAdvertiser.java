package edu.asu.remindmenow.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.util.UUID;

import edu.asu.remindmenow.R;

/**
 * Created by Jithin Roy on 3/23/16.
 */
public class BluetoothAdvertiser {

    private BluetoothLeAdvertiser advertiser;
    private AdvertiseSettings settings;
    private Context context;

    private BluetoothAdapter mBluetoothAdapter;

    public BluetoothAdvertiser(Context ctxt) {

        context = ctxt;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    public void startAdvertising(String userId) {
        mBluetoothAdapter.setName(userId);
        if (mBluetoothAdapter.isEnabled() == false) {
            mBluetoothAdapter.enable();
        }

    }

}
