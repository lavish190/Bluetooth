package com.example.homeautomation;

import android.bluetooth.BluetoothSocket;
import android.os.SystemClock;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

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

                    String data = new String(buffer, StandardCharsets.UTF_8);

                    int i = 0;
                    while (data.charAt(i) != '\n') i++;

                    data = data.substring(0, i);


                    ArrayList<Devices> device_list = new ArrayList<>(); // no of devices supported in arduino

                    i = 0;
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
                    // remote(device_list);
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
