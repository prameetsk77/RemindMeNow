package edu.asu.remindmenow.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;

import java.nio.charset.Charset;
import java.util.UUID;

import edu.asu.remindmenow.R;

/**
 * Created by Jithin Roy on 3/23/16.
 */
public class BluetoothAdvertiser {

////    private BluetoothLeAdvertiser advertiser;
//    private AdvertiseSettings settings;
//    private Context context;
//
//    public BluetoothAdvertiser(Context ctxt) {
//
//        context = ctxt;
//        settings = new AdvertiseSettings.Builder()
//                .setAdvertiseMode( AdvertiseSettings.ADVERTISE_MODE_BALANCED)
//                .setTxPowerLevel( AdvertiseSettings.ADVERTISE_TX_POWER_LOW)
//                .setConnectable(false)
//                .build();
//
//        if( !BluetoothAdapter.getDefaultAdapter().isMultipleAdvertisementSupported() ) {
//            Log.i("BLE", "Not supported");
//
//        }
//    }
//
//    public void startAdvertising(String userId) {
//
//        String adStr = "RemindMe-" + userId;
//        ParcelUuid pUuid = new ParcelUuid( UUID.fromString(context.getString(R.string.ble_uuid)) );
//        AdvertiseData data = new AdvertiseData.Builder()
//                .setIncludeDeviceName( true)
//                .addServiceUuid( pUuid )
//                .addServiceData( pUuid, adStr.getBytes( Charset.forName("UTF-8") ) )
//                .build();
//        BluetoothLeAdvertiser advertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();
//        Log.i("BLE", ""+advertiser);
//        if (advertiser != null) {
//            Log.i("BLE", "Advertiser ");
//        }
//        advertiser.startAdvertising( settings, data, advertisingCallback );
//    }
//
//    AdvertiseCallback advertisingCallback = new AdvertiseCallback() {
//        @Override
//        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
//            super.onStartSuccess(settingsInEffect);
//            Log.i("BLE", "Advertising onStartSuccess");
//        }
//
//        @Override
//        public void onStartFailure(int errorCode) {
//            Log.e("BLE", "Advertising onStartFailure: " + errorCode);
//            super.onStartFailure(errorCode);
//        }
//    };
//

}
