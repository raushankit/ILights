package com.raushankit.ILghts.model;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

@Keep
@IgnoreExtraProperties
@SuppressWarnings("unused")
public class PinInfo {

    private String name;

    public PinInfo() {

    }

    public PinInfo(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return "PinInfo{" +
                "name='" + name + '\'' +
                '}';
    }
}
