package com.example.dyna;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.textview.MaterialTextView;

public class LiveDataView extends BaseLiveDataView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_data);
        initializeButtons();
        lineChart = findViewById(R.id.lineChartLiveData);
        timeLimit = 30000;
    }

    @Override
    public void updateStats(){
        ((MaterialTextView)findViewById(R.id.txtPeakMax)).setText(String.valueOf(session.sessionMax));
        ((MaterialTextView)findViewById(R.id.txtAvg)).setText(String.valueOf(session.currentAvg));
        ((MaterialTextView)findViewById(R.id.txtCurrent)).setText(String.valueOf(session.getLatest()));
    }

    public void initializeButtons() {
        findViewById(R.id.btnLdvStart).setOnClickListener(view -> {
            dc.startCollecting();
        });
        findViewById(R.id.btnLdvStop).setOnClickListener(view -> {
            Log.d("stop", "scan stopped");
            dc.stopCollecting();
        });
    }
}