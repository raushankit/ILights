package com.raushankit.ILghts.observer;

import com.raushankit.ILghts.flivedata.ChildDelayLiveData;
import com.raushankit.ILghts.model.PinData;

public class PinUpdateLiveData extends ChildDelayLiveData<PinData> {
    public PinUpdateLiveData(String boardId) {
        super("/board_control/" + boardId + "/update", PinData.class);
    }
}
