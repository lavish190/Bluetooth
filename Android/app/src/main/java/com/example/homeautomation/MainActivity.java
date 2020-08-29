package com.example.homeautomation;

import androidx.appcompat.app.AppCompatActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;

    String data;

    BluetoothAdapter bluetooth;
    ListView listView;
    TextView textView;
    RelativeLayout relativeLayout;
    String device_name;
    private Handler mHandler; // Our main handler that will receive callback notifications
    private ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
    private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetooth = BluetoothAdapter.getDefaultAdapter();
        if(bluetooth==null) {
            showToast("Device incompatible");
        }

        ArrayList<String> list = new ArrayList<>();
        listView = findViewById(R.id.listView);
        textView = findViewById(R.id.textView);
        relativeLayout = findViewById(R.id.relativeLayout);


        if(!bluetooth.isEnabled()) {
            showToast("Turning On Bluetooth");
            Intent in = new Intent(bluetooth.ACTION_REQUEST_ENABLE);
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

        mHandler = new Handler(){
            public void handleMessage(android.os.Message msg){
                if(msg.what == MESSAGE_READ){
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                textView.setText("Setting Up Remote");
                if(msg.what == CONNECTING_STATUS){
                    String bStatus;
                    if(msg.arg1 == 1)
                        bStatus="Connected to Device: " + (String)(msg.obj);
                    else
                        bStatus="Connection Failed";
                }
            }
        };

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                listView.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
                //relativeLayout.setVisibility(View.GONE);
                device_name = (String) listView.getItemAtPosition(position);
                final String address = device_name.substring(device_name.length() - 17);
                final String name = device_name.substring(0,device_name.length() - 17);

                textView.setText("Connecting to.. "+ name);

                boolean fail = false;

                BluetoothDevice device = bluetooth.getRemoteDevice(address);

                try {
                    mBTSocket = createBluetoothSocket(device);
                } catch (IOException e) {
                    fail = true;
                    Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                }
                // Establish the Bluetooth socket connection.
                try {
                    mBTSocket.connect();
                } catch (IOException e) {
                    try {
                        fail = true;
                        mBTSocket.close();
                        Toast.makeText(getBaseContext(), "Connection failed", Toast.LENGTH_SHORT).show();

                        mHandler.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget();
                    } catch (IOException e2) {
                        //insert code to deal with this
                        Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                    }
                }
                if(!fail) {
                    textView.setText("Getting Devices...");
                   /* mConnectedThread = new ConnectedThread(mBTSocket);
                    mConnectedThread.start();
                    mConnectedThread.write("read_device");*/
                    mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name).sendToTarget();
                }
            }
        });

        System.out.println("2: "+ data + " :2");
    }

    private class ConnectedThread extends Thread {
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

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.available();
                    if(bytes != 0) {
                        SystemClock.sleep(100); //pause and wait for rest of data. Adjust this depending on your sending speed.
                        bytes = mmInStream.available(); // how many bytes are ready to be read?
                        bytes = mmInStream.read(buffer, 0, bytes); // record how many bytes we actually read
                        mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget(); // Send the obtained bytes to the UI activity
                        data = new String(buffer, StandardCharsets.UTF_8);

                        //textView.setText("Setting Up Remote");
                        int i=0;
                        while(data.charAt(i) != '\n') i++;

                        data = data.substring(0, i);

                        ArrayList<ListItem> device_list = new ArrayList<>(); // no of devices supported in arduino

                        i=0;
                        while(i < data.length()){
                            int dev_no = 0;

                            while(i < data.length() && Character.isDigit(data.charAt(i))){
                                dev_no = dev_no*10 + Character.getNumericValue(data.charAt(i));
                                i++;
                            }

                            i++; // skip :

                            String device_code = "";
                            while(i < data.length() && Character.isLetter(data.charAt(i))){
                                device_code += data.charAt(i);
                                i++;
                            }

                            // create ListItem -->change to android code
                            ListItem device = new ListItem(dev_no, device_code);
                            device_list.add(device);

                            i++; // skip ,
                        }



                        break;
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

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connection with BT device using UUID
    }

    private void showToast(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
    class ListItem{
        public int dev_no;
        public String name;
        public int status; // on, off, etc

        ListItem(int p_no, String n){
            this.dev_no = p_no;
            if (n.equals("t")) this.name = "Tubelight";
            else if (n.equals("f")) this.name = "Fan";
            this.status = 0;

            System.out.println(this.dev_no);
            System.out.println(this.name);
            System.out.println(this.status);
        }
    }
}