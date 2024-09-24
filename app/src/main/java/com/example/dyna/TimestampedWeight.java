package com.example.dyna;

public class TimestampedWeight {
    long timestamp;
    int weight;
    public TimestampedWeight(int w){
        this.timestamp = System.currentTimeMillis();
        this.weight = w;
    }
}
