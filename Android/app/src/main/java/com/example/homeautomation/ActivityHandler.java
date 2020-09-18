package com.example.homeautomation;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import java.nio.charset.StandardCharsets;

import static com.example.homeautomation.MainActivity.CONNECTING_STATUS;
import static com.example.homeautomation.MainActivity.MESSAGE_READ;


public class ActivityHandler extends Handler {
    private static final String TAG = "ActivityHandler";
    @Override
    public void handleMessage(@NonNull Message msg) {
        if(msg.what == MESSAGE_READ){
            String data = null;
            data = new String((byte[]) msg.obj, StandardCharsets.UTF_8);

            int i = 0;
            while (data.charAt(i) != '\n') i++;

            data = data.substring(0, i);
            Log.e(TAG, "handleMessage: " + data);
        }

        if(msg.what == CONNECTING_STATUS){
            if(msg.arg1 == 1)
                Log.e(TAG, "handleMessage: Connected to Device: " + (String)(msg.obj));
            else
                Log.e(TAG, "handleMessage: Connection Failed" );
        }
    }
}