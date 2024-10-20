package com.flying_kiwi.dyna.LiveDataViews;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flying_kiwi.dyna.R;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;

public class PeakLoadLiveData extends BaseLiveDataView {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        view = inflater.inflate(R.layout.peak_load_live_data_fragment,container, false);
        lineChart = view.findViewById(R.id.lineChartPeakData);

        if(isHistorical) {
            displayChart();
            updateStats();
        } else {
            timeLimit = 5000;
        }

        initializeStartStopSaveExportButtons(
                view.findViewById(R.id.btnPeakStart),
                view.findViewById(R.id.btnPeakStop),
                view.findViewById(R.id.btnPeakSave),
                view.findViewById(R.id.btnPeakExport)
        );

        return view;
    }

    @Override
    public void updateStats() {
        ((TextView)view.findViewById(R.id.txtPeakMax)).setText(session.getSessionMax().toString());
    }
    @Override
    public void displayChart(){
        //TODO: If units are KG, we need a different formula, one that maxes out at 300
        // https://mycurvefit.com/
        //TODO: If we modify DataCollector to divide readings by 100 we need to remove division here
        YAxis leftAxis = lineChart.getAxisLeft();
        double x = session.getSessionMax().getWeight();
        float limit = (float)(4.641323f*Math.pow(x,0.7529087));
        leftAxis.setAxisMaximum(limit);
        leftAxis.setAxisMinimum(0f);
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setAxisMinimum(0f);
        rightAxis.setAxisMaximum(limit);

        LimitLine llPlotMax = new LimitLine(session.getSessionMax().getWeight(), "Max");

        llPlotMax.setLineWidth(2f);
        llPlotMax.setLineColor(Color.GREEN);
        llPlotMax.enableDashedLine(10f, 10f, 0f);
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(llPlotMax);

        super.displayChart();
    }

    @Override
    int getSaveButtonId() {
        return R.id.btnPeakSave;
    }
}