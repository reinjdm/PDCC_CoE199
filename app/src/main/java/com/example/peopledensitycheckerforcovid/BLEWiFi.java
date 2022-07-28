package com.example.peopledensitycheckerforcovid;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.stealthcopter.networktools.SubnetDevices;
import com.stealthcopter.networktools.subnet.Device;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class BLEWiFi extends AppCompatActivity implements View.OnClickListener {

    private Button blescan, wifiscan;
    private ListView wifilist;
    private ScrollView blescroll;
    private TextView apstat;

    //BLE
    public static final int REQUEST_ENABLE_BT = 1;
    private HashMap<String, BLEDevice> mBtDevicesHashMap;
    private ArrayList<BLEDevice> mbleDeviceArrayList;
    private ListAdapter_BLEDevices badapter;
    private BtStateBroadcastReceiver mBtStateUpdateReceiver;
    private BLEWifiScanner bleScanner;
    private int high = 0;
    private int med = 0;
    private int low = 0;
    private int connectedDevtoAP = 0;

    //WiFi
    private WifiManager wifiManager;
    private List<ScanResult> scanResultList;
    private HashMap<String, SubnetDevice> subnetDevicesHashMap;
    private ArrayList<SubnetDevice> arrayList;
    private ListAdapter_WifiDevice wadapter;
    private final static String[] permissions = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blewi_fi);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Utility.toast(getApplicationContext(), "BLE not supported.");
            finish();
        }

        //initializing WiFi
        apstat = (TextView) findViewById(R.id.AP);
        subnetDevicesHashMap = new HashMap<>();
        arrayList = new ArrayList<>();
        wifiscan = (Button) findViewById(R.id.startWScan);
        wifiscan.setOnClickListener(BLEWiFi.this);
        wifilist = (ListView) findViewById(R.id.wifiList);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(!wifiManager.isWifiEnabled()){
            Toast.makeText(this, "WiFi is OFF", Toast.LENGTH_SHORT).show();
            wifiManager.setWifiEnabled(true);
        }
        wadapter = new ListAdapter_WifiDevice(this,R.layout.wifidevicelayout,arrayList);
        wifilist.setAdapter(wadapter);
        handlePermisions();

        //Initializing BLE
        mBtStateUpdateReceiver = new BtStateBroadcastReceiver(getApplicationContext());
        bleScanner = new BLEWifiScanner(BLEWiFi.this, 10000, -100);
        mBtDevicesHashMap = new HashMap<>();
        mbleDeviceArrayList = new ArrayList<>();
        badapter = new ListAdapter_BLEDevices(this, R.layout.bledevicelistitem, mbleDeviceArrayList);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
        ListView blistView = new ListView(this);
        blistView.setAdapter(badapter);
        blescroll = (ScrollView) findViewById(R.id.scrollView1);
        blescroll.addView(blistView);
        blescan= (Button) findViewById(R.id.startBScan);
        blescan.setOnClickListener(BLEWiFi.this);

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.startBScan:
                if (!bleScanner.isScanning()) {
                    Toast.makeText(getApplicationContext(),"BLE Scan button pressed",Toast.LENGTH_SHORT).show();
                    startBScan();
                } else {
                    stopBScan();
                }
                break;
            case R.id.startWScan:
                scanWiFi();
                findSubnetDevices();
            default:
                break;
        }
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
        stopBScan();
        crowdDetect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mBtStateUpdateReceiver);
        stopBScan();
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
        badapter.notifyDataSetChanged();

    }

    public void startBScan() {
        blescan.setText("Scanning");
        mbleDeviceArrayList.clear();
        mBtDevicesHashMap.clear();
        badapter.notifyDataSetChanged();
        bleScanner.start();
        Toast.makeText(getApplicationContext(),"Starting BLE Scan",Toast.LENGTH_SHORT).show();

    }

    public void stopBScan() {
        blescan.setText("Start BLE Scan");
        bleScanner.stop();
    }

    public void crowdDetect() {
        if (badapter.devicelist.size() == 0) {
            return;
        }
        for (int pos = 0; pos < badapter.devicelist.size(); pos++) {
            BLEDevice bleDevice = badapter.devicelist.get(pos);
            int rssi = bleDevice.getRssi();
            if (rssi >= -69) {
                high++;

            } else if (rssi < -69 && rssi >= -74) {
                //Toast.makeText(this, "med", Toast.LENGTH_SHORT).show();
                med++;
            } else if (rssi < -74) {
                //Toast.makeText(this, "low", Toast.LENGTH_SHORT).show();
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


    private void handlePermisions() {
        if (android.os.Build.VERSION.SDK_INT < 23) {
            return;
        }

        for (int i = 0; i < permissions.length; i++) {
            String p = permissions[i];
            if (checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{permissions[i]},1001);
            }
        }
    }


    private void scanWiFi() {
        wifiscan.setText("Scanning");
        arrayList.clear();
        BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                scanResultList = wifiManager.getScanResults();
                unregisterReceiver(this);

                for (ScanResult scanResult: scanResultList){
                    apstat.setText("Connected to: " + scanResult.SSID + "  with RSSI: "+ Integer.toString(scanResult.level));
                    wadapter.notifyDataSetChanged();
                }
            }
        };
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        Toast.makeText(this, "Scanning for devices", Toast.LENGTH_SHORT).show();
    }

    public void addSubnetDevice(Device device){
        String ip_add = device.hostname;
        if(!subnetDevicesHashMap.containsKey(ip_add)){
            SubnetDevice subnetDevice= new SubnetDevice(device);
            connectedDevtoAP++;
            subnetDevicesHashMap.put(ip_add,subnetDevice);
            arrayList.add(subnetDevice);
        }
        wadapter.notifyDataSetChanged();
    }
    private void findSubnetDevices() {
        SubnetDevices subnetDevices = SubnetDevices.fromLocalAddress().findDevices
                (new SubnetDevices.OnSubnetDeviceFound() {
                    @Override
                    public void onDeviceFound(Device device) {
                        //optional
                    }
                    @Override
                    public void onFinished(ArrayList<Device> devicesFound) {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run() {
                                connectedDevtoAP = 0;
                                for(Device device:devicesFound){
                                    addSubnetDevice(device);
                                    Toast.makeText(getApplicationContext(), device.hostname , Toast.LENGTH_SHORT).show();
                                }
                                Toast.makeText(getApplicationContext(), "Number of detected devices: "+ connectedDevtoAP , Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                });
        wadapter.notifyDataSetChanged();
        //subnetDevices.cancel();
    }


}