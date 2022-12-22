package com.raushankit.ILghts.model;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

@Keep
@IgnoreExtraProperties
@SuppressWarnings("unused")
public class PinInfo {

    private String name;

    private String description;

    public PinInfo() {

    }

    public PinInfo(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NonNull
    @Override
    public String toString() {
        return "PinInfo{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
