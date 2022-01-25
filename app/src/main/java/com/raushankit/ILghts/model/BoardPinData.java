package com.raushankit.ILghts.model;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

@Keep
@IgnoreExtraProperties
@SuppressWarnings("unused")
public class BoardPinData {

    @PropertyName("available_pins")
    private int usablePins;

    public BoardPinData(){

    }

    public BoardPinData(int usablePins) {
        this.usablePins = usablePins;
    }

    @PropertyName("available_pins")
    public int getUsablePins() {
        return usablePins;
    }

    @PropertyName("available_pins")
    public void setUsablePins(int usablePins) {
        this.usablePins = usablePins;
    }

    @Override
    @NonNull
    public String toString() {
        return "BoardPinData{" +
                "usablePins=" + usablePins +
                '}';
    }
}
