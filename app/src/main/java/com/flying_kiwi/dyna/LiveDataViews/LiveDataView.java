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
        initializeStartStopSaveExportButtons(view.findViewById(R.id.btnLdvStart),view.findViewById(R.id.btnLdvStop),view.findViewById(R.id.btnLdvSave),view.findViewById(R.id.btnLdvExport));

        view.findViewById(R.id.btnLdvStop).setOnClickListener(v -> {
            Log.d("stop", "scan stopped");
            view.findViewById(R.id.btnLdvExport).setEnabled(true);
            view.findViewById(R.id.btnLdvSave).setEnabled(true);
            view.findViewById(R.id.btnLdvStart).setEnabled(true);
            v.setEnabled(false);
            dc.stopCollecting();
        });

        view.findViewById(R.id.btnLdvReset).setOnClickListener(view -> {
            dc.stopCollecting();
            //reset everthing. Min/Max/avg weights
        });

    }

    @Override
    int getSaveButtonId() {
        //TODO: IMPLEMENT if we want to save live data sessions
        return 0;
    }
}