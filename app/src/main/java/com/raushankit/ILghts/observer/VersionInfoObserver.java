package com.raushankit.ILghts.observer;

import androidx.annotation.NonNull;

import com.raushankit.ILghts.flivedata.ValueDelayLiveData;
import com.raushankit.ILghts.model.VersionInfo;

public class VersionInfoObserver extends ValueDelayLiveData<VersionInfo> {
    public VersionInfoObserver(@NonNull String path) {
        super(path, VersionInfo.class);
    }
}
