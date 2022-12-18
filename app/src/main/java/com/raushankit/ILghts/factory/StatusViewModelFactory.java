package com.raushankit.ILghts.factory;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.raushankit.ILghts.viewModel.StatusViewModel;

public class StatusViewModelFactory implements ViewModelProvider.Factory {
    private final Application mApplication;
    private final String boardId;

    public StatusViewModelFactory(Application application, @NonNull String boardId) {
        mApplication = application;
        this.boardId = boardId;
    }

    @SuppressLint("UnsafeOptInUsageWarning")
    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new StatusViewModel(mApplication, boardId);
    }
}
