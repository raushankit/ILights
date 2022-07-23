package com.raushankit.ILghts.factory;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.raushankit.ILghts.viewModel.BoardMemberViewModel;

public class BoardMemberViewModelFactory implements ViewModelProvider.Factory {

    private final Application mApplication;
    private final String boardId;
    private final Long creationTime;

    public BoardMemberViewModelFactory(Application application, @NonNull String boardId, @NonNull Long creationTime) {
        mApplication = application;
        this.boardId = boardId;
        this.creationTime = creationTime;
    }


    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new BoardMemberViewModel(mApplication, boardId);
    }
}
