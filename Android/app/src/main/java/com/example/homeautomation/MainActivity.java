package com.example.homeautomation;

import androidx.appcompat.app.AppCompatActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_ENABLE_BT = 1;

    BluetoothAdapter bluetooth;
    ListView listView;
    TextView textView;
    static GridView grid;
    static RelativeLayout relativeLayout;
    String device_name;
    ActivityHandler mHandler; // Our main handler that will receive callback notifications
    public static ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
    private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier
    public final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new ActivityHandler(this);
        bluetooth = BluetoothAdapter.getDefaultAdapter();
        if(bluetooth==null) {
            Toast.makeText(this,"Device incompatible",Toast.LENGTH_SHORT).show();
        }

        setTitle("Select Your Room");
        ArrayList<String> list = new ArrayList<>();
        listView = findViewById(R.id.listView);
        grid = findViewById(R.id.grid);
        textView = findViewById(R.id.textView);
        relativeLayout = findViewById(R.id.relativeLayout);


        if(!bluetooth.isEnabled()) {
            Toast.makeText(this,"Turning On Bluetooth",Toast.LENGTH_SHORT).show();
            Intent in = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(in, REQUEST_ENABLE_BT);
        }
        else {
            Set<BluetoothDevice> devices = bluetooth.getBondedDevices();
            for (BluetoothDevice device : devices) {
                list.add(device.getName() + "\n" + device.getAddress());
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
                            runOnUiThread(new Runnable(){
                                public void run() {
                                    Log.e(TAG, "handleMessage: Socket Creation Failed");
                                    textView.setText("Socket Creation Failed: Your Device might not support Bluetooth");
                                }
                            });
                        }
                        // Establish the Bluetooth socket connection.
                        try {
                            mBTSocket.connect();
                        } catch (IOException e) {
                            try {
                                fail = true;
                                mBTSocket.close();
                                runOnUiThread(new Runnable(){
                                    public void run() {
                                        Log.e(TAG, "handleMessage: Connection Failed");
                                        textView.setText("Connection Failed");
                                    }
                                });
                                Intent intent = new Intent(MainActivity.this,MainActivity.class);
                                startActivity(intent);
                            } catch (IOException e2) {
                                runOnUiThread(new Runnable(){
                                    public void run() {
                                        Log.e(TAG, "handleMessage: Socket Creation Failed");
                                        textView.setText("Socket Creation Failed: Your Device might not support Bluetooth");
                                    }
                                });
                            }
                        }
                        if (!fail) {
                            System.out.println(mBTSocket.isConnected());
                            runOnUiThread(new Runnable(){
                                public void run() {
                                    Log.d(TAG, "handleMessage: Connected to Device: " + name );
                                    textView.setText("Connected to " + name);
                                    setTitle(name);
                                }
                            });
                            mConnectedThread = new ConnectedThread(mBTSocket);
                            mConnectedThread.start();
                            mConnectedThread.write("read_device");
                        }
                    }
                }.start();
            }
        });
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connection with BT device using UUID
    }

    class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        @Override
        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.available();
                    if (bytes != 0) {
                        SystemClock.sleep(100); //pause and wait for rest of data. Adjust this depending on your sending speed.
                        bytes = mmInStream.available(); // how many bytes are ready to be read?
                        bytes = mmInStream.read(buffer, 0, bytes); // record how many bytes we actually read

                        System.out.println("reached till connected thread");

                        mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    }

                } catch (IOException e) {
                    e.printStackTrace();

                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            byte[] bytes = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }
        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
}