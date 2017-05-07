package com.hasanmen.smartfiredetector;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Context context = null;

    private BluetoothAdapter bluetoothAdapter = null;
    private BluetoothDevice connectedDevice = null;
    private Set<BluetoothDevice> btPairedDevices = null;

    private Button btn_listDevices = null;
    private Button btn_connectBLU = null;
    private Button btn_sendInfo = null;
    private ToggleButton btn_btState = null;

    private EditText val=null;

    private static final int LIST_DEVICES_RESULT_CODE = 1;
    private static final String TAG = "MainActivity";

    private ConnectThread bluThread=null;


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
                Log.d(TAG,"Connect button clicked");
                if(connectedDevice==null){
                    Toast.makeText(context,"Please select a bluetooth device!",Toast.LENGTH_SHORT).show();
                    return;
                }
                bluThread = new ConnectThread(connectedDevice);
                bluThread.start();
            }
        });

        val = (EditText)findViewById(R.id.et_val);

        btn_sendInfo = (Button) findViewById(R.id.btn_sendInfo);
        btn_sendInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluThread.sentData(val.getText().toString().getBytes());
            }
        });


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
                tv.setText("Device MAC:"+connectedDevice.getAddress());
                Log.d(TAG,"Device Name:"+connectedDevice.getName());
            } else connectedDevice = null;
        }
    }
}
