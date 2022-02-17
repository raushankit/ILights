package com.raushankit.ILghts.observer;

import com.raushankit.ILghts.flivedata.ValueDelayLiveData;

public class BoardMetaData extends ValueDelayLiveData<Integer> {
    public BoardMetaData() {
        super("/metadata/board_data/available_pins", Integer.class);
    }
}
