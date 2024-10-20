package com.flying_kiwi.dyna.Utils;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.util.Log;

import com.flying_kiwi.dyna.TimestampedWeight;

import java.util.Arrays;
import java.util.function.Consumer;

public class DataCollector {
    //List devices
    //select device
    //If direct BT, handshake and connect
    //If advertisement, filter by device name

    Consumer<TimestampedWeight> viewCallback;
    BTManager btManager;
    String deviceName;
    public DataCollector(BluetoothManager bluetoothManager, Consumer<TimestampedWeight> viewCallback) {
        btManager = new BTManager(bluetoothManager);
        //LiveData view callback
        this.viewCallback = viewCallback;
        this.deviceName = "IF_B7";
        startScan();
    }

    private boolean collectingData = false;
    private boolean bleConnectionMode = true;
    //ONLY WH-C06 devices named "IF_B7
    public void startScan(){
        if("IF_B7".equals(deviceName)){
            bleConnectionMode = true;
            btManager.startBLEScan(scanCallback);
        } else {
            bleConnectionMode = false;
            //Connect to device, probably earlier than at this point.
            btManager.startReadingBTConnData();
        }
    }

    static int cstu(byte i) {
        return (int) i & 0xFF;
    }

    private final ScanCallback scanCallback = new ScanCallback() {
        byte[] last = new byte[17];
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            byte[] data = result.getScanRecord().getManufacturerSpecificData(256);


            if (result.getScanRecord() != null && collectingData) {
                if(data[9] != last[9]){
                    Log.d("Data Last " + collectingData,Arrays.toString(last));
                    Log.d("Data " + collectingData,Arrays.toString(data));
                }
                last = data;
                //byte 10 * 256 + unsigned byte 11 = weight
                //Byte 9/14 for unit of measurement
                //kg -> 1,1
                //lb -> -1 0
//                Log.d("Data","Accepted data");
                //TODO: Maybe we should divide by 100 here because that's the real data it's reading
                // And store in reading what unit the scale is outputting so we can display that too
                TimestampedWeight reading = new TimestampedWeight((cstu(data[10]) * 256 + cstu(data[11]))/100f,data[14]==1);
                viewCallback.accept(reading);
            } else {
                Log.d("Data","Tossing data");
            }
        }
    };
    //TODO REMOVE MISSING PERMISSION TAGS
    @SuppressLint("MissingPermission")
    public void stopScanning(){
        stopCollecting();
        btManager.stopBLEScan(scanCallback);
    }
    public void stopCollecting(){
        btManager.stopBLEScan(scanCallback);
        collectingData = false;
    }

    public void startCollecting(){
        btManager.startBLEScan(scanCallback);
        collectingData = true;
    }
    public boolean isCollectingData() {
        return collectingData;
    }
}
