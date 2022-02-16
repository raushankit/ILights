package com.raushankit.ILghts.viewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.raushankit.ILghts.model.Role;
import com.raushankit.ILghts.model.SplashData;
import com.raushankit.ILghts.model.UpdatePriority;

public class SplashViewModel extends ViewModel {
    private static final String TAG = "SplashViewModel";
    private final MediatorLiveData<SplashData> data = new MediatorLiveData<>();
    private final SplashData chunk = new SplashData();
    private boolean GOT_ROLE_DATA;
    private boolean GOT_VERSION_DATA;
    private AppUpdateInfo appUpdateInfo;

    public SplashViewModel() {
        GOT_ROLE_DATA = false;
        GOT_VERSION_DATA = false;
    }

    public void addSource(LiveData<?> childData) {
        data.addSource(childData, o -> {
            if (o instanceof UpdatePriority) {
                Log.w(TAG, "addSource: update data = " + o);
                chunk.setUpdatePriority((UpdatePriority) o);
                GOT_VERSION_DATA = true;
            }
            if (o instanceof Role) {
                Log.w(TAG, "addSource: role data = " + o);
                chunk.setRole((Role) o);
                GOT_ROLE_DATA = true;
            }
            if (GOT_ROLE_DATA && GOT_VERSION_DATA) data.setValue(chunk);
        });
    }

    public void setVersionFlag(boolean flag) {
        GOT_VERSION_DATA = flag;
    }

    public void setRoleFlag(boolean flag) {
        GOT_ROLE_DATA = flag;
    }

    public LiveData<SplashData> getData() {
        return data;
    }

    public AppUpdateInfo getAppUpdateInfo() {
        return appUpdateInfo;
    }

    public void setAppUpdateInfo(AppUpdateInfo appUpdateInfo) {
        this.appUpdateInfo = appUpdateInfo;
    }
}
