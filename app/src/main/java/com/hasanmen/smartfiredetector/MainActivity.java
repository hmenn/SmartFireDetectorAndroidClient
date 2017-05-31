package com.hasanmen.smartfiredetector;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Context context = null;

    private BluetoothAdapter bluetoothAdapter = null;
    private BluetoothDevice connectedDevice = null;
    private Set<BluetoothDevice> btPairedDevices = null;

    private Button btn_listDevices = null;
    private Button btn_connectBLU = null;
    private Button btn_sendInfo = null;
    private ToggleButton btn_btState = null;
    private TextView tv_connState = null;

    private EditText val = null;

    private static final int LIST_DEVICES_RESULT_CODE = 1;
    private static final String TAG = "MainActivity";

    private ComThread bluThread = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        // if bluetooth closed, open it
        btn_btState = (ToggleButton) findViewById(R.id.btn_btState);
        if (bluetoothAdapter.isEnabled()) {
            btn_btState.setChecked(true);
        }

        btn_btState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bluetoothAdapter.isEnabled()) {
                    //bluetoothAdapter.enable();
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, 0);
                    Toast.makeText(getApplicationContext(), "BLuetooth enabled.", Toast.LENGTH_SHORT).show();
                } else {
                    bluetoothAdapter.disable();
                    //Intent intent = new Intent(BluetoothAdapter.);
                    Toast.makeText(getApplicationContext(), "Bluetooth disabled.", Toast.LENGTH_SHORT).show();
                    //btn_btState.setEnabled(false);
                }
            }
        });

        btPairedDevices = bluetoothAdapter.getBondedDevices();
        btn_listDevices = (Button) findViewById(R.id.btn_listDevices);
        btn_listDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BTDevicesActivity.class);
                startActivityForResult(intent, LIST_DEVICES_RESULT_CODE);
                // bluetoothAdapter.disable();
            }
        });


        btn_connectBLU = (Button) findViewById(R.id.btn_connectBLU);
        btn_connectBLU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Connect button clicked");
                if (connectedDevice == null) {
                    Toast.makeText(context, "Please select a bluetooth device!", Toast.LENGTH_SHORT).show();
                    return;
                }
                bluThread = new ComThread(connectedDevice);
                bluThread.start();
            }
        });

        val = (EditText) findViewById(R.id.et_val);

        btn_sendInfo = (Button) findViewById(R.id.btn_sendInfo);
        btn_sendInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluThread.sentData(val.getText().toString().getBytes());
            }
        });

        tv_connState = (TextView) findViewById(R.id.tv_connState);


    }


    private class ComThread extends Thread {

        private static final String TAG = "ConnectThread";
        private final BluetoothSocket btSocket;
        private final BluetoothDevice btDevice;

        private InputStream inputStream;
        private OutputStream outputStream;

        private UUID applicationUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        public ComThread(BluetoothDevice device) {
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


        @Override
        public void run() {
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            TextView tv = (TextView) findViewById(R.id.tv_connState);

            try {
                btSocket.connect();
                //tv.setText("Connected");

                listenBT();

            } catch (IOException ex) {
                Log.e(TAG, "Bluetooth connection error", ex);
                try {
                    btSocket.close();
                } catch (IOException e) {
                    Log.e(TAG, "Bluetooth socket close error!", e);
                }
                return;
            }
        }

        public void sentData(byte[] bytes) {
            try {
                outputStream.write(bytes);
                Log.d(TAG,"write1");

            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);
            }
        }

        public void listenBT() {
            TextView tv = (TextView) findViewById(R.id.tvStatus);
            try {
                while (true) {
                        Log.d(TAG,"read");
                        int val = inputStream.read();
                        if (val == '0') {
                            tv.setText("SAFE");
                        } else {
                            tv.setText("ALERT");
                        }
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TextView tv = (TextView) findViewById(R.id.tv_connDevice);
        if (requestCode == LIST_DEVICES_RESULT_CODE && resultCode == RESULT_OK) {
            String id = data.getStringExtra("id");

            int index = Integer.valueOf(id);
            Iterator<BluetoothDevice> iter = btPairedDevices.iterator();
            while (index > 0) {
                if (iter.hasNext()) {
                    iter.next();
                    --index;
                } else break;
            }

            if (iter.hasNext()) {
                connectedDevice = iter.next();
                tv.setText("Device MAC:" + connectedDevice.getAddress());
                Log.d(TAG, "Device Name:" + connectedDevice.getName());
            } else connectedDevice = null;
        }
    }
}
