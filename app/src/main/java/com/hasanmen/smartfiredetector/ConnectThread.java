package com.hasanmen.smartfiredetector;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by hmenn on 07.05.2017.
 */

public class ConnectThread extends Thread {

    private static final String TAG ="ConnectThread";
    private final BluetoothSocket btSocket;
    private final BluetoothDevice btDevice;

    private  InputStream inputStream;
    private  OutputStream outputStream;

    private UUID applicationUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    public ConnectThread(BluetoothDevice device) {

        btDevice = device;

        BluetoothSocket tmp = null;
        try {

            tmp = btDevice.createRfcommSocketToServiceRecord(applicationUUID);

            inputStream = tmp.getInputStream();
            outputStream = tmp.getOutputStream();

        } catch (IOException e) {
            Log.e("ERROR", "Bluetooth socket creation error.", e);
        }

        btSocket = tmp;

    }

    public void run() {

        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

        try {

            btSocket.connect();
        } catch (IOException ex) {
            Log.e(TAG,"Bluetooth connection error",ex);
            try {
                btSocket.close();
            }catch (IOException e){
                Log.e(TAG,"Bluetooth socket close error!",e);
            }
            return;
        }
    }

    public void sentData(byte[] bytes){
        try {
            outputStream.write(bytes);

        } catch (IOException e) {
            Log.e(TAG, "Error occurred when sending data", e);

        }

    }
}
