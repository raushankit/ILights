package com.raushankit.ILghts.observer;

import com.raushankit.ILghts.flivedata.ValueDelayLiveData;
import com.raushankit.ILghts.model.UpdatePriority;

public class UpdateTypeLiveData extends ValueDelayLiveData<UpdatePriority> {
    public UpdateTypeLiveData(int versionCode) {
        super("/metadata/version/" + versionCode, UpdatePriority.class);
    }
}
