package com.example.dyna;

import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.function.Consumer;

public abstract class BaseLiveDataView extends AppCompatActivity {

    Session session;
    LineChart lineChart;
    long timeLimit = 30000;
    DataCollector dc;

    Consumer<TimestampedWeight> callback = tsw -> {
        session.addWeight(tsw);
            updateStats();
            displayChart();
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Bluetooth
        BluetoothManager bluetoothMgr = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);

        //TODO: will pass device name later. Right now we'll hardcode IF_B7
        //        Intent intent = getIntent();
        //        final String deviceName = intent.getStringExtra("deviceName");
        Intent intent = getIntent();
        session = (Session)intent.getSerializableExtra("session");
        if(session == null){
            session = new Session();
        }
        dc = new DataCollector(bluetoothMgr, callback);

    }

    public void setLineLimits(){
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMaximum(session.plotMax + 10);

        LimitLine llPlotMin = new LimitLine(session.plotMin, "Min"); // 10f is the Y value, "Limit" is the label
        LimitLine llPlotMax = new LimitLine(session.plotMax, "Max"); // 10f is the Y value, "Limit" is the label

        llPlotMin.setLineWidth(2f); // Set line width
        llPlotMin.setLineColor(Color.GREEN); // Set line color
        llPlotMin.enableDashedLine(10f, 10f, 0f); // Optional: dashed line

        llPlotMax.setLineWidth(2f); // Set line width
        llPlotMax.setLineColor(Color.RED); // Set line color
        llPlotMax.enableDashedLine(10f, 10f, 0f); // Optional: dashed line

        leftAxis.addLimitLine(llPlotMin);
        leftAxis.addLimitLine(llPlotMax);
    }
    public abstract void updateStats();

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
        lineDataSet.setCircleRadius(2f);
        lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
        runOnUiThread(() -> lineChart.invalidate());
    }
    public void displayHistoricalChart(){
        LineData lineData;
        LineDataSet lineDataSet;
        long startTime = session.weights.get(0).timestamp;
        for(TimestampedWeight weight : session.weights){
            entries.add(new Entry((float) (weight.timestamp - startTime) / 1000, (float)weight.weight/100));
        }
        lineDataSet = new LineDataSet(entries,null);
        lineDataSet.setCircleRadius(2f);
        lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
        runOnUiThread(() -> lineChart.invalidate());
    }
}
