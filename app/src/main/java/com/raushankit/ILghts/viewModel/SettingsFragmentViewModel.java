package com.raushankit.ILghts.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.raushankit.ILghts.entity.InfoType;
import com.raushankit.ILghts.model.VersionInfo;

public class SettingsFragmentViewModel extends ViewModel {
    private MutableLiveData<InfoType> profileInfo = new MutableLiveData<>();
    private MutableLiveData<VersionInfo> versionInfo = new MutableLiveData<>();

    public LiveData<VersionInfo> getLiveVersionInfo() {
        return versionInfo;
    }

    public LiveData<InfoType> getLiveProfileInfo() {
        return profileInfo;
    }

    public void setProfileInfo(InfoType infoType) {
        profileInfo.setValue(infoType);
    }

    public void setVersionInfo(VersionInfo vInfo) {
        versionInfo.setValue(vInfo);
    }
}
