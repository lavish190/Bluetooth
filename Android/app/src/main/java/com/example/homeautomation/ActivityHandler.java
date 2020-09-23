package com.example.homeautomation;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import androidx.annotation.NonNull;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static com.example.homeautomation.MainActivity.CONNECTING_STATUS;
import static com.example.homeautomation.MainActivity.MESSAGE_READ;
import static com.example.homeautomation.MainActivity.grid;
import static com.example.homeautomation.MainActivity.mConnectedThread;
import static com.example.homeautomation.MainActivity.relativeLayout;
import static com.example.homeautomation.MainActivity.textView;

public class ActivityHandler extends Handler {
    private static final String TAG = "ActivityHandler";
    private Context context;
    public ActivityHandler(Context context) {
        this.context=context;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        if(msg.what == MESSAGE_READ){
            String data;
            data = new String((byte[]) msg.obj, StandardCharsets.UTF_8);

            int i = 0;
            while (data.charAt(i) != '\n') i++;

            data = data.substring(0, i);
            Log.e(TAG, "handleMessage: " + data);

            if(data.contains(":")) getDevices(data);
        }

        if(msg.what == CONNECTING_STATUS){
            if(msg.arg1 == 1) {
                Log.e(TAG, "handleMessage: Connected to Device: " + (String) (msg.obj));
                textView.setText("Connected to " + (String) (msg.obj));
            }
            else {
                Log.e(TAG, "handleMessage: Connection Failed");
                textView.setText("Connection Failed");
            }
        }
    }

    private void getDevices(String data) {
        textView.setText("Getting Devices...");
        final ArrayList<Devices> device_list = new ArrayList<>(); // no of devices supported in arduino

        int i = 0;
        while (i < data.length()) {
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

            // create ListItem -->change to android code
            Devices device = new Devices(dev_no, device_code);
            device_list.add(device);

            i++; // skip ,
        }

        CustomGrid gridAdapter = new CustomGrid(context, device_list);
        grid.setAdapter(gridAdapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Devices dev = device_list.get(position);
                if(dev.status==1) dev.status=0; else dev.status=1;
                String  control = Integer.toString(dev.dev_no*10 + dev.status);
                System.out.println(control);
                mConnectedThread.write(control);
            }
        });

        Log.d(TAG, "getDevices: setting remote now");
        relativeLayout.setVisibility(View.GONE);
        Log.d(TAG, "getDevices: Now grid should be visible");
    }
}