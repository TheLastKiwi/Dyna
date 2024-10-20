package com.flying_kiwi.dyna;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Session implements Serializable {
    private SessionType sessionType;
    private List<TimestampedWeight> weights;

    private String name;
    private TimestampedWeight sessionMax;
    private float currentAvg = 0;
    private float weightSum = 0;
    private float CF;

    public float getCF() {
        return CF;
    }

    public void setCF(float CF) {
        this.CF = CF;
    }

    public float getWP() {
        return WP;
    }

    public void setWP(float WP) {
        this.WP = WP;
    }

    private float WP;
    int enduranceDuration;

    private int numSets;
    private int numReps;
    private int workTime;
    private int restTime;
    private int pauseTime;
    private int countdown;
    private boolean plotTarget;
    private int plotMin;
    private int plotMax;
    private boolean sound;

    private static final long serialVersionUID = 1779590375867446386L; //For deserialization, this was the original id before adding a versioning number for added fields
    private final int version;

    public Session(){
        weights = new ArrayList<>();
        version = 2;
    }
    public Session(SessionType type) {
        this.sessionType = type;
        weights = new ArrayList<>();
        version = 2;
    }

    public void addWeight(TimestampedWeight timestampedWeight){
        weights.add(timestampedWeight);
        if(sessionMax == null || sessionMax.getWeight() < timestampedWeight.getWeight()){
            sessionMax = timestampedWeight;
        }
        weightSum += timestampedWeight.getWeight();
        currentAvg = weightSum / weights.size();
    }
    public TimestampedWeight getLatest(){
        if(weights.isEmpty()) return new TimestampedWeight(0,false);
        return weights.get(weights.size()-1);
    }
    public long getSesionLength(){
        return weights.get(weights.size() - 1).getTimestamp() - weights.get(0).getTimestamp();
    }

    public SessionType getSessionType() {
        return sessionType;
    }

    public List<TimestampedWeight> getWeights() {
        return weights;
    }

    public String getName() {
        return name;
    }

    public TimestampedWeight getSessionMax() {
        return sessionMax != null ? sessionMax : new TimestampedWeight(0,false);
    }

    public float getCurrentAvg() {
        return currentAvg;
    }

    public float getWeightSum() {
        return weightSum;
    }

    public int getNumSets() {
        return numSets;
    }

    public int getNumReps() {
        return numReps;
    }

    public int getWorkTime() {
        return workTime;
    }

    public int getRestTime() {
        return restTime;
    }

    public int getCountdown() {
        return countdown;
    }

    public int getPauseTime() {
        return pauseTime;
    }

    public boolean isPlotTarget() {
        return plotTarget;
    }

    public int getPlotMin() {
        return plotMin;
    }

    public int getPlotMax() {
        return plotMax;
    }

    public boolean isSound() {
        return sound;
    }
    public void setNumSets(int numSets) {
        this.numSets = numSets;
    }

    public void setNumReps(int numReps) {
        this.numReps = numReps;
    }

    public void setWorkTime(int workTime) {
        this.workTime = workTime;
    }

    public void setRestTime(int restTime) {
        this.restTime = restTime;
    }

    public void setPauseTime(int pauseTime) {
        this.pauseTime = pauseTime;
    }

    public void setCountdown(int countdown) {
        this.countdown = countdown;
    }

    public void setPlotTarget(boolean plotTarget) {
        this.plotTarget = plotTarget;
    }

    public void setPlotMin(int plotMin) {
        this.plotMin = plotMin;
    }

    public void setPlotMax(int plotMax) {
        this.plotMax = plotMax;
    }

    public void setSound(boolean sound) {
        this.sound = sound;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        try {
            in.defaultReadObject();
            switch (version) {
                case 0:
                case 1:
                    //Get max manually
                    TimestampedWeight max = new TimestampedWeight(0,false);
                    for(TimestampedWeight tsw : weights){
                        if(tsw.getWeight()>max.getWeight()){
                            max = tsw;
                        }
                    }
                    sessionMax = max;
                case 2:
                    //Current
                    break;
                default:
            }
        } catch (IOException | ClassNotFoundException e) {
            //Ignore
            Log.d("err",e.toString());
        }
    }
}



/*
Peak Load -> No settings
Endurance -> Duration
             +Countdown duration before start
             +Target zone min
             +Target zone max
             +Beep when exiting target zone
Repeaters -> +# sets
             +# reps
             +Countdown before set start
             +Target zone min
             +Target zone max
             +Beep when exiting target zone
             +pause between sets duration
 */
