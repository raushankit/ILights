package com.raushankit.ILghts.observer;

import android.util.Log;

import androidx.annotation.NonNull;

import com.raushankit.ILghts.flivedata.ValueDelayLiveData;
import com.raushankit.ILghts.model.Role;

public class RoleLiveData extends ValueDelayLiveData<Role> {
    private static final String TAG = "ROLE_LIVEDATA";

    public RoleLiveData(@NonNull String path) {
        super(path, Role.class);
        Log.d(TAG, "RoleLiveData: ");
    }
}
