package com.example.dyna;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class DataCollector {
    //List devices
    //select device
    //If direct BT, handshake and connect
    //If advertisement, filter by device name

    BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    Consumer<TimestampedWeight> callback;

    public DataCollector(BluetoothManager bluetoothManager, Consumer<TimestampedWeight> callback) {
        this.bluetoothManager = bluetoothManager;
        this.callback = callback;
    }

    boolean collectingData = false;
    private TimestampedWeight weight;
    ScanSettings settings;
    List<ScanFilter> filters;

    //ONLY WH-C06 devices named "IF_B7
    @SuppressLint("MissingPermission")
    public void startBLEScan() {
        collectingData = true;
        settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build(); //Low latency required or 70% of packets drop
        filters = new ArrayList<>(Collections.singletonList(new ScanFilter.Builder().setDeviceName("IF_B7").build())); //IF_B7 is for WH-C06
        bluetoothAdapter = bluetoothManager.getAdapter();

        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        bluetoothLeScanner.startScan(filters, settings, scanCallback);
    }
    public void startReadingBTConnData(){

    }
    public void startScan(String deviceName){
        if("IF_B7".equals(deviceName)){
            startBLEScan();
        } else {
            startReadingBTConnData();
        }
    }
    static int cstu(byte i) {
        return (int) i & 0xFF;
    }

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (result.getScanRecord() != null && collectingData) {
                Log.d("Data", result.getScanRecord().toString());
                byte[] data = result.getScanRecord().getManufacturerSpecificData(256);
                //byte 10 * 256 + unsigned byte 11 = weight
                //Byte 9/14 for unit of measurement
                //kg -> 1,1
                //lb -> -1 0
//                TimestampedWeight reading = new TimestampedWeight(cstu(data[10]) * 256 + cstu(data[11]));
                TimestampedWeight reading = new TimestampedWeight( (((int)data[10] & 0xff) << 8) | data[10 + 1] & 0xff);
                callback.accept(reading);
            }
        }
    };
    public void stopScanning(){
        collectingData = false;
    }
}
