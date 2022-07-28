package com.example.peopledensitycheckerforcovid;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.Gravity;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class Utility extends Activity {


    public static boolean checkBluetooth(BluetoothAdapter bluetoothAdapter) {

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            return false;
        } else {
            return true;
        }
    }

    public static void requestBluetooth(Activity activity) {
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1001);
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1001);
        }
        activity.startActivityForResult(enableIntent, BLE.REQUEST_ENABLE_BT);
    }

    public static void toast(Context context, String string){
        Toast toast = Toast.makeText(context, string, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0,0);
        toast.show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
