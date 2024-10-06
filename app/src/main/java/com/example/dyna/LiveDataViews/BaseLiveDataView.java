package com.example.dyna.LiveDataViews;

import static android.content.Context.BLUETOOTH_SERVICE;

import android.app.Activity;
import android.bluetooth.BluetoothManager;
import android.graphics.Color;
import android.os.Bundle;

import com.example.dyna.R;
import com.example.dyna.Utils.DataCollector;
import com.example.dyna.Utils.FileManager;
import com.example.dyna.Session;
import com.example.dyna.TimestampedWeight;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.function.Consumer;

public abstract class BaseLiveDataView extends Fragment {

    Session session;
    LineChart lineChart;
    long timeLimit = 30000;
    boolean isHistorical = false;
    DataCollector dc;
    View view;

    Consumer<TimestampedWeight> callback = tsw -> {
        session.addWeight(tsw);
            updateStats();
            displayChart();
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Activity activity = getActivity();
        // Initialize Bluetooth
        assert activity != null;
        BluetoothManager bluetoothMgr = (BluetoothManager) activity.getSystemService(BLUETOOTH_SERVICE);

        //TODO: will pass device name later. Right now we'll hardcode IF_B7
        //      Intent intent = getIntent();
        //      final String deviceName = getArguments().getString("device_name", "IF_B7");

        assert getArguments() != null;
        session = (Session)getArguments().get("session");
        isHistorical= getArguments().getBoolean("historical", false);

        dc = new DataCollector(bluetoothMgr, callback);
        return null;

    }

    public void setLineLimits(){
        //TODO: LIGHT/DARK MODE
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMaximum(session.getPlotMax() + 10f);
        leftAxis.setAxisMinimum(0f);
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setAxisMaximum(session.getPlotMax() + 10f);
        rightAxis.setAxisMinimum(0f);

        LimitLine llPlotMin = new LimitLine(session.getPlotMin(), "Min");
        LimitLine llPlotMax = new LimitLine(session.getPlotMax(), "Max");

        llPlotMin.setLineWidth(2f);
        llPlotMin.setLineColor(Color.GREEN); // Set line color
        llPlotMin.enableDashedLine(10f, 10f, 0f);

        llPlotMax.setLineWidth(2f);
        llPlotMax.setLineColor(Color.RED);
        llPlotMax.enableDashedLine(10f, 10f, 0f);

        leftAxis.addLimitLine(llPlotMin);
        leftAxis.addLimitLine(llPlotMax);
    }
    public abstract void updateStats();

    int startIndex = 0;
    ArrayList<Entry> entries = new ArrayList<>();
    public void displayChart(){
        if(isHistorical){
            displayHistoricalChart();
            return;
        }
        LineData lineData;
        LineDataSet lineDataSet;

        long now = System.currentTimeMillis();

        while(now - session.getWeights().get(startIndex).getTimestamp() > timeLimit){
            entries.remove(0);
            startIndex++;
        }

        long startTime = session.getWeights().get(0).getTimestamp();
        TimestampedWeight latest = session.getWeights().get(session.getWeights().size() - 1);
        entries.add(new Entry((float) (latest.getTimestamp() - startTime) / 1000, latest.getWeight()));

        lineDataSet = new LineDataSet(entries,null);
        lineDataSet.setCircleRadius(2f);
        lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    public void displayHistoricalChart(){
        LineData lineData;
        LineDataSet lineDataSet;
        long startTime = session.getWeights().get(0).getTimestamp();
        for(TimestampedWeight weight : session.getWeights()){
            entries.add(new Entry((float) (weight.getTimestamp() - startTime) / 1000, weight.getWeight()));
        }
        lineDataSet = new LineDataSet(entries,null);
        lineDataSet.setCircleRadius(2f);
        lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    void showSaveSessionDialog() {
        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Enter session name");
        input.setPadding(10,0,0,0);
        // Build the dialog
        new AlertDialog.Builder(requireContext())
                .setTitle("Save Session")
                .setMessage("Please enter a name for the session")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    // Get the user input
                    String fileName = input.getText().toString().replace('/','_').trim();

                    if (!fileName.isEmpty()) {
                        // Handle the file saving process here
                        FileManager fm = new FileManager(requireContext());
                        session.setName(fileName);
                        fm.saveSession(session);
                        requireActivity().findViewById(getSaveButtonId()).setEnabled(false);
                    } else {
                        Toast.makeText(requireContext(), "Session name cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    abstract int getSaveButtonId();
    private void saveFileWithName(String fileName) {
        // Implement your file saving logic here
        FileManager fm = new FileManager(requireContext());
        fm.saveSession(session);
        Toast.makeText(requireContext(), "File '" + fileName + "' saved", Toast.LENGTH_SHORT).show();
    }

    public void initializeStartStopSaveExportButtons(Button btnStart, Button btnStop, Button btnSave, Button btnExport) {
        if(!isHistorical) {
            btnStart.setOnClickListener(v -> {
                btnExport.setEnabled(false);
                btnSave.setEnabled(false);
                btnStop.setEnabled(true);
                v.setEnabled(false);
                dc.startCollecting();
            });
            btnStop.setOnClickListener(v -> {
                Log.d("stop", "scan stopped");
                btnExport.setEnabled(true);
                btnSave.setEnabled(true);
                v.setEnabled(false);
                dc.stopCollecting();
            });

            btnSave.setOnClickListener(v -> {
                Log.d("Save", "Saving");
                showSaveSessionDialog();
                if(session.getName() != null){
                    v.setEnabled(false);
                }
            });

        }else {
            //Hides start/stop/save when in historical view
            //TODO: Reposition export button?
            btnStart.setVisibility(View.INVISIBLE);
            btnStop.setVisibility(View.INVISIBLE);
            btnSave.setVisibility(View.INVISIBLE);
        }
        btnExport.setOnClickListener(v -> {
            FileManager fm = new FileManager(requireContext());
            fm.exportSessionToCSV(session);
        });
    }

}
