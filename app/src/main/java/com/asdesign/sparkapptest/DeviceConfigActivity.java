package com.asdesign.sparkapptest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.utils.Async;
import io.particle.android.sdk.utils.Toaster;

public class DeviceConfigActivity extends AppCompatActivity {

    // Get the passed in device name we chose
    String nameDevice;

    ParticleCloud localSparkCloud = ParticleCloudSDK.getCloud();
    ParticleDevice localParticleDevice = null;

    private final static String PARTICLE_DEVICE_TAG = "SPARK_TEST_LOG_TAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(PARTICLE_DEVICE_TAG, "onCreate()");

        Intent passedInIntent = getIntent();
        nameDevice = passedInIntent.getExtras().getString("ParticleDevice");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_config);

        // Retrieve the particle device for the passed in name
        initDevice(findViewById(R.id.deviceConfigView));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_logged_in, menu);
        return true;
    }

    @Override
    protected void onResume() {
        Log.i(PARTICLE_DEVICE_TAG, "onResume()");

        super.onResume();
        setContentView(R.layout.activity_device_config);

        // Display the properties is handled once the response from the init devices is completed
    }

    /**
     * @brief This function retrieves the list of connected devices from the cloud
     * @param myView specifies the view from which this is being called
     */
    private void initDevice(View myView){

        // List out all connected devices
        Async.executeAsync(localSparkCloud, new Async.ApiWork<ParticleCloud, List<ParticleDevice>>() {

            public List<ParticleDevice> callApi(ParticleCloud particleCloud)
                    throws ParticleCloudException, IOException {
                return particleCloud.getDevices();
            }

            @Override
            public void onSuccess(List<ParticleDevice> devices) {
                for (ParticleDevice device : devices) {
                    if (device.getName().equals(nameDevice)) {
                        localParticleDevice = device;
                        break;
                    }
                }

                listDeviceProps();
            }

            @Override
            public void onFailure(ParticleCloudException e) {
                //Log.e("SOME_TAG", e);
                Log.i(PARTICLE_DEVICE_TAG, "Call onFailure()");
                Toaster.l(DeviceConfigActivity.this, "Wrong credentials or no internet connectivity, please try again");
                listDeviceProps();
            }
        });
    }

    private void listDeviceProps(){
        // Get the list view object
        ListView listView = (ListView)findViewById(R.id.deviceConfigListView);

        // Copy the names of all devices to an array
        List<String> localParticleDeviceString = new ArrayList<String>();
        localParticleDeviceString.clear();

        if( null != localParticleDevice) {
            localParticleDeviceString.add(localParticleDevice.getName().toString());
            localParticleDeviceString.add(localParticleDevice.getID().toString());
            localParticleDeviceString.add(localParticleDevice.getVersion().toString());
        }
        else
        {
            localParticleDeviceString.add("Warning - Device not found");
        }
        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data
        ArrayAdapter<String> localSparkDevicesAdapter = new ArrayAdapter<String>(DeviceConfigActivity.this,
                android.R.layout.simple_list_item_1, android.R.id.text1, localParticleDeviceString);

        listView.setAdapter(localSparkDevicesAdapter);
    }
}
