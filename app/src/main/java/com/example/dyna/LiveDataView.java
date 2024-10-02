package com.example.dyna;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
        ((TextView)findViewById(R.id.txtMax)).setText(String.valueOf(session.sessionMax));
        ((TextView)findViewById(R.id.txtAvg)).setText(String.valueOf(session.currentAvg));
        ((TextView)findViewById(R.id.txtCurrent)).setText(String.valueOf(session.getLatest()));
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