package com.example.dyna;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
enum SessionType implements Serializable{
    //TODO: I could over-engineer this and have subclasses of session for each type to hold
    // only their own data. Maybe in the future I'll do that
    LIVE_DATA,
    PEAK_LOAD,
    ENDURANCE,
    REPEATER,
    CRITICAL_FORCE
}
public class Session implements Serializable {
    SessionType sessionType;
    List<TimestampedWeight> weights;
    String name;
    int sessionMax = 0;
    int currentAvg = 0;
    private int weightSum = 0;

    int enduranceDuration;
    int numSets;
    int numReps;
    int workTime;
    int restTime;
    int pauseTime;
    int countdown;
    boolean plotTarget;
    int plotMin;
    int plotMax;
    boolean sound;


    Session(){
        weights = new ArrayList<>();
    }
    Session(SessionType type) {
        this.sessionType = type;
        weights = new ArrayList<>();
    }

    public void addWeight(TimestampedWeight timestampedWeight){
        weights.add(timestampedWeight);
        sessionMax = Math.max(sessionMax, timestampedWeight.weight);
        weightSum += timestampedWeight.weight;
        currentAvg = weightSum / weights.size();
    }
    public int getLatest(){
        if(weights.isEmpty()) return 0;
        return weights.get(weights.size()-1).weight;
    }
    public long getSesionLength(){
        return weights.get(weights.size() - 1).timestamp - weights.get(0).timestamp;
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
