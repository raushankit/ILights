package com.raushankit.ILghts.factory;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.raushankit.ILghts.model.room.BoardRoomUserData;
import com.raushankit.ILghts.viewModel.PinDataViewModel;

public class PinDataViewModelFactory implements ViewModelProvider.Factory {
    private final Application mApplication;
    private final String userId;
    private final BoardRoomUserData boardData;

    public PinDataViewModelFactory(Application application, @NonNull String userId, @NonNull BoardRoomUserData boardData) {
        mApplication = application;
        this.userId = userId;
        this.boardData = boardData;
    }

    @SuppressLint("UnsafeOptInUsageWarning")
    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new PinDataViewModel(mApplication, userId, boardData);
    }
}
