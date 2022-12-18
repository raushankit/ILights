package com.raushankit.ILghts.observer;

import com.raushankit.ILghts.flivedata.ChildDelayLiveData;

public class PinStatusLiveData extends ChildDelayLiveData<Boolean> {
    public PinStatusLiveData(String boardId) {
        super("/board_control/" + boardId + "/status", Boolean.class);
    }
}
