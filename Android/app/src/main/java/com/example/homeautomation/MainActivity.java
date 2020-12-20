package com.example.homeautomation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    BluetoothAdapter bluetooth;
    ImageButton change_room;
    TextView textView;
    ImageView imageGrid,loading;
    TextView textGrid;
    GridView grid;
    ProgressBar progressBar;
    RelativeLayout relativeLayout,load;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter rAdapter;
    AnimationDrawable animationDrawable;

    ArrayList<BTdevice> bluetoothDevices = new ArrayList<>();

    public static ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
    private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path

    public static final int DURATION = 200;
    public static final int y_TRANS = 700;
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier
    private boolean show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetooth = BluetoothAdapter.getDefaultAdapter();
        if(bluetooth==null) {
            Toast.makeText(this,"Device incompatible",Toast.LENGTH_SHORT).show();
        }
        setTitle("Select Your Room");
        grid = findViewById(R.id.grid);
        change_room = findViewById(R.id.change_room);
        textView = findViewById(R.id.textView);
        relativeLayout = findViewById(R.id.relativeLayout);
        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_horizontal);
        load = findViewById(R.id.load);
        loading = findViewById(R.id.loading);
        animationDrawable = (AnimationDrawable)loading.getDrawable();
        animationDrawable.start();

        progressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.orange),android.graphics.PorterDuff.Mode.SRC_IN);
        recyclerView.hasFixedSize();
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if(!bluetooth.isEnabled()) {
            startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(stateReciever,BTIntent);
        } else {
            bluetoothDevices.add(new BTdevice("", ""));
            bluetoothDevices.add(new BTdevice("", ""));
            Set<BluetoothDevice> devices = bluetooth.getBondedDevices();
            for (BluetoothDevice device : devices)
                bluetoothDevices.add(new BTdevice(device.getName(), device.getAddress()));
            bluetoothDevices.add(new BTdevice("", ""));
            bluetoothDevices.add(new BTdevice("", ""));

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(stateReciever,BTIntent);
        }

        bluetooth.startDiscovery();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        rAdapter = new CustomList(bluetoothDevices);
        recyclerView.setAdapter(rAdapter);

        final SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        recyclerView.setVisibility(View.VISIBLE);
        change_room.setVisibility(View.GONE);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull final RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                View view = snapHelper.findSnapView(layoutManager);
                int pos = layoutManager.getPosition(view);

                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(pos);
                final RelativeLayout list_item = viewHolder.itemView.findViewById(R.id.list_item);
                TextView name_text = viewHolder.itemView.findViewById(R.id.text);
                TextView address_text = viewHolder.itemView.findViewById(R.id.address);
                ImageButton connect = viewHolder.itemView.findViewById(R.id.connect);

                final String name = (String) name_text.getText();
                final String address = (String) address_text.getText();

                if(newState == RecyclerView.SCROLL_STATE_IDLE) {
                    connect.setVisibility(View.VISIBLE);
                    list_item.setBackgroundColor(getResources().getColor(R.color.light_yellow));
                    name_text.setTextColor(getResources().getColor(R.color.colorPrimary));
                    name_text.animate().setDuration(350).scaleX(1.2f).scaleY(1.2f).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                    connect.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            recyclerView.animate().setDuration(DURATION).translationY(y_TRANS).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                            show=false;

                            textView.setText("Connecting to.. "+ name);
                            textView.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.VISIBLE);
                            progressBar.setProgress(10,true);


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
                                                    textView.setText("Connection Failed. Please try again");
                                                    progressBar.setVisibility(View.GONE);
                                                    recyclerView.animate().setDuration(DURATION).translationY(0).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                                                }
                                            });
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
                                        progressBar.setProgress(60,true);
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
                }else {
                    connect.setVisibility(View.GONE);
                    list_item.setBackgroundColor(getResources().getColor(R.color.no_color));
                    name_text.setTextColor(Color.parseColor("#000000"));
                    name_text.animate().setDuration(350).scaleX(1).scaleY(1).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                }
            }
        });

        change_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!show) {
                    show=true;
                    Log.d(TAG, "onClick: True");
                    recyclerView.animate().setDuration(DURATION).translationY(0).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                } else {
                    show=false;
                    Log.d(TAG, "onClick: False");
                    recyclerView.animate().setDuration(DURATION).translationY(y_TRANS).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.help) {
            Intent intent = new Intent(MainActivity.this,HelpActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        // creates secure outgoing connection with BT device using UUID
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
                   // if(!mmSocket.isConnected()) return;
                    // Read from the InputStream
                    bytes = mmInStream.available();
                    if (bytes != 0) {
                        SystemClock.sleep(100); //pause and wait for rest of data. Adjust this depending on your sending speed.
                        bytes = mmInStream.available(); // how many bytes are ready to be read?
                        bytes = mmInStream.read(buffer, 0, bytes); // record how many bytes we actually read

                        System.out.println("reading....");
                        String data = new String((byte[]) buffer, StandardCharsets.UTF_8);


                        int i = 0;
                        while (data.charAt(i) != '\n') i++;

                        data = data.substring(0, i-1);
                        if(Pattern.matches("(\\d:[a-zA-Z]:\\d,)*$",data)) getDevices(data);
                        else if(data.contains("Acknowledgement")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    load.setVisibility(View.GONE);
                                }
                            });
                            Log.d(TAG, "run: " + data);
                        }
                        //mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
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
        private void getDevices(String data) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText("Setting up Remote");
                }
            });
            final ArrayList<Devices> device_list = new ArrayList<>(); // no of devices supported in arduino

            int i = 0;
            while (i < data.length()-1) {
                int dev_no = 0;
                while (i < data.length() && Character.isDigit(data.charAt(i))) {
                    dev_no = dev_no * 10 + Character.getNumericValue(data.charAt(i));
                    i++;
                }

                i++; // skip :

                String device_code = "";
                while (i < data.length() && Character.isLetter(data.charAt(i))) {
                    device_code += data.charAt(i);
                    i++;
                }

                i++; // skip :

                int status = 0;
                while (i < data.length() && Character.isDigit(data.charAt(i))) {
                    status = status * 10 + Character.getNumericValue(data.charAt(i));
                    i++;
                }

                // create ListItem -->change to android code
                Devices device = new Devices(dev_no, device_code,status);
                device_list.add(device);

                i++; // skip ,
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setProgress(100,true);
                    CustomGrid gridAdapter = new CustomGrid(MainActivity.this, device_list);
                    grid.setAdapter(gridAdapter);
                    grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            load.setVisibility(View.VISIBLE);
                            imageGrid = view.findViewById(R.id.grid_image);
                            textGrid = view.findViewById(R.id.grid_text);
                            Devices dev = device_list.get(position);
                            if(dev.status==1) {
                                dev.status=0;
                                view.setBackgroundResource(R.drawable.round);
                                textGrid.setTextColor(getResources().getColor(R.color.orange));
                                imageGrid.setColorFilter(getResources().getColor(R.color.orange));
                            } else {
                                dev.status=1;
                                view.setBackgroundResource(R.drawable.round_orange);
                                textGrid.setTextColor(getResources().getColor(R.color.colorPrimary));
                                imageGrid.setColorFilter(getResources().getColor(R.color.colorPrimary));
                            }
                            String  control = dev.dev_no + ":" + dev.status;
                            System.out.println(control);
                            write(control);
//                            if(dev.name=="Tubelight") if(dev.status==1) imageGrid.setImageResource(R.drawable.tubelight_on); else imageGrid.setImageResource(R.drawable.tubelight_off);
//                            if(dev.name=="Fan") if(dev.status==1) imageGrid.setImageResource(R.drawable.fan_on); else imageGrid.setImageResource(R.drawable.fan_off);
//                            if(dev.name=="Socket") if(dev.status==1) imageGrid.setImageResource(R.drawable.socket_on); else imageGrid.setImageResource(R.drawable.socket_off);
//                            if(dev.name=="Lamp") if(dev.status==1) imageGrid.setImageResource(R.drawable.lamp_on); else imageGrid.setImageResource(R.drawable.lamp_off);
//                            if(dev.name=="CFL") if(dev.status==1) imageGrid.setImageResource(R.drawable.cfl_on); else imageGrid.setImageResource(R.drawable.cfl_off);
//                            if(dev.name=="Ceiling light") if(dev.status==1) imageGrid.setImageResource(R.drawable.ceiling_light_on); else imageGrid.setImageResource(R.drawable.ceiling_light_off);
//                            if(dev.name=="Bulb") if(dev.status==1) imageGrid.setImageResource(R.drawable.bulb_on); else imageGrid.setImageResource(R.drawable.bulb_off);
                        }
                    });
                    Log.d(TAG, "getDevices: setting remote now");
                    relativeLayout.setVisibility(View.GONE);
                    change_room.setVisibility(View.VISIBLE);
                    Log.d(TAG, "getDevices: Now grid should be visible");
                }
            });
        }
    }
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                bluetoothDevices.add(new BTdevice(device.getName(), device.getAddress()));
                Log.d(TAG, "onReceive: "+ device.getName() +":"+ device.getAddress());
                rAdapter = new CustomList(bluetoothDevices);
                recyclerView.setAdapter(rAdapter);
            }
        }
    };
    private final BroadcastReceiver stateReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(bluetooth.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,bluetooth.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "onReceive: turning on");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "onReceive: state on");
                        Set<BluetoothDevice> devices = bluetooth.getBondedDevices();
                        bluetoothDevices.add(new BTdevice("", ""));
                        bluetoothDevices.add(new BTdevice("", ""));
                        for (BluetoothDevice device : devices)
                            bluetoothDevices.add(new BTdevice(device.getName(),device.getAddress()));
                        bluetoothDevices.add(new BTdevice("", ""));
                        bluetoothDevices.add(new BTdevice("", ""));
                        rAdapter = new CustomList(bluetoothDevices);
                        recyclerView.setAdapter(rAdapter);
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "onReceive: Turning off");
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: state off");
                        bluetoothDevices.clear();
                        rAdapter = new CustomList(bluetoothDevices);
                        recyclerView.setAdapter(rAdapter);
                        break;
                }
            }
        }
    };
}