package com.flying_kiwi.dyna.LiveDataViews;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.flying_kiwi.dyna.R;
import com.flying_kiwi.dyna.Session;
import com.flying_kiwi.dyna.SessionType;
import com.flying_kiwi.dyna.Utils.FileManager;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Objects;

public class LiveDataView extends BaseLiveDataView {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        view = inflater.inflate(R.layout.live_data_fragment,container, false);
        initializeButtons();
        lineChart = view.findViewById(R.id.lineChartLiveData);
        timeLimit = 30000;
        if(isHistorical){
            view.findViewById(R.id.mcvLiveCurrent).setVisibility(View.GONE);
            displayChart();
            updateStats();
        }
        return view;
    }

    @Override
    public void updateStats(){
        ((MaterialTextView)view.findViewById(R.id.txtPeakMax)).setText(session.getSessionMax().toString());
        ((MaterialTextView)view.findViewById(R.id.txtAvg)).setText(String.format("%.2f %s",session.getCurrentAvg(),session.getLatest().isKg()?"kg":"lb"));
        ((MaterialTextView)view.findViewById(R.id.txtCurrent)).setText(session.getLatest().toString());
    }

    public void initializeButtons() {
        Button btnStart = view.findViewById(R.id.btnLdvStart);
        Button btnStop = view.findViewById(R.id.btnLdvStop);
        Button btnSave = view.findViewById(R.id.btnLdvSave);
        Button btnExport = view.findViewById(R.id.btnLdvExport);
        Button btnReset = view.findViewById(R.id.btnLdvReset);
        initializeStartStopSaveExportButtons(btnStart, btnStop, btnSave, btnExport);
        if(!isHistorical) {
            btnStart.setOnClickListener(v -> {
                btnExport.setEnabled(false);
                btnSave.setEnabled(false);
                btnStop.setEnabled(true);
                btnReset.setEnabled(true);
                v.setEnabled(false);
                dc.startCollecting();
            });
            btnStop.setOnClickListener(v -> {
                Log.d("stop", "scan stopped");
                view.findViewById(R.id.btnLdvExport).setEnabled(true);
                view.findViewById(R.id.btnLdvSave).setEnabled(true);
                view.findViewById(R.id.btnLdvStart).setEnabled(true);
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
            btnReset.setOnClickListener(view -> {
                //dc.stopCollecting();
                lineChartDataPoints = new ArrayList<>();

                session = new Session(SessionType.LIVE_DATA);
                startIndex = 0;
                lineChart.setData(new LineData());
                YAxis leftAxis = lineChart.getAxisLeft();
                leftAxis.setAxisMinimum(-1f);
                YAxis rightAxis = lineChart.getAxisRight();
                rightAxis.setAxisMinimum(-1f);
                lineChart.invalidate();
                if(!dc.isCollectingData()){
                    view.setEnabled(false);
                    btnSave.setEnabled(false);
                    btnExport.setEnabled(false);
                }
            });
            btnSave.setEnabled(false);
            btnReset.setEnabled(false);
            btnExport.setEnabled(false);
            btnStop.setEnabled(false);

        }else {
            //Hides start/stop/save when in historical view
            //TODO: Reposition export button?
            btnStart.setVisibility(View.INVISIBLE);
            btnStop.setVisibility(View.INVISIBLE);
            btnSave.setVisibility(View.INVISIBLE);
            btnReset.setVisibility(View.INVISIBLE);
            btnExport.setEnabled(true);
        }
        btnExport.setOnClickListener(v -> {
            FileManager fm = new FileManager(requireContext());
            fm.exportSessionToCSV(session);
        });



    }

    @Override
    int getSaveButtonId() {
        //TODO: IMPLEMENT if we want to save live data sessions
        return R.id.btnLdvSave;
    }
}