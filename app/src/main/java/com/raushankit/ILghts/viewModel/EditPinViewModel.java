package com.raushankit.ILghts.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.raushankit.ILghts.model.BoardPinData;
import com.raushankit.ILghts.model.EditPinInfo;
import com.raushankit.ILghts.model.PinData;
import com.raushankit.ILghts.model.PinInfo;
import com.raushankit.ILghts.observer.BoardMetaData;
import com.raushankit.ILghts.observer.PinInfoLivedata;

import java.util.LinkedHashMap;
import java.util.Map;

public class EditPinViewModel extends ViewModel {
    private final PinInfoLivedata pinInfoLivedata;
    private final BoardMetaData boardMetaData;

    public EditPinViewModel(){
        pinInfoLivedata = new PinInfoLivedata();
        boardMetaData = new BoardMetaData();
    }

    public LiveData<Map<String, PinInfo>> getPinIfo(){
        return pinInfoLivedata;
    }

    public LiveData<BoardPinData> getBoardData(){
        return boardMetaData;
    }

    public Map<String, Object> getUpdateMap(EditPinInfo pinInfo, PinData pinData){
        Map<String, Object> mp = new LinkedHashMap<>();
        mp.put("control/info/"+pinInfo.getPinNumber(), pinInfo.getPinInfo());
        mp.put("control/update/"+pinInfo.getPinNumber(), pinData);
        mp.put("control/status/"+pinInfo.getPinNumber(), Boolean.FALSE);
        return mp;
    }
}
