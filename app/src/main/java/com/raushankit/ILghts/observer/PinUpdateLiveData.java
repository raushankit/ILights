package com.raushankit.ILghts.observer;

import com.raushankit.ILghts.flivedata.ChildDelayLiveData;
import com.raushankit.ILghts.model.PinData;

public class PinUpdateLiveData extends ChildDelayLiveData<PinData> {
    public PinUpdateLiveData() {
        super("/control/update", PinData.class);
    }
}
