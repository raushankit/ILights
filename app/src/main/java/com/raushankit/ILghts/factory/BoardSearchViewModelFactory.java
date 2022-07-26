package com.raushankit.ILghts.factory;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.raushankit.ILghts.viewModel.BoardSearchViewModel;

public class BoardSearchViewModelFactory implements ViewModelProvider.Factory {
    private final Application mApplication;
    private final String boardId;

    public BoardSearchViewModelFactory(Application application, @NonNull String boardId) {
        mApplication = application;
        this.boardId = boardId;
    }


    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new BoardSearchViewModel(mApplication, boardId);
    }
}

