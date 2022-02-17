package com.raushankit.ILghts.observer;

import com.raushankit.ILghts.flivedata.ChildDelayLiveData;

public class PinStatusLiveData extends ChildDelayLiveData<Boolean> {
    public PinStatusLiveData() {
        super("/control/status", Boolean.class);
    }
}
