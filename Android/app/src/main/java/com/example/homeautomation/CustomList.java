package com.example.homeautomation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomList extends RecyclerView.Adapter<CustomList.ViewHolder> {
    private final ArrayList<BTdevice> bluetoothDevices;

    public CustomList(ArrayList<BTdevice> bluetoothDevices) {
        this.bluetoothDevices = bluetoothDevices;

        for(BTdevice bTdevice:bluetoothDevices) bTdevice.printDevices();
    }

    @NonNull
    @Override
    public CustomList.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_single,parent,false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.text.setText(bluetoothDevices.get(position).name);
    }

    @Override
    public int getItemCount() {
        return bluetoothDevices.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
        }
    }
}