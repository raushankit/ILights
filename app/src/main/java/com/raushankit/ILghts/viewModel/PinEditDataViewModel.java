package com.raushankit.ILghts.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import kotlin.Triple;

public class PinEditDataViewModel extends ViewModel {

    private final MutableLiveData<Triple<Integer, String, String>> pinData = new MutableLiveData<>();


    public void setPinData(int pinNumber, String title, String description) {
        pinData.setValue(new Triple<>(pinNumber, title, description));
    }

    public LiveData<Triple<Integer, String, String>> getPinData() {
        return this.pinData;
    }
}
