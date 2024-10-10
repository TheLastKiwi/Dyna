package com.flying_kiwi.dyna.Utils;

import android.Manifest;
import android.annotation.SuppressLint;

import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.pm.PackageManager;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BTManager extends AppCompatActivity {
    BluetoothManager bluetoothMgr;
    BluetoothLeScanner bleScanner;
    BTManager(BluetoothManager btMgr){
        this.bluetoothMgr = btMgr;
        bleScanner = btMgr.getAdapter().getBluetoothLeScanner();
    }

    @SuppressLint("MissingPermission")
    public void startBLEScan(ScanCallback scanCallback) {
        ScanSettings settings;
        List<ScanFilter> filters;
        settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build(); //Low latency required or 70% of packets drop
        filters = new ArrayList<>(Collections.singletonList(new ScanFilter.Builder().setDeviceName("IF_B7").build())); //IF_B7 is for WH-C06

        bleScanner = bluetoothMgr.getAdapter().getBluetoothLeScanner();
        bleScanner.startScan(filters, settings, scanCallback);
    }
    //The only way to stop is to have started. We can suppress
    @SuppressLint("MissingPermission")
    public void stopBLEScan(ScanCallback scanCallback){
        bleScanner.stopScan(scanCallback);
    }

    public void startReadingBTConnData(){

    }
}
