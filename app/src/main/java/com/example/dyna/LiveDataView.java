package com.example.dyna;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.function.Consumer;

public class LiveDataView extends AppCompatActivity {

    Session session;
    LineChart lineChart;
    int timeLimit = 30000;
    DataCollector dc;
    Button ldvStartButton;
    Button ldvStopButton;

    Consumer<TimestampedWeight> callback = tsw -> {
        session.addWeight(tsw);
        updateStats();
        displayChart();
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_data);
        // Initialize Bluetooth
        BluetoothManager bluetoothMgr = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        ldvStartButton = findViewById(R.id.btnLdvStart);
        ldvStopButton = findViewById(R.id.btnLdvStop);
        lineChart = findViewById(R.id.lineChart);
        dc = new DataCollector(bluetoothMgr, callback);

        //Get intent here to set session settings parameters
        session = new Session();


        ldvStartButton.setOnClickListener(view -> {
            if (bluetoothMgr.getAdapter() == null || !bluetoothMgr.getAdapter().isEnabled()) {
                // Launch the Bluetooth enable request
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                bluetoothActivityResultLauncher.launch(enableBtIntent);
            } else {
                Log.d("start", "scan starting");
                String deviceName = "IF_B7";
                dc.startScan(deviceName);
            }
        });
        ldvStopButton.setOnClickListener(view -> {
            Log.d("stop", "scan stopped");
            dc.stopScanning();

        });
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
    public void updateStats(){
        TextView maxWeight = findViewById(R.id.txtMax);
        TextView avgWeight = findViewById(R.id.txtAvg);
        maxWeight.setText(String.valueOf(session.currentMax));
        avgWeight.setText(String.valueOf(session.currentAvg));

    }
    int startIndex = 0;
    ArrayList<Entry> entries = new ArrayList<>();
    public void displayChart(){
        LineData lineData;
        LineDataSet lineDataSet;

        long now = System.currentTimeMillis();

        while(now - session.weights.get(startIndex).timestamp > timeLimit){
            entries.remove(0);
            startIndex++;
        }

        long startTime = session.weights.get(0).timestamp;
        TimestampedWeight latest = session.weights.get(session.weights.size() - 1);
        entries.add(new Entry((float) (latest.timestamp - startTime) / 1000, (float)latest.weight/100));

        lineDataSet = new LineDataSet(entries,null);
        lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
        runOnUiThread(() -> lineChart.invalidate());
    }




}
