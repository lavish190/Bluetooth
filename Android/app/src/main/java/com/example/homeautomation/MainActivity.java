package com.example.homeautomation;

import androidx.appcompat.app.AppCompatActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;

    BluetoothAdapter bluetooth;
    ListView listView;
    TextView textView;
    GridView grid;
    RelativeLayout relativeLayout;
    String device_name;
    static ActivityHandler mHandler; // Our main handler that will receive callback notifications
    private ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
    private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier
    public final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    public final static int CONNECTING_STATUS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new ActivityHandler();
        bluetooth = BluetoothAdapter.getDefaultAdapter();
        if(bluetooth==null) {
            Toast.makeText(this,"Device incompatible",Toast.LENGTH_SHORT).show();
        }

        ArrayList<String> list = new ArrayList<>();
        listView = findViewById(R.id.listView);
        textView = findViewById(R.id.textView);
        relativeLayout = findViewById(R.id.relativeLayout);


        if(!bluetooth.isEnabled()) {
            Toast.makeText(this,"Turning On Bluetooth",Toast.LENGTH_SHORT).show();
            Intent in = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(in, REQUEST_ENABLE_BT);
        }
        else
        {
            Set<BluetoothDevice> devices = bluetooth.getBondedDevices();
            for(BluetoothDevice device:devices) {
                list.add(device.getName()+"\n"+device.getAddress());
            }
        }
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                listView.setVisibility(View.GONE);

                device_name = (String) listView.getItemAtPosition(position);
                final String address = device_name.substring(device_name.length() - 17);
                final String name = device_name.substring(0,device_name.length() - 17);

                textView.setText("Connecting to.. "+ name);
                textView.setVisibility(View.VISIBLE);

                new Thread() {
                    public void run() {

                        boolean fail = false;
                        BluetoothDevice device = bluetooth.getRemoteDevice(address);

                        try {
                            mBTSocket = createBluetoothSocket(device);
                        } catch (IOException e) {
                            fail = true;
                            Toast.makeText(getApplicationContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                        }
                        // Establish the Bluetooth socket connection.
                        try {
                            mBTSocket.connect();
                        } catch (IOException e) {
                            try {
                                fail = true;
                                mBTSocket.close();
                                mHandler.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget();
                                //Toast.makeText(getApplicationContext(), "Connection failed", Toast.LENGTH_SHORT).show();

                                mHandler.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget();
                            } catch (IOException e2) {
                                //insert code to deal with this
                                //Toast.makeText(getApplicationContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (!fail) {
                            //textView.setText("Getting Devices...");
                            System.out.println(mBTSocket.isConnected());
                            mConnectedThread = new ConnectedThread(mBTSocket);
                            mConnectedThread.start();
                            mConnectedThread.write("read_device");
                            mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name).sendToTarget();
//                            relativeLayout.setVisibility(View.GONE);
                        }
                    }
                }.start();
            }
        });
        System.out.println("test");
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connection with BT device using UUID
    }


   public void remote(final ArrayList<Devices> device_list) {
        CustomGrid adapter = new CustomGrid(MainActivity.this, device_list);
        //grid=(GridView)findViewById(R.id.grid);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "You Clicked at " +grid.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}