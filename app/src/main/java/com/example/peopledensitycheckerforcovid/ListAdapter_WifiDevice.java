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

public class ListAdapter_WifiDevice extends ArrayAdapter<SubnetDevice> {
    Activity activity;
    int layoutResourceID;
    ArrayList<SubnetDevice> subnetList;

    public ListAdapter_WifiDevice(Activity activity, int resource, ArrayList<SubnetDevice> objects){
        super(activity.getApplicationContext(),resource,objects);
        this.activity=activity;
        layoutResourceID = resource;
        subnetList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutResourceID,parent,false);

        }

        SubnetDevice subnetDevice = subnetList.get(position);
        String name = subnetDevice.getHostname();
        String ip_add = subnetDevice.getIPadd();
        String mac_add = subnetDevice.getMAC();


        TextView wname = (TextView) convertView.findViewById(R.id.wname);
        if(name != null && name.length() >0 ){
            wname.setText(subnetDevice.getHostname());
        }else{
            wname.setText("No Name");
        }

        TextView wipaddr = (TextView) convertView.findViewById(R.id.wipadd);
        if(ip_add!=null && ip_add.length()>0){
            wipaddr.setText(subnetDevice.getIPadd());
        }else{
            wipaddr.setText("No Address");
        }
        return convertView;
    }

}


