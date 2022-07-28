package com.example.peopledensitycheckerforcovid;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Scanning extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanning_options);
    }

    public void chooseBLE(View view){

        Intent intent= new Intent(this, BLE.class);
        startActivity(intent);
    }
    public void chooseWifi(View view){
        Intent intent= new Intent(this, WiFi.class);
        startActivity(intent);

    }
    public void chooseHybrid(View view){
        Intent intent= new Intent(this, BLEWiFi.class);
        startActivity(intent);
    }


}