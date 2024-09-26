package com.example.dyna;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {

    DataCollector dc;
    String deviceName;
    static List<TimestampedWeight> timestampedWeights = new ArrayList<>();
    LineChart lineChart;
    int timeLimit = 60000;

    //Create an ActivityResultLauncher for Bluetooth enable request
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

    ArrayList<Entry> entries = new ArrayList<>();
    int counter = 0;
    Consumer<TimestampedWeight> callback = tsw -> {
        long now = System.currentTimeMillis();

        timestampedWeights.add(tsw);
        entries.add(new Entry(counter++, timestampedWeights.get(timestampedWeights.size() - 1).weight)
        );
        while (now - timestampedWeights.get(0).timestamp > timeLimit) {
            timestampedWeights.remove(0);
            entries.remove(0);
        }

        displayChart();
    };

    public void displayChart(){
        LineData lineData;
        LineDataSet lineDataSet;

//        for (int i = 0; i < timestampedWeights.size(); i++) {
//            entries.add(new Entry(i, timestampedWeights.get(i).weight));
//        }
        lineDataSet = new LineDataSet(entries,null);
        lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
        runOnUiThread(() -> lineChart.invalidate());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startButton = findViewById(R.id.startButton);
        Button stopButton = findViewById(R.id.stopButton);

        // Initialize Bluetooth
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        dc = new DataCollector(bluetoothManager, callback);

        // Button to start scanning
        startButton.setOnClickListener(view -> {
            if (bluetoothManager.getAdapter() == null || !bluetoothManager.getAdapter().isEnabled()) {
                // Launch the Bluetooth enable request
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                bluetoothActivityResultLauncher.launch(enableBtIntent);
            } else {
                Log.d("start","scan starting");
                deviceName = "IF_B7";
                timestampedWeights = new ArrayList<>();
                dc.startScan(deviceName);
            }
        });

        // Button to stop scanning
        stopButton.setOnClickListener(view -> stopScanning());

        lineChart = findViewById(R.id.lineChart);
    }

    private void stopScanning() {
        dc.stopScanning();
    }
}

