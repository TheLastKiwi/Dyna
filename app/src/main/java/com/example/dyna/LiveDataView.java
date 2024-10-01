package com.example.dyna;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

        lineChart = findViewById(R.id.lineChartLiveData);
        dc = new DataCollector(bluetoothMgr, callback);

        findViewById(R.id.btnLdvStart).setOnClickListener(view -> {
            String deviceName = "IF_B7";
            dc.startScan(deviceName);
        });
        findViewById(R.id.btnLdvStop).setOnClickListener(view -> {
            Log.d("stop", "scan stopped");
            dc.stopScanning();

        });

        Intent intent = getIntent();
        session = (Session) intent.getSerializableExtra("session");
        if(session == null) session = new Session();
        //If session plot target = true
        //add lines on graph to show target zones

    }


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
