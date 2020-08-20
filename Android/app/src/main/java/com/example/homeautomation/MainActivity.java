package com.example.homeautomation;

import androidx.appcompat.app.AppCompatActivity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;

    BluetoothAdapter bluetooth;
    TextView textview1;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetooth = BluetoothAdapter.getDefaultAdapter();
        if(bluetooth==null) {
            showToast("Device incompatible");
        }
        textview1 = findViewById(R.id.textView1);
        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!bluetooth.isEnabled()) {
                    showToast("Turning On Bluetooth");
                    Intent in = new Intent(bluetooth.ACTION_REQUEST_ENABLE);
                    startActivityForResult(in, REQUEST_ENABLE_BT);
                }
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(bluetooth.isEnabled()) {
            Intent intent = new Intent(MainActivity.this, PairedActivity.class);
            startActivity(intent);
        }
    }
    private void showToast(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
}