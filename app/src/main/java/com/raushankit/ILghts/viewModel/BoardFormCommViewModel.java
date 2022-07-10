package com.raushankit.ILghts.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BoardFormCommViewModel extends ViewModel {
    private final MutableLiveData<String> data = new MutableLiveData<>();
    private final MutableLiveData<String> currentFrag = new MutableLiveData<>();

    public LiveData<String> getData(){
        return data;
    }

    public LiveData<String> getCurrentFrag() {
        return currentFrag;
    }

    public void putData(String value){
        data.setValue(value);
    }

    public void setCurrentFrag(String name){
        currentFrag.setValue(name);
    }
}
