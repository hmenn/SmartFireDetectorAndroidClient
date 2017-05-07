package com.hasanmen.smartfiredetector;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by hmenn on 07.05.2017.
 */

public class BTDevicesActivity extends AppCompatActivity {
    private ListView lv_devices = null;
    private Set<BluetoothDevice> btPairedDevices;
    private Context context = null;
    private static final String TAG_D="BTDevicesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.devices);
        context = getApplicationContext();

        lv_devices = (ListView) findViewById(R.id.lv_devices);

        ArrayList<String> btList = new ArrayList<>();
        btPairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();

        for (BluetoothDevice dev : btPairedDevices)
            btList.add(dev.getName());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.simple_text_view, btList);

        lv_devices.setAdapter(adapter);

        lv_devices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent data = new Intent();
                try {
                Log.d(TAG_D,"Clicked Item ID:"+String.valueOf(position));
                data.putExtra("id",String.valueOf(position));
                setResult(RESULT_OK,data);
                finish();
                } catch (Exception e) {
                    Log.e(TAG_D, "Error at setOnItemClickListener");
                }
            }
        });
    }
}
