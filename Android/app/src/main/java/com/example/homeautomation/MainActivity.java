package com.example.homeautomation;

import androidx.appcompat.app.AppCompatActivity;
import  android.bluetooth.BluetoothAdapter;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    BluetoothAdapter bluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetooth = BluetoothAdapter.getDefaultAdapter();

    }
}
