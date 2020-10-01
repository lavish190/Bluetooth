package com.example.homeautomation;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Pattern;

import static com.example.homeautomation.MainActivity.MESSAGE_READ;
import static com.example.homeautomation.MainActivity.grid;
import static com.example.homeautomation.MainActivity.mConnectedThread;
import static com.example.homeautomation.MainActivity.relativeLayout;

public class ActivityHandler extends Handler {
    private static final String TAG = "ActivityHandler";
    ImageView imageGrid;
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

            data = data.substring(0, i-1);
            Log.d(TAG, "handleMessage:  " + data + "  length: " +data.length());

            if(Pattern.matches("(\\d:[a-zA-Z]:\\d,)*$",data)) getDevices(data);
        }
    }

    private void getDevices(String data) {
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

        CustomGrid gridAdapter = new CustomGrid(context, device_list);
        grid.setAdapter(gridAdapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                imageGrid = (ImageView) view.findViewById(R.id.grid_image);
                Devices dev = device_list.get(position);
                if(dev.status==1) {
                    dev.status=0;
                    view.setBackgroundResource(R.drawable.round);
                } else {
                    dev.status=1;
                    view.setBackgroundResource(R.drawable.round_orange);
                }
                String  control = dev.dev_no + ":" + dev.status;
                System.out.println(control);
                mConnectedThread.write(control);
                if(dev.name=="Tubelight") if(dev.status==1) imageGrid.setImageResource(R.drawable.tubelight_on); else imageGrid.setImageResource(R.drawable.tubelight_off);
                if(dev.name=="Fan") if(dev.status==1) imageGrid.setImageResource(R.drawable.fan_on); else imageGrid.setImageResource(R.drawable.fan_off);
                if(dev.name=="Socket") if(dev.status==1) imageGrid.setImageResource(R.drawable.socket_on); else imageGrid.setImageResource(R.drawable.socket_off);
                if(dev.name=="Lamp") if(dev.status==1) imageGrid.setImageResource(R.drawable.lamp_on); else imageGrid.setImageResource(R.drawable.lamp_off);
                if(dev.name=="CFL") if(dev.status==1) imageGrid.setImageResource(R.drawable.cfl_on); else imageGrid.setImageResource(R.drawable.cfl_off);
                if(dev.name=="Ceiling light") if(dev.status==1) imageGrid.setImageResource(R.drawable.ceiling_light_on); else imageGrid.setImageResource(R.drawable.ceiling_light_off);
                if(dev.name=="Bulb") if(dev.status==1) imageGrid.setImageResource(R.drawable.bulb_on); else imageGrid.setImageResource(R.drawable.bulb_off);
            }
        });

        Log.d(TAG, "getDevices: setting remote now");
        relativeLayout.setVisibility(View.GONE);
        Log.d(TAG, "getDevices: Now grid should be visible");
    }
}