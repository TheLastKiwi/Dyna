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

import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

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

    private void showFileNameDialog() {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Enter session name");

        // Build the dialog
        new AlertDialog.Builder(this)
                .setTitle("Save Session")
                .setMessage("Please enter a name for the session")
                .setView(input)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get the user input
                        String fileName = input.getText().toString().trim();

                        if (!fileName.isEmpty()) {
                            // Handle the file saving process here
                            saveFileWithName(fileName);
                        } else {
                            Toast.makeText(getApplicationContext(), "Session name cannot be empty", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveFileWithName(String fileName) {
        // Implement your file saving logic here
        FileManager fm = new FileManager(this);
        fm.saveSession(session);
        Toast.makeText(this, "File '" + fileName + "' saved", Toast.LENGTH_SHORT).show();
    }
}
