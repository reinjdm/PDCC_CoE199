package com.example.peopledensitycheckerforcovid;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class BLE extends Activity implements View.OnClickListener {

    public static final int REQUEST_ENABLE_BT = 1;

    private HashMap<String, BLEDevice> mBtDevicesHashMap;
    private ArrayList<BLEDevice> mbleDeviceArrayList;
    private ListAdapter_BLEDevices adapter;

    private BtStateBroadcastReceiver mBtStateUpdateReceiver;
    private BLEScanner bleScanner;

    private Button startScanning;
    private TextView connectionStatus;
    private ScrollView scrollView;


    private int high = 0;
    private int med = 0;
    private int low = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Utility.toast(getApplicationContext(), "BLE not supported.");
            finish();
        }
        mBtStateUpdateReceiver = new BtStateBroadcastReceiver(getApplicationContext());
        bleScanner = new BLEScanner(this, 10000, -100);

        mBtDevicesHashMap = new HashMap<>();
        mbleDeviceArrayList = new ArrayList<>();

        adapter = new ListAdapter_BLEDevices(this, R.layout.bledevicelistitem, mbleDeviceArrayList);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);

        ListView listView = new ListView(this);
        listView.setAdapter(adapter);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.addView(listView);

        startScanning = (Button) findViewById(R.id.startScan);
        startScanning.setOnClickListener(BLE.this);

        connectionStatus = (TextView) findViewById(R.id.connectionStatus);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mBtStateUpdateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopScan();
        crowdDetect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mBtStateUpdateReceiver);
        stopScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Utility.toast(getApplicationContext(), "Bluetooth is now ON");
            } else if (resultCode == RESULT_CANCELED) {
                Utility.toast(getApplicationContext(), "Kindly turn on the Bluetooth");
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.startScan:
                if (!bleScanner.isScanning()) {
                    startScan();
                } else {
                    stopScan();
                }
                break;
            default:
                break;
        }

    }


    public void addDevice(BluetoothDevice bluetoothDevice, int new_rssi) {
        String add = bluetoothDevice.getAddress();
        if (!mBtDevicesHashMap.containsKey(add)) {
            BLEDevice bleDevice = new BLEDevice(bluetoothDevice);
            bleDevice.setRssi(new_rssi);
            mBtDevicesHashMap.put(add, bleDevice);
            mbleDeviceArrayList.add(bleDevice);

        } else {
            Objects.requireNonNull(mBtDevicesHashMap.get(add)).setRssi(new_rssi);
        }
        adapter.notifyDataSetChanged();
    }

    public void startScan() {
        startScanning.setText("Scanning");
        connectionStatus.setText("Scanning for Devices");
        mbleDeviceArrayList.clear();
        mBtDevicesHashMap.clear();
        adapter.notifyDataSetChanged();
        bleScanner.start();
    }

    public void stopScan() {
        startScanning.setText("Start Scan");
        connectionStatus.setText("Click START SCAN button to start scanning");
        bleScanner.stop();
    }


    public void crowdDetect() {
        if (adapter.devicelist.size() == 0) {
            return;
        }
        for (int pos = 0; pos < adapter.devicelist.size(); pos++) {
            BLEDevice bleDevice = adapter.devicelist.get(pos);
            int rssi = bleDevice.getRssi();
            if (rssi >= -69) {
                high++;
            } else if (rssi < -69 && rssi >= -74) {
                med++;
            } else if (rssi < -74) {
                low++;
            }
        }
        if (high > 3) {
            if (med > 10 && low > 10) {
                Toast.makeText(this, "High Risk Area", Toast.LENGTH_SHORT).show();
            } else if (med < 10 & low < 10) {
                Toast.makeText(this, "Med Risk Area", Toast.LENGTH_SHORT).show();
            }
        } else if (high < 3) {
            if (med > 10 && low > 10) {
                Toast.makeText(this, "High Risk Area", Toast.LENGTH_SHORT).show();
            } else if (med < 10 && low > 10) {
                Toast.makeText(this, "Med Risk Area", Toast.LENGTH_SHORT).show();
            } else if (med < 10 && low < 10) {
                Toast.makeText(this, "Low Risk Area", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
