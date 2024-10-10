package com.flying_kiwi.dyna.LiveDataViews;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flying_kiwi.dyna.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class RepeaterLiveData extends BaseLiveDataView {
    int setNum = 0;
    int repNum = 0;
    int countdownLeft = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        view = inflater.inflate(R.layout.repeater_live_data_fragment,container, false);


        lineChart = view.findViewById(R.id.lineChartRepeater);
        if(isHistorical) {
            displayChart();
            updateStats();
        } else {
            timeLimit = session.getWorkTime();
        }
        initializeStartStopSaveExportButtons(
                view.findViewById(R.id.btnRepeaterStart),
                view.findViewById(R.id.btnRepeaterStop),
                view.findViewById(R.id.btnRepeaterSave),
                view.findViewById(R.id.btnRepeaterExport)
        );
        view.findViewById(R.id.btnRepeaterStop).setOnClickListener(v -> {
            Log.d("stop", "scan stopped");
            view.findViewById(R.id.btnRepeaterExport).setEnabled(true);
            view.findViewById(R.id.btnRepeaterSave).setEnabled(true);
            v.setEnabled(false);
            dc.stopCollecting();
            stopTimer();
        });
        view.findViewById(R.id.btnRepeaterStart).setOnClickListener(v -> {
            view.findViewById(R.id.btnRepeaterStop).setEnabled(true);
            startTimer();
        });

        timeLimit = session.getWorkTime() * 1000L;
        if(session.isPlotTarget()) {
            setLineLimits();
        }
        return view;
    }


    @Override
    public void updateStats() {
        ((TextView)view.findViewById(R.id.txtCriticalCurrent)).setText(String.format("%.2f",session.getLatest()));
        ((TextView)view.findViewById(R.id.txtCriticalRepNum)).setText(repNum + "/" + session.getNumReps());
        ((TextView)view.findViewById(R.id.txtRepeaterSetNum)).setText(setNum + "/" + session.getNumSets());
        ((TextView)view.findViewById(R.id.txtCountdown)).setText(String.valueOf(countdownLeft));
        if(isWorking){
            ((TextView) view.findViewById(R.id.txtPullRest)).setText("Pull");
        } else {
            ((TextView) view.findViewById(R.id.txtPullRest)).setText("Rest");
        }
    }

    @Override
    int getSaveButtonId() {
        return R.id.btnRepeaterSave;
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
                        if(setNum == 0) setNum = 1; //To deal with the first set after the initial countdown
                        repNum = (++repNum) % session.getNumReps();
                        dc.startCollecting();
                    } else {
                        if(timer - restWork.get(restWorkPos) == session.getPauseTime()){
                            setNum++;
                        }
                        dc.stopCollecting();
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
                dc.stopCollecting();
            }
        }.start();
    }

    private void stopTimer() {
        countDownTimer.cancel();
    }
}