package com.raushankit.ILghts.viewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingCommViewModel extends ViewModel {
    public static final String DEFAULT_KEY = "default_request_key";
    private final MutableLiveData<Object> selectedItem = new MutableLiveData<>();
    private String requestKey = DEFAULT_KEY;

    public void selectItem(@NonNull String requestKey, Object data) {
        this.requestKey = requestKey;
        selectedItem.setValue(data);
    }

    public LiveData<Object> getSelectedItem() {
        return selectedItem;
    }

    public String getRequestKey() {
        return requestKey;
    }
}
