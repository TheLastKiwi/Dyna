package com.example.dyna;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textview.MaterialTextView;

public class LiveDataView extends BaseLiveDataView {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        view = inflater.inflate(R.layout.live_data,container, false);
        initializeButtons();
        lineChart = view.findViewById(R.id.lineChartLiveData);
        timeLimit = 30000;

        return view;
    }

    @Override
    public void updateStats(){
        ((MaterialTextView)view.findViewById(R.id.txtPeakMax)).setText(String.valueOf(session.sessionMax));
        ((MaterialTextView)view.findViewById(R.id.txtAvg)).setText(String.valueOf(session.currentAvg));
        ((MaterialTextView)view.findViewById(R.id.txtCurrent)).setText(String.valueOf(session.getLatest()));
    }

    public void initializeButtons() {
        view.findViewById(R.id.btnLdvStart).setOnClickListener(view -> {
            dc.startCollecting();
        });
        view.findViewById(R.id.btnLdvStop).setOnClickListener(view -> {
            Log.d("stop", "scan stopped");
            dc.stopCollecting();
        });
    }
}