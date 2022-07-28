package com.example.peopledensitycheckerforcovid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button scan, cloud;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void chooseScan(View view){
        Intent intent= new Intent(MainActivity.this, Scanning.class);
        startActivity(intent);
    }

    public void chooseCT(View view){
        Intent intent= new Intent(MainActivity.this, conTraceHistory.class);
        startActivity(intent);
    }


}