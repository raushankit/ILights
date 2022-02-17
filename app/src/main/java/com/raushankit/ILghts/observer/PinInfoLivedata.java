package com.raushankit.ILghts.observer;

import com.raushankit.ILghts.flivedata.ChildDelayLiveData;
import com.raushankit.ILghts.model.PinInfo;

public class PinInfoLivedata extends ChildDelayLiveData<PinInfo> {
    public PinInfoLivedata() {
        super("/control/info", PinInfo.class);
    }
}
