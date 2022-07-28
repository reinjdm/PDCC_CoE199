package com.example.peopledensitycheckerforcovid;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BtStateBroadcastReceiver extends BroadcastReceiver {
    
    Context actContext;
    
    public BtStateBroadcastReceiver(Context actContext){
        this.actContext = actContext;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

            switch (state){
                case BluetoothAdapter.STATE_OFF:
                    Utility.toast(actContext, "Bluetooth is OFF");
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    Utility.toast(actContext, "Bluetooth is turning OFF");
                    break;
                case BluetoothAdapter.STATE_ON:
                    Utility.toast(actContext, "Bluetooth is ON");
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    Utility.toast(actContext, "Bluetooth is turning ON");
                    break;
            }
        }
    }
}

