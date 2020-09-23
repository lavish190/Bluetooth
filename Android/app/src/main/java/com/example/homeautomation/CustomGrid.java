package com.example.homeautomation;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class CustomGrid extends BaseAdapter{
    private static final String TAG = "CustomGrid";
    private final ArrayList<Devices> devices;
    private final Context mContext;

    public CustomGrid(Context mContext, ArrayList<Devices> devices) {
        this.mContext = mContext;
        this.devices = devices;

        Log.d(TAG, "CustomGrid: ");
        for (Devices device : devices) device.printDevices();
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            View grid = inflater.inflate(R.layout.grid_single, null);

            ImageView imageGrid = (ImageView) grid.findViewById(R.id.grid_image);
            TextView textView = (TextView) grid.findViewById(R.id.grid_text);

            if(devices.get(position).name=="Tubelight") {
                imageGrid.setImageResource(R.drawable.tubelight);
            }
            textView.setText(devices.get(position).name);

            return grid;
        }
        else return convertView;
    }
}