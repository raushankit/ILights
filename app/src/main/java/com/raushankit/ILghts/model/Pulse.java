package com.raushankit.ILghts.model;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

@Keep
@IgnoreExtraProperties
@SuppressWarnings("unused")
public class Pulse {

    @PropertyName("heart_beat")
    private long aliveTime;

    public Pulse(){

    }

    public Pulse(long aliveTime) {
        this.aliveTime = aliveTime;
    }

    @PropertyName("heart_beat")
    public long getAliveTime() {
        return aliveTime;
    }

    @PropertyName("heart_beat")
    public void setAliveTime(long aliveTime) {
        this.aliveTime = aliveTime;
    }

    @NonNull
    @Override
    public String toString() {
        return "Pulse{" +
                "aliveTime=" + aliveTime +
                '}';
    }
}

