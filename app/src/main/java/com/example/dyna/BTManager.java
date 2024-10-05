package com.example.dyna;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

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

    private final ActivityResultLauncher<Intent> bluetoothActivityResultLauncher =
        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // Handle the Bluetooth enable request result
                if (result.getResultCode() != RESULT_OK) {
                    // Bluetooth was not enabled
                    // Prompt to enable bluetooth
                    // ("Bluetooth not enabled");
                }
            });
    public void activate() {
        if (bluetoothMgr.getAdapter() == null || !bluetoothMgr.getAdapter().isEnabled()) {
            // Launch the Bluetooth enable request
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            bluetoothActivityResultLauncher.launch(enableBtIntent);
        }
    }
    //TODO REMOVE MISSING PERMISSION TAGS
    @SuppressLint("MissingPermission")
    public void startBLEScan(ScanCallback scanCallback) {
        ScanSettings settings;
        List<ScanFilter> filters;
        settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build(); //Low latency required or 70% of packets drop
        filters = new ArrayList<>(Collections.singletonList(new ScanFilter.Builder().setDeviceName("IF_B7").build())); //IF_B7 is for WH-C06

        bleScanner = bluetoothMgr.getAdapter().getBluetoothLeScanner();

        bleScanner.startScan(filters, settings, scanCallback);
    }
    //TODO REMOVE MISSING PERMISSION TAGS
    @SuppressLint("MissingPermission")
    public void stopBLEScan(ScanCallback scanCallback){
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
        bleScanner.stopScan(scanCallback);
    }

    public void startReadingBTConnData(){

    }
}
