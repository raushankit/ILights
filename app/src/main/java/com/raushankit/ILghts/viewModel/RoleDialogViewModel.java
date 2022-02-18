package com.raushankit.ILghts.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.raushankit.ILghts.model.RoleModData;
import com.raushankit.ILghts.utils.SingleLiveData;

public class RoleDialogViewModel extends ViewModel {
    private SingleLiveData<RoleModData> data = new SingleLiveData<>();

    public LiveData<RoleModData> getData() {
        return data;
    }

    public void setData(RoleModData roleModData){
        data.setValue(roleModData);
    }
}
