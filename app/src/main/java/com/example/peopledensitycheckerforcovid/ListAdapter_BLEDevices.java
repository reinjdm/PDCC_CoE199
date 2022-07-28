package com.example.peopledensitycheckerforcovid;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ListAdapter_BLEDevices extends ArrayAdapter<BLEDevice> {
    Activity activity;
    int layoutResourceID;
    ArrayList<BLEDevice> devicelist;

    public ListAdapter_BLEDevices(Activity activity, int resource, ArrayList<BLEDevice> objects){
        super(activity.getApplicationContext(),resource,objects);
        this.activity=activity;
        layoutResourceID = resource;
        devicelist = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutResourceID,parent,false);
        }

        BLEDevice bleDevice = devicelist.get(position);
        String name = bleDevice.getName();
        String address = bleDevice.getAdd();
        int rssi = bleDevice.getRssi();

        TextView tvname = (TextView) convertView.findViewById(R.id.tvname);
        if(name != null && name.length() >0 ){
            tvname.setText(bleDevice.getName());
        }else{
            tvname.setText("No Name");
        }
        TextView tvrssi = (TextView) convertView.findViewById(R.id.tvrssi);
        tvrssi.setText("RSSI: " + Integer.toString(rssi));

        TextView tvmacaddr = (TextView) convertView.findViewById(R.id.tvmacaddr);
        if(address!=null && address.length()>0){
            tvmacaddr.setText(bleDevice.getAdd());
        }else{
            tvmacaddr.setText("No Address");
        }

        TextView tvrisk = (TextView) convertView.findViewById(R.id.tvrisk);
        if(rssi >= -69){
            tvrisk.setText("HIGH RISK");
        }
        else if (rssi<-69 && rssi>=-74){
            tvrisk.setText("MEDIUM RISK");
        }
        else if (rssi<-74){
            tvrisk.setText("LOW RISK");
        }

        return convertView;
    }

}
