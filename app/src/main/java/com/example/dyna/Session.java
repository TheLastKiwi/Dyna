package com.example.dyna;

import java.util.ArrayList;
import java.util.List;
enum SessionType {
    LIVE_DATA,
    PEAK_LOAD,
    ENDURANCE,
    REPEATER,
    CRITICAL_FORCE
}
public class Session {
    SessionType sessionType;
    List<TimestampedWeight> weights;
    int currentMax = 0;
    int sessionMax = 0;
    int currentAvg = 0;
    private int weightSum = 0;

    int targetZoneMin;
    int targetZoneMax;
    boolean beepWhenExitingTargetZone;
    int startCountdownTimer;

    int enduranceDuration;

    int repeaterWorkTime;
    int repeaterRestTime;
    int numRepeaterSets;
    int numRepeaterReps;
    int pauseBetweenRepeaterSetsLength;


    Session(){
        weights = new ArrayList<>();
    }

    public void addWeight(TimestampedWeight timestampedWeight){
        weights.add(timestampedWeight);

        currentMax = Math.max(currentMax, timestampedWeight.weight);
        sessionMax = Math.max(sessionMax, currentMax);
        weightSum += timestampedWeight.weight;
        currentAvg = weightSum / weights.size();
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
