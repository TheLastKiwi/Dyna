package com.flying_kiwi.dyna;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class TimestampedWeight implements Serializable {
    public long getTimestamp() {
        return timestamp;
    }

    public float getWeight() {
        return weight;
    }

    private final long timestamp; // v0
    private final float weight; // v0

    public boolean isKg() {
        return isKg;
    }

    private boolean isKg; // v1
    private static final long serialVersionUID = -1786388294433430104L;
    private final int version; // v1
    public TimestampedWeight(float w, boolean isKg){
        this.timestamp = System.currentTimeMillis();
        this.weight = w;
        this.isKg = isKg;
        this.version = 1;
    }


    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        try {
            in.defaultReadObject();
            switch (version){
                case 0:
                    isKg = false; //not really needed but I'll keep it to know what was modified when
                case 1:
                  // Current
            }
        } catch (IOException | ClassNotFoundException e) {

        }
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("%.2f %s",weight, isKg?"kg":"lb");
    }
}
