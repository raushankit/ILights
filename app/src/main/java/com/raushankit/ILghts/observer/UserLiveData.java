package com.raushankit.ILghts.observer;

import androidx.annotation.NonNull;

import com.raushankit.ILghts.flivedata.ValueDelayLiveData;
import com.raushankit.ILghts.model.User;

public class UserLiveData extends ValueDelayLiveData<User> {
    public UserLiveData(@NonNull String path) {
        super(path, User.class);
    }
}
