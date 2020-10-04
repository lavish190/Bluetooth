package com.example.homeautomation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomGrid extends BaseAdapter{
    private final ArrayList<Devices> devices;
    private final Context mContext;
    private RecyclerView.ViewHolder holder;

    public CustomGrid(Context mContext, ArrayList<Devices> devices) {
        this.mContext = mContext;
        this.devices = devices;

        for (Devices device : devices) device.printDevices();
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int position) {
        return devices.get(position);
    }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            View grid = inflater.inflate(R.layout.grid_single, null);

            ImageView imageGrid = (ImageView) grid.findViewById(R.id.grid_image);
            TextView textGrid = (TextView) grid.findViewById(R.id.grid_text);

            if(devices.get(position).name=="Tubelight") imageGrid.setImageResource(R.drawable.tubelight_off);
            if(devices.get(position).name=="Fan") imageGrid.setImageResource(R.drawable.fan_off);
            if(devices.get(position).name=="Socket") imageGrid.setImageResource(R.drawable.socket_off);
            if(devices.get(position).name=="Lamp") imageGrid.setImageResource(R.drawable.lamp_off);
            if(devices.get(position).name=="CFL") imageGrid.setImageResource(R.drawable.cfl_off);
            if(devices.get(position).name=="Ceiling light") imageGrid.setImageResource(R.drawable.ceiling_light_off);
            if(devices.get(position).name=="Bulb") imageGrid.setImageResource(R.drawable.bulb_off);

            textGrid.setText(devices.get(position).name);

            if(devices.get(position).status==0) {
                grid.setBackgroundResource(R.drawable.round);
                textGrid.setTextColor(mContext.getResources().getColor(R.color.orange));
                imageGrid.setColorFilter(mContext.getResources().getColor(R.color.orange));
            } else {
                grid.setBackgroundResource(R.drawable.round_orange);
                textGrid.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                imageGrid.setColorFilter(mContext.getResources().getColor(R.color.colorPrimary));
            }
            return grid;
        }
        else return convertView;
    }
}