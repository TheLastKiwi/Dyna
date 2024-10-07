package com.flying_kiwi.dyna;

import java.io.Serializable;

public class TimestampedWeight implements Serializable {
    public long getTimestamp() {
        return timestamp;
    }

    public float getWeight() {
        return weight;
    }

    private final long timestamp;
    private final float weight;
    public TimestampedWeight(float w){
        this.timestamp = System.currentTimeMillis();
        this.weight = w;
    }
}
