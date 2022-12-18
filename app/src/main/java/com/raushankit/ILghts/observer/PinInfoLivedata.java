package com.raushankit.ILghts.observer;

import com.raushankit.ILghts.flivedata.ChildDelayLiveData;
import com.raushankit.ILghts.model.PinInfo;

public class PinInfoLivedata extends ChildDelayLiveData<PinInfo> {
    public PinInfoLivedata(String boardId) {
        super("/board_control/" + boardId + "/info", PinInfo.class);
    }
}
