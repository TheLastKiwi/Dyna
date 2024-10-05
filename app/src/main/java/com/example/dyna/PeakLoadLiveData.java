package com.example.dyna;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;

public class PeakLoadLiveData extends BaseLiveDataView {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        view = inflater.inflate(R.layout.peak_load_live_data,container, false);

        lineChart = view.findViewById(R.id.lineChartPeakData);
        assert getArguments() != null;

        if(isHistorical) {
            displayChart();
            updateStats();
        } else {
            session.sessionType=SessionType.PEAK_LOAD;
            timeLimit = 5000;
        }
        //Probably add in base view
        initializeButtons();
        return view;
    }

    @Override
    public void updateStats() {
        ((TextView)view.findViewById(R.id.txtPeakMax)).setText(String.valueOf(session.sessionMax));
    }
    @Override
    public void displayChart(){
        //TODO: If units are KG, we need a different formula, one that maxes out at 300
        // https://mycurvefit.com/
        YAxis leftAxis = lineChart.getAxisLeft();
        float x = session.sessionMax/100f;
        float limit = (float)(4.641323f*Math.pow(x,0.7529087));
        leftAxis.setAxisMaximum(limit);
        leftAxis.setAxisMinimum(0f);
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setAxisMinimum(0f);
        rightAxis.setAxisMaximum(limit);

        LimitLine llPlotMax = new LimitLine(session.sessionMax/100f, "Max");

        llPlotMax.setLineWidth(2f);
        llPlotMax.setLineColor(Color.GREEN);
        llPlotMax.enableDashedLine(10f, 10f, 0f);
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(llPlotMax);
        if(isHistorical) {
            super.displayHistoricalChart();
        } else {
            super.displayChart();
        }

    }
    public void initializeButtons() {

        if(!isHistorical) {
            view.findViewById(R.id.btnPeakStart).setOnClickListener(view -> {
                dc.startCollecting();
            });
            view.findViewById(R.id.btnPeakStop).setOnClickListener(view -> {
                Log.d("stop", "scan stopped");
                dc.stopCollecting();
            });
            //TODO Only enable save button if collection has stopped
            view.findViewById(R.id.btnPeakSave).setOnClickListener(view -> {
                Log.d("Save", "Saving");
                dc.stopCollecting();
                FileManager fm = new FileManager(requireContext());

                session.name = "Peak " + System.currentTimeMillis()/1000;
                fm.saveSession(session);
            });
        }else {
            //TODO
            // hide start, stop, save buttons
            // show export buttons
        }
    }
}