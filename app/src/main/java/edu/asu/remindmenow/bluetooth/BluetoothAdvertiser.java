package edu.asu.remindmenow.bluetooth;

import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.ParcelUuid;

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

    public BluetoothAdvertiser(Context ctxt) {

        context = ctxt;
        settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode( AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setTxPowerLevel( AdvertiseSettings.ADVERTISE_TX_POWER_LOW)
                .setConnectable(false)
                .build();
    }

    public void startAdvertising(String userId) {

        String adStr = "RemindMe-" + userId;
        ParcelUuid pUuid = new ParcelUuid( UUID.fromString(context.getString(R.string.ble_uuid)) );
        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName( true )
                .addServiceUuid( pUuid )
                .addServiceData( pUuid, adStr.getBytes( Charset.forName("UTF-8") ) )
                .build();
    }
}
