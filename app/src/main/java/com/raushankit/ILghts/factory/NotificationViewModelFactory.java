package com.raushankit.ILghts.factory;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.raushankit.ILghts.viewModel.NotificationViewModel;

public class NotificationViewModelFactory implements ViewModelProvider.Factory {
    private final Application mApplication;
    private final String userId;

    public NotificationViewModelFactory(Application application, @NonNull String userId) {
        mApplication = application;
        this.userId = userId;
    }

    @SuppressLint("UnsafeOptInUsageWarning")
    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new NotificationViewModel(mApplication, userId);
    }
}
