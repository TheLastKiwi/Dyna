package com.example.dyna;

import java.io.Serializable;

public class TimestampedWeight implements Serializable {
    long timestamp;
    int weight;
    public TimestampedWeight(int w){
        this.timestamp = System.currentTimeMillis();
        this.weight = w;
    }
}
