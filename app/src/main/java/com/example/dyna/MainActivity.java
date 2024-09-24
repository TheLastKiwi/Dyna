package com.example.dyna;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

//    private static final int SCAN_PERIOD = 25;  // 8 times per second
    private BluetoothLeScanner bluetoothLeScanner;
    private Handler handler;
    private TextView bluetoothDataTextView;
    private BluetoothAdapter bluetoothAdapter;

    // Step 1: Create an ActivityResultLauncher for Bluetooth enable request
    private final ActivityResultLauncher<Intent> bluetoothActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        // Handle the Bluetooth enable request result
                        if (result.getResultCode() == RESULT_OK) {
                            // Bluetooth was enabled
                            startScanning();
                        } else {
                            // Bluetooth was not enabled
                            bluetoothDataTextView.setText("Bluetooth not enabled");
                        }
                    });

    ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build(); // Set scan mode to low latency
    List<ScanFilter> filters = new ArrayList<>(Collections.singletonList(new ScanFilter.Builder().setDeviceName("IF_B7").build()));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startButton = findViewById(R.id.startButton);
        Button stopButton = findViewById(R.id.stopButton);
        bluetoothDataTextView = findViewById(R.id.bluetoothDataTextView);

        // Initialize Bluetooth
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        handler = new Handler(Looper.getMainLooper());

        // Button to start scanning
        startButton.setOnClickListener(view -> {
            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                // Launch the Bluetooth enable request
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                bluetoothActivityResultLauncher.launch(enableBtIntent);
            } else {
                startScanning();
            }
        });

        // Button to stop scanning
        stopButton.setOnClickListener(view -> stopScanning());

        lineChart = findViewById(R.id.lineChart);
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void startScanning() {
        // Check for necessary permissions

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH_SCAN},
                    1);

        }

        // Start Bluetooth LE scanning
        bluetoothLeScanner.startScan(filters,settings,scanCallback);
//        handler.postDelayed(this::updateBluetoothData, SCAN_PERIOD);
    }

    private void stopScanning() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        bluetoothLeScanner.stopScan(scanCallback);
        handler.removeCallbacksAndMessages(null);
    }

    static int cstu(byte i){
        return (int)i & 0xFF;
    }

    static List<Long[]> datas = new ArrayList<>();
    static List<byte[]> mfgData = new ArrayList<>();
    LineChart lineChart;
    LineData lineData;
    LineDataSet lineDataSet;
    ArrayList<Entry> entries = new ArrayList<>();

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if(result.getScanRecord() != null) {
                Log.d("Data",result.getScanRecord().toString());
                mfgData.add(result.getScanRecord().getManufacturerSpecificData(256));
                byte[] data = result.getScanRecord().getManufacturerSpecificData(256);
                long now = System.currentTimeMillis();
                datas.add(new Long[]{(long)cstu(data[10])*256+cstu(data[11]),now});
                while(now-datas.get(0)[1] > 30000){
                    mfgData.remove(0);
                    datas.remove(0);
                }
                //byte 10 * 256 + unsigned byte 11
                //Byte 9/14 for unit of measurement
                //kg -> 1,1
                //lb -> -1 0
                entries = new ArrayList<>();
                for(int i = 0; i < datas.size(); i++){
                    entries.add(new Entry(i,datas.get(i)[0]));
                }
                lineDataSet = new LineDataSet(entries,"");
                lineData = new LineData(lineDataSet);
                lineChart.setData(lineData);

            }
            runOnUiThread(() -> lineChart.invalidate());
        }
    };

//    private void updateBluetoothData() {
//        // Repost this runnable every SCAN_PERIOD to keep updating the UI
//        handler.postDelayed(this::updateBluetoothData, SCAN_PERIOD);
//    }
}

