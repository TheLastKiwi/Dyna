package com.flying_kiwi.dyna.Settings;

import java.io.Serializable;

public class PresetSettings implements Serializable {
    private String name;
    private int numSets;

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

    public int getPauseTime() {
        return pauseTime;
    }

    public int getCountdown() {
        return countdown;
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

    private int numReps;
    private int workTime;
    private int restTime;
    private int pauseTime;
    private int countdown;
    private boolean plotTarget;
    private int plotMin;
    private int plotMax;
    private boolean sound;
    //Repeaters
    PresetSettings(int sets, int reps, int work, int rest, int pause, int cd, boolean plot, int min, int max, boolean sound) {
        this.numSets = sets;
        this.numReps = reps;
        this.workTime = work;
        this.restTime = rest;
        this.pauseTime = pause;
        this.countdown = cd;
        this.plotTarget = plot;
        this.plotMin = min;
        this.plotMax  = max;
        this.sound = sound;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
