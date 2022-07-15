package com.raushankit.ILghts.viewModel;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.raushankit.ILghts.model.User;

public class BoardCommViewModel extends ViewModel {
    private final MutableLiveData<Pair<String, User>> userMutableLiveData = new MutableLiveData<>();

    public LiveData<Pair<String, User>> getData(){
        return userMutableLiveData;
    }

    public void setData(@NonNull Pair<String, User> data){
        userMutableLiveData.setValue(data);
    }
}
