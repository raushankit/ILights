package com.raushankit.ILghts.viewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.raushankit.ILghts.model.Role;
import com.raushankit.ILghts.model.SettingUserData;
import com.raushankit.ILghts.model.User;

public class SettingUserViewModel extends ViewModel {
    private static final String TAG = "SettingUserViewModel";
    private final MediatorLiveData<SettingUserData> data;
    private final SettingUserData settingUserData = new SettingUserData();
    private boolean gotUserData;
    private boolean gotRoleData;

    public SettingUserViewModel() {
        data = new MediatorLiveData<>();
        gotUserData = false;
        gotRoleData = false;
    }

    public LiveData<SettingUserData> getUserData() {
        return data;
    }

    public void addUserSource(LiveData<User> userLiveData) {
        data.addSource(userLiveData, user -> {
            gotUserData = user != null;
            if (gotUserData) settingUserData.setUser(user);
            if (gotUserData && gotRoleData) data.setValue(settingUserData);
        });
    }

    public void addRoleSource(LiveData<Role> roleLiveData) {
        data.addSource(roleLiveData, role -> {
            gotRoleData = role != null;
            if (gotRoleData) settingUserData.setRole(role);
            if (gotUserData && gotRoleData) data.setValue(settingUserData);
        });
    }

    public <S> void removeSource(LiveData<S> liveData) {
        data.removeSource(liveData);
    }

    @Override
    protected void onCleared() {
        Log.e(TAG, "onCleared: ");
        super.onCleared();
    }
}
