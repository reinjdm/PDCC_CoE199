package com.example.peopledensitycheckerforcovid;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;

import androidx.core.app.ActivityCompat;

public class BLEWifiScanner {
    private BLEWiFi bleWiFiMainActivity;

    private BluetoothAdapter bluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;

    private long scanPeriod;
    private int signalStrength;



    public BLEWifiScanner(BLEWiFi mainActivity, long scanPeriod, int signalStrength) {
        bleWiFiMainActivity = mainActivity;
        mHandler = new Handler();

        this.scanPeriod = scanPeriod;
        this.signalStrength = signalStrength;

        final BluetoothManager bluetoothManager = (BluetoothManager) bleWiFiMainActivity.getSystemService(Context.BLUETOOTH_SERVICE);

        bluetoothAdapter = bluetoothManager.getAdapter();
    }

    public boolean isScanning() {
        return mScanning;
    }

    public void start() {
        if (!com.example.peopledensitycheckerforcovid.Utility.checkBluetooth(bluetoothAdapter)) {
            com.example.peopledensitycheckerforcovid.Utility.requestBluetooth(bleWiFiMainActivity);
            bleWiFiMainActivity.stopBScan();

        } else {
            scanDevice(true);
        }
    }

    public void stop() {
        scanDevice(false);

    }

    private void scanDevice(final boolean enable) {
        if (enable && !mScanning) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    com.example.peopledensitycheckerforcovid.Utility.toast(bleWiFiMainActivity, "Scanning stopped.");
                    mScanning = false;
                    ActivityCompat.requestPermissions(bleWiFiMainActivity, new String[]{Manifest.permission.BLUETOOTH_SCAN}, 1001);
                    if (ActivityCompat.checkSelfPermission(bleWiFiMainActivity, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(bleWiFiMainActivity, new String[]{Manifest.permission.BLUETOOTH_SCAN},1001);
                        return;
                    }

                    bluetoothAdapter.stopLeScan(mLeScanCallback);
                    bleWiFiMainActivity.stopBScan();


                }
            }, scanPeriod);
            mScanning = true;
            bluetoothAdapter.startLeScan(mLeScanCallback);

        }else{
            mScanning = false;
            bluetoothAdapter.stopLeScan(mLeScanCallback);


        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice bluetoothDevice, int rssi, byte[] bytes) {
            final int new_rssi = rssi;
            if(rssi> signalStrength){
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        bleWiFiMainActivity.addDevice(bluetoothDevice, new_rssi);
                    }
                });
            }
        }
    };
}
