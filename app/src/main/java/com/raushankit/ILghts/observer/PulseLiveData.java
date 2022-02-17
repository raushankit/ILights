package com.raushankit.ILghts.observer;

import com.raushankit.ILghts.flivedata.ValueDelayLiveData;

public class PulseLiveData extends ValueDelayLiveData<Long> {
    public PulseLiveData() {
        super("metadata/board_data/heart_beat", Long.class);
    }
}
