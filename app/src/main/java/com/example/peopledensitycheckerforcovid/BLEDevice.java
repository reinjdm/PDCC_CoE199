package com.example.peopledensitycheckerforcovid;

import android.bluetooth.BluetoothDevice;

public class BLEDevice {

    private BluetoothDevice bluetoothDevice;
    private int rssi;

    public BLEDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public String getAdd() {
        return bluetoothDevice.getAddress();
    }
    //public String getIP(){return bluetoothDevice.getIP();}
    public String getName() {
        return bluetoothDevice.getName();
    }
    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
    public int getRssi() {
        return rssi;
    }
}
