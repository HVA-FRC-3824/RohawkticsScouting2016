package com.team3824.akmessing1.scoutingapp.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.bluetooth.BluetoothSync;

import java.util.Set;

public class SyncActivity extends AppCompatActivity {
    private static String TAG = "SyncActivity";
    private BluetoothAdapter mBluetoothAdapter = null;
    Object[] pairedDevices;
    private TextView textView;
    private BluetoothSync bluetoothSync;
    private SyncHandler handler;

    private class SyncHandler extends android.os.Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            String message = new String((byte[])msg.obj);
            Log.d(TAG,"Received: "+message);
            textView.setText(message);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        SharedPreferences sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE);
        String eventID = sharedPreferences.getString("event_id", "");

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        ArrayAdapter<String> pairedDevicesArrayAdapter =
                new ArrayAdapter<String>(this, R.layout.list_item_device_name);

        // Find and set up the ListView for paired devices
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(pairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevicesSet = mBluetoothAdapter.getBondedDevices();
        pairedDevices = pairedDevicesSet.toArray();
        if (pairedDevices.length > 0) {
            for (int i = 0; i < pairedDevices.length; i++){
                pairedDevicesArrayAdapter.add(((BluetoothDevice)pairedDevices[i]).getName() + "\n" + ((BluetoothDevice)pairedDevices[i]).getAddress());
            }
        }
        handler = new SyncHandler();
        bluetoothSync = new BluetoothSync(handler, false);

        textView = (TextView)findViewById(R.id.sync_log);

        Button sync_send = (Button)findViewById(R.id.sync_send);
        sync_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "Hello World";
                Log.d(TAG, "Sending");
                if(bluetoothSync.getState() == BluetoothSync.STATE_CONNECTED) {
                    Log.d(TAG,"Connected");
                    bluetoothSync.write(text.getBytes());
                }
            }
        });
        bluetoothSync.start();
    }

    /**
     * The on-click listener for all devices in the ListViews
     */
    private AdapterView.OnItemClickListener mDeviceClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int position, long arg3) {
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            v.setBackgroundColor(Color.BLUE);
            ((TextView) v).setTextColor(Color.WHITE);
            bluetoothSync.connect(((BluetoothDevice)pairedDevices[position]), false);
        }
    };


}
