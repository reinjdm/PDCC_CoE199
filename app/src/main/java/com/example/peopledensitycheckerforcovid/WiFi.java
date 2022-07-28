package com.example.peopledensitycheckerforcovid;

import android.Manifest;
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
import android.widget.TextView;
import android.widget.Toast;

import com.stealthcopter.networktools.SubnetDevices;
import com.stealthcopter.networktools.subnet.Device;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WiFi extends AppCompatActivity {

    private ListView listView;
    private Button scanButton;
    private TextView apstat;

    private WifiManager wifiManager;
    private List<ScanResult> scanResultList;
    private HashMap<String, SubnetDevice> subnetDevicesHashMap;
    private ArrayList<SubnetDevice> arrayList;
    private ListAdapter_WifiDevice adapter;

    private int total_dev=0;

    private final static String[] permissions = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wi_fi);

        scanButton = findViewById(R.id.startScan);
        apstat = (TextView) findViewById(R.id.wifiap);

        subnetDevicesHashMap = new HashMap<>();
        arrayList = new ArrayList<>();
        scanButton = (Button) findViewById(R.id.startScan);
        listView = (ListView) findViewById(R.id.wifiList);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(!wifiManager.isWifiEnabled()){
            Toast.makeText(this, "WiFi is OFF", Toast.LENGTH_SHORT).show();
            wifiManager.setWifiEnabled(true);
        }
        adapter = new ListAdapter_WifiDevice(this,R.layout.wifidevicelayout,arrayList);
        listView.setAdapter(adapter);
        handlePermisions();
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanWiFi();
                findSubnetDevices();
            }
        });
        handlePermisions();
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
        scanButton.setText("Scanning");
        arrayList.clear();
        BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                scanResultList = wifiManager.getScanResults();
                unregisterReceiver(this);

                for (ScanResult scanResult: scanResultList){
                    apstat.setText("Connected to: " + scanResult.SSID + " with RSSI: "+ Integer.toString(scanResult.level));
                    adapter.notifyDataSetChanged();
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
            subnetDevicesHashMap.put(ip_add,subnetDevice);
            arrayList.add(subnetDevice);
        }
        adapter.notifyDataSetChanged();
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
                                total_dev = 0;
                                for(Device device:devicesFound){
                                    addSubnetDevice(device);
                                    total_dev++;
                                }
                                Toast.makeText(getApplicationContext(), "Number of detected devices: "+ total_dev , Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
        adapter.notifyDataSetChanged();
    }
}
