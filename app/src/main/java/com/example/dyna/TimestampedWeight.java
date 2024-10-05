package com.example.dyna;

import java.io.Serializable;

public class TimestampedWeight implements Serializable {
    public long getTimestamp() {
        return timestamp;
    }

    public int getWeight() {
        return weight;
    }

    private final long timestamp;
    private final int weight;
    public TimestampedWeight(int w){
        this.timestamp = System.currentTimeMillis();
        this.weight = w;
    }
}
