package com.flying_kiwi.dyna.LiveDataViews;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flying_kiwi.dyna.R;
import com.flying_kiwi.dyna.TimestampedWeight;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class CriticalForceLiveData extends BaseLiveDataView {

    //TODO: Effectively just static settings repeaters with a calculation at the end
    int repNum = 0;
    int countdownLeft = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        view = inflater.inflate(R.layout.critical_force_live_data_fragment,container, false);
        lineChart = view.findViewById(R.id.lineChartCritical);

        if(isHistorical) {
            view.findViewById(R.id.mcvCF).setVisibility(View.VISIBLE);
            ((MaterialTextView) view.findViewById(R.id.txtCF)).setText(String.format("%.2f",session.getCF()));
            view.findViewById(R.id.mcvWP).setVisibility(View.VISIBLE);
            ((MaterialTextView) view.findViewById(R.id.txtWP)).setText(String.format("%.2f",session.getWP()));
            view.findViewById(R.id.mcvRep).setVisibility(View.GONE);
            view.findViewById(R.id.mcvWeight).setVisibility(View.GONE);

            //Set horizontal line for CF
            LimitLine limitLine = new LimitLine(session.getCF(), ""); // Position at Y = 50
            limitLine.setLineWidth(2f);
            limitLine.setLineColor(Color.YELLOW);  // Set line color
            limitLine.enableDashedLine(10f, 10f, 0f); // Optional: dashed line
            YAxis leftAxis = lineChart.getAxisLeft();
            leftAxis.addLimitLine(limitLine);

            lineChart.getAxisRight().setEnabled(false);
            displayChart();
            updateStats();
        } else {
            timeLimit = 7000;
        }
        initializeStartStopSaveExportButtons(
                view.findViewById(R.id.btnCriticalStart),
                view.findViewById(R.id.btnCriticalStop),
                view.findViewById(R.id.btnCriticalSave),
                view.findViewById(R.id.btnCriticalExport)
        );
        view.findViewById(R.id.btnCriticalStop).setOnClickListener(v -> {
            Log.d("stop", "scan stopped");
            view.findViewById(R.id.btnCriticalExport).setEnabled(true);
            view.findViewById(R.id.btnCriticalSave).setEnabled(true);
            v.setEnabled(false);
            dc.stopCollecting();
            stopTimer();
        });
        view.findViewById(R.id.btnCriticalStart).setOnClickListener(v -> {
            view.findViewById(R.id.btnCriticalStop).setEnabled(true);
            v.setEnabled(false);
            startTimer();
        });
        return view;
    }

    @Override
    public void updateStats() {
        ((TextView)view.findViewById(R.id.txtCriticalCurrent)).setText(String.valueOf(session.getLatest()));
        ((TextView)view.findViewById(R.id.txtCriticalRepNum)).setText(repNum + "/" + session.getNumReps());
        ((TextView)view.findViewById(R.id.txtCountdown)).setText(String.valueOf(countdownLeft));
        if(isWorking){
            ((TextView) view.findViewById(R.id.txtPullRest)).setText("Pull");
        } else {
            ((TextView) view.findViewById(R.id.txtPullRest)).setText("Rest");
        }
    }
    @Override
    int getSaveButtonId() {
        return R.id.btnCriticalSave;
    }

    int timer = 0;
    boolean isWorking = false;
    CountDownTimer countDownTimer;
    private void startTimer() {
        /*   rest = even; work = odd;
             I couldn't figure out the math so this is how I did it
             I made an array of rest/work/rest/work of cumulative times for the entire session
             duration of the session
             As the 'int timer' increases if it's above the current position we are at currently at
             then we know we are in the next block of either rest or work so we can keep track of
             reps and sets easily. It's such a small calculation that it's negligible.
        */
        final ArrayList<Integer> restWork = new ArrayList<>();
        restWork.add(session.getCountdown());
        int pos = 1;
//        session.setWorkTime(2);
//        session.setRestTime(1);
        for(int s = 0; s < session.getNumSets(); s++){
            for(int r = 0; r < session.getNumReps(); r++){
                restWork.add(session.getWorkTime() + restWork.get(pos++-1));
                restWork.add(session.getRestTime() + restWork.get(pos++-1));
            }
            restWork.set(pos - 1, session.getPauseTime() + restWork.get(pos-2));
        }
        restWork.remove(restWork.size() - 1);
        countDownTimer = new CountDownTimer((restWork.get(restWork.size() - 1) - timer) * 1000L, 1000) {
            int restWorkPos = 0;
            @Override
            public void onTick(long millisUntilFinished) {
                timer++;
                //If we have exceeded the current rest/work cycle move to next
                if(timer > restWork.get(restWorkPos)) {
                    restWorkPos++;
                    isWorking = !isWorking;
                    if(isWorking){
                        repNum = (++repNum) % session.getNumReps();
                        if(repNum == 1) dc.startCollecting();
//                        dc.startCollecting();
                    } else {
//                        dc.stopCollecting();
                    }
                }
                countdownLeft = restWork.get(restWorkPos) - timer + 1;

                if(countdownLeft <= session.getCountdown()){
                    ((MaterialTextView)view.findViewById(R.id.txtCountdown)).setTextColor(Color.RED);
                    //TODO: Display larger countdown timer
                } else {
                    ((MaterialTextView)view.findViewById(R.id.txtCountdown)).setTextColor(Color.BLACK);
                }
                updateStats();
            }

            @Override
            public void onFinish() {
                //TODO: Some calculation of critical force.
                // Save to session, show and set a textbox with the calculation
                float[] cfwp = getCFWP();
                float critForce = cfwp[0];
                float wp = cfwp[1];
                session.setCF(critForce);
                session.setWP(wp);
                ((MaterialTextView)view.findViewById(R.id.txtCF)).setText(String.format("%.2f", critForce));
                ((MaterialTextView)view.findViewById(R.id.txtWP)).setText(String.format("%.2f", wp));
                view.findViewById(R.id.mcvCF).setVisibility(View.VISIBLE);
                view.findViewById(R.id.mcvWP).setVisibility(View.VISIBLE);
                view.findViewById(R.id.mcvRep).setVisibility(View.GONE);
                view.findViewById(R.id.mcvWeight).setVisibility(View.GONE);
                view.findViewById(R.id.btnCriticalStop).setEnabled(false);
                view.findViewById(R.id.btnCriticalStart).setEnabled(false);
                view.findViewById(R.id.btnCriticalSave).setEnabled(true);
                view.findViewById(R.id.btnCriticalExport).setEnabled(true);
                dc.stopCollecting();
            }
        }.start();
    }

    private void stopTimer() {
        countDownTimer.cancel();
    }

    //TODO: Maybe also show the average graphs at some point idk
    public float[] getCFWP(){
        //Take the last 60 seconds. Compute the average of all values above 50% of max pull
        //You're supposed to calculate std dev but I mean if you're giving it your all then you're
        //pulling as hard as you can as long as you can. If you're not giving it your all then the
        //value is pointless anyways. 1 std dev is just to remove the fluctuations which this does
        //TODO: I guess it does take out the spikes at the start too. Meh, I'll implement it later.
        List<TimestampedWeight> weights = session.getWeights();
        long ending = weights.get(weights.size() - 1).getTimestamp();
        int startingIndex = weights.size() - 1;
        float maxval = 0;

        while(ending - weights.get(startingIndex).getTimestamp() < 60000){
            maxval = Math.max(maxval, weights.get(startingIndex--).getWeight());
        }
        float sum = 0;
        int count = 0;
        for(int i = startingIndex; i < weights.size(); i++){
            if(weights.get(i).getWeight() >= maxval/5){
                sum += weights.get(i).getWeight();
                count++;
            }
        }
        float CF = sum/count;
        float WP = 0;
        for(int i = 1; i < weights.size(); i++){
            if(weights.get(i).getWeight() > CF){
                WP += weights.get(i).getWeight() * (weights.get(i).getTimestamp() - weights.get(i-1).getTimestamp())/1000;
                count++;
            }
        }
        return new float[]{CF,WP};
    }
}