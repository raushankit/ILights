package com.raushankit.ILghts.observer;

import androidx.annotation.NonNull;

import com.raushankit.ILghts.flivedata.ChildDelayLiveData;

public class BoardRoomIdLivedata extends ChildDelayLiveData<Integer> {
    public BoardRoomIdLivedata(@NonNull String userId) {
        super("user_boards/" + userId, Integer.class);
    }
}
