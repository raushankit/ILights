package com.raushankit.ILghts.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.raushankit.ILghts.entity.ControllerFragActions;

public class FragViewModel extends ViewModel {
    private final MutableLiveData<ControllerFragActions> selectedItem = new MutableLiveData<>();

    public void selectItem(ControllerFragActions item) {
        selectedItem.setValue(item);
    }

    public LiveData<ControllerFragActions> getSelectedItem() {
        return selectedItem;
    }
}
