package com.flying_kiwi.dyna.LiveDataViews;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flying_kiwi.dyna.R;
import com.google.android.material.textview.MaterialTextView;

public class LiveDataView extends BaseLiveDataView {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        view = inflater.inflate(R.layout.live_data_fragment,container, false);
        initializeButtons();
        lineChart = view.findViewById(R.id.lineChartLiveData);
        timeLimit = 30000;

        return view;
    }

    @Override
    public void updateStats(){
        ((MaterialTextView)view.findViewById(R.id.txtPeakMax)).setText(String.format("%.2f",session.getSessionMax()));
        ((MaterialTextView)view.findViewById(R.id.txtAvg)).setText(String.format("%.2f",session.getCurrentAvg()));
        ((MaterialTextView)view.findViewById(R.id.txtCurrent)).setText(String.format("%.2f",session.getLatest()));
    }

    public void initializeButtons() {
        view.findViewById(R.id.btnLdvStart).setOnClickListener(view -> {
            Log.d("Obj",dc.toString());
            Log.d("ButtonPress", "Start Collecting");
            dc.startCollecting();
        });
        view.findViewById(R.id.btnLdvStop).setOnClickListener(view -> {
            Log.d("stop", "scan stopped");
            dc.stopCollecting();
        });
    }
    @Override
    int getSaveButtonId() {
        //TODO: IMPLEMENT if we want to save live data sessions
        return 0;
    }
}