package com.raushankit.ILghts.factory;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.raushankit.ILghts.viewModel.PinDataViewModel;

public class PinDataViewModelFactory implements ViewModelProvider.Factory {
    private final Application mApplication;
    private final String userId;
    private final String boardId;

    public PinDataViewModelFactory(Application application, @NonNull String userId, @NonNull String boardId) {
        mApplication = application;
        this.userId = userId;
        this.boardId = boardId;
    }

    @SuppressLint("UnsafeOptInUsageWarning")
    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new PinDataViewModel(mApplication, userId, boardId);
    }
}
