package com.raushankit.ILghts.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.raushankit.ILghts.model.PinInfo;
import com.raushankit.ILghts.observer.BoardMetaData;
import com.raushankit.ILghts.observer.PinInfoLivedata;

import java.util.Map;

public class EditPinViewModel extends ViewModel {
    private final PinInfoLivedata pinInfoLivedata;
    private final BoardMetaData boardMetaData;

    public EditPinViewModel() {
        pinInfoLivedata = new PinInfoLivedata();
        boardMetaData = new BoardMetaData();
    }

    public LiveData<Map<String, PinInfo>> getPinIfo() {
        return pinInfoLivedata;
    }

    public LiveData<Integer> getBoardData() {
        return boardMetaData;
    }

}
