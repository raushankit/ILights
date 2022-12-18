package com.raushankit.ILghts.observer;

import androidx.annotation.NonNull;

import com.raushankit.ILghts.flivedata.ValueDelayLiveData;

public class PulseLiveData extends ValueDelayLiveData<Long> {
    public PulseLiveData(@NonNull String boardID) {
        super("board_details/" + boardID + "/heartBeat", Long.class);
    }
}
