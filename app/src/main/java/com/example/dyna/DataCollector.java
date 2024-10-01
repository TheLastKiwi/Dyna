package com.example.dyna;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class DataCollector {
    //List devices
    //select device
    //If direct BT, handshake and connect
    //If advertisement, filter by device name

    Consumer<TimestampedWeight> callback;
    BTManager btManager;
    public DataCollector(BluetoothManager bluetoothManager, Consumer<TimestampedWeight> callback) {
        btManager = new BTManager(bluetoothManager);
        //LiveData view callback
        this.callback = callback;
    }

    boolean collectingData = false;
    private TimestampedWeight weight;


    //ONLY WH-C06 devices named "IF_B7


    public void startScan(String deviceName){
        if("IF_B7".equals(deviceName)){
            collectingData = true;
            btManager.startBLEScan(scanCallback);
        } else {
            btManager.startReadingBTConnData();
        }
    }
    static int cstu(byte i) {
        return (int) i & 0xFF;
    }

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (result.getScanRecord() != null && collectingData) {
//                Log.d("Data", result.getScanRecord().toString());
                byte[] data = result.getScanRecord().getManufacturerSpecificData(256);
                //byte 10 * 256 + unsigned byte 11 = weight
                //Byte 9/14 for unit of measurement
                //kg -> 1,1
                //lb -> -1 0
                Log.d("Data",Arrays.toString(data));
//                TimestampedWeight reading = new TimestampedWeight());
                TimestampedWeight reading = new TimestampedWeight(cstu(data[10]) * 256 + cstu(data[11]));
                callback.accept(reading);
            }
        }
    };
    @SuppressLint("MissingPermission")
    public void stopScanning(){
        collectingData = false;
        btManager.stopBLEScan(scanCallback);

    }
}
