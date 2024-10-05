package com.example.dyna.LiveDataViews;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dyna.R;

public class RepeaterLiveData extends BaseLiveDataView {
    int setNum = 0;
    int repNum = 0;
    int countdownLeft = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        view = inflater.inflate(R.layout.repeater_live_data_fragment,container, false);


        lineChart = view.findViewById(R.id.lineChartRepeater);
        initializeButtons();
        timeLimit = session.getWorkTime() * 1000L;
        if(session.isPlotTarget()) {
            setLineLimits();
        }
        return view;
    }


    @Override
    public void updateStats() {
        ((TextView)view.findViewById(R.id.txtRepeaterCurrent)).setText(String.valueOf(session.getLatest()));
        ((TextView)view.findViewById(R.id.txtRepeaterRepNum)).setText(String.valueOf(repNum));
        ((TextView)view.findViewById(R.id.txtRepeaterSetNum)).setText(setNum + "/" + session.getNumSets());
        ((TextView)view.findViewById(R.id.txtCountdown)).setText(String.valueOf(countdownLeft));
    }

    @Override
    int getSaveButtonId() {
        return R.id.btnRepeaterSave;
    }

    public void initializeButtons() {
        if(!isHistorical) {
            view.findViewById(R.id.btnRepeaterStart).setOnClickListener(view -> {
                timerTowerStart();
            });
            view.findViewById(R.id.btnRepeaterStop).setOnClickListener(view -> {
                Log.d("stop", "scan stopped");
                dc.stopCollecting();
            });
            view.findViewById(R.id.btnRepeaterSave).setOnClickListener(view -> {
                showSaveSessionDialog();
                if(session.getName() != null){
                    view.findViewById(R.id.btnRepeaterSave).setEnabled(false);
                }
            });
        } else {
            //TODO hide start and stop buttons
            // show export buttons
        }
    }

    private void timerTowerStart(){
        //Initial countdown timer
        countdownLeft = session.getCountdown();
        Log.d("TIMER", "initialTimer Started");
        new CountDownTimer(countdownLeft * 1000L, 1000) { // 10 seconds countdown, ticking every second
            public void onTick(long millisUntilFinished) {
                // Update UI with the time left
                countdownLeft--;
                Log.d("TIMER", "Seconds remaining: " + millisUntilFinished / 1000);
                updateStats();
            }

            public void onFinish() {
                Log.d("TIMER", "Initial timer finished");
                repNum = 1;
                setNum = 1;
                countdownLeft = session.getWorkTime();
                updateStats();
                startRepTimer();
            }
        }.start();
    }

    public void startCountdownTimer(){
        Log.d("TIMER", "countdownTimer Started");
        new CountDownTimer(countdownLeft * 1000L, 1000) {
            public void onTick(long millisUntilFinished) {
                // Update UI with the time left
                // big font
                countdownLeft--;
                Log.d("TIMER", "Seconds remaining: " + millisUntilFinished / 1000);
                updateStats();
            }

            public void onFinish() {

                if(repNum == session.getNumReps()) {
                    repNum = 1;
                    setNum ++;
                } else {
                    repNum++;
                }

                countdownLeft = session.getWorkTime();
                updateStats();
                startRepTimer();
            }
        }.start();
    }
    public void startRepTimer(){
        Log.d("TIMER", "repTimer Started");
        dc.startCollecting();
        ((TextView) view.findViewById(R.id.txtPullRest)).setText("Pull");
        new CountDownTimer(session.getWorkTime() * 1000L, 1000) { // 10 seconds countdown, ticking every second

            public void onTick(long millisUntilFinished) {
                // Update UI with the time left on rep
                countdownLeft--;
                Log.d("TIMER", "Seconds remaining: " + millisUntilFinished / 1000);
                updateStats();
            }

            public void onFinish() {
                // Code to run when the timer finishes
                Log.d("TIMER", "rep finished");
                ((TextView) view.findViewById(R.id.txtPullRest)).setText("Rest");
                if(repNum < session.getNumReps()) {
                    countdownLeft = session.getRestTime();
                    dc.stopCollecting();
                    startPauseBetweenRepsTimer();

                } else if(setNum < session.getNumSets()) {
                    countdownLeft = session.getPauseTime();
                    dc.stopCollecting();
                    startPauseBetweenSetsTimer();

                } else {
                    //Display final stats?
                    //stop collecting data
                    dc.stopScanning();
                }
                updateStats();

            }
        }.start();
    }
    public void startPauseBetweenRepsTimer(){
        dc.stopCollecting();
        Log.d("TIMER", "pauseBetweenReps Started");
        //if zero it'll never start, make sure it's never zero
        Log.d("TIMER",String.format("Rest: %d, \n CD: %d, \nPause: %d",session.getRestTime(), session.getCountdown(), Math.max(session.getRestTime() - session.getCountdown(), 0) * 1000L));
        new CountDownTimer(Math.max(session.getRestTime() - session.getCountdown(), 0) * 1000L, 1000) {
            public void onTick(long millisUntilFinished) {
                // Update UI with the time left before rep
                countdownLeft--;
                Log.d("TIMER", "Seconds remaining: " + millisUntilFinished / 1000);
                updateStats();
            }

            public void onFinish() {
                updateStats();
                startCountdownTimer();
            }
        }.start();
    }
    public void startPauseBetweenSetsTimer(){
        dc.stopCollecting();
        Log.d("TIMER", "pauseBetweenSets Started");
        new CountDownTimer(Math.max(session.getPauseTime() - session.getCountdown(), 0) * 1000L, 1000) {
            public void onTick(long millisUntilFinished) {
                // Update UI with the time left on pause
                countdownLeft--;
                Log.d("TIMER", "Seconds remaining: " + millisUntilFinished / 1000);
                updateStats();
            }

            public void onFinish() {
                updateStats();
                startCountdownTimer();
            }
        }.start();
    }
}