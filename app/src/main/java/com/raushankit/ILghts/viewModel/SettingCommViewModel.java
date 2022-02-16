package com.raushankit.ILghts.viewModel;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingCommViewModel extends ViewModel {
    private final MutableLiveData<Pair<String, Object>> selectedItem = new MutableLiveData<>();

    public void selectItem(Pair<String, Object> item) {
        selectedItem.setValue(item);
    }

    public LiveData<Pair<String, Object>> getSelectedItem() {
        return selectedItem;
    }
}
