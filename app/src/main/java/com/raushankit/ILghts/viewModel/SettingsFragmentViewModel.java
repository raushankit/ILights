package com.raushankit.ILghts.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.raushankit.ILghts.entity.InfoType;
import com.raushankit.ILghts.model.VersionInfo;
import com.raushankit.ILghts.utils.ListLiveData;

import java.util.HashSet;
import java.util.Set;

public class SettingsFragmentViewModel extends ViewModel {
    private final Set<InfoType> infoTypeSet = new HashSet<>();
    private final ListLiveData<InfoType.Keys,InfoType> profileInfo = new ListLiveData<>();
    private final MutableLiveData<VersionInfo> versionInfo = new MutableLiveData<>();

    public LiveData<VersionInfo> getLiveVersionInfo() {
        return versionInfo;
    }

    public LiveData<InfoType> getLiveProfileInfo() {
        return profileInfo;
    }

    public void setProfileInfo(InfoType.Keys key, InfoType infoType) {
        profileInfo.mSetValue(key, infoType);
    }

    public void setVersionInfo(VersionInfo vInfo) {
        versionInfo.setValue(vInfo);
    }
}
