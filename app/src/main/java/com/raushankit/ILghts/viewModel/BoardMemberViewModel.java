package com.raushankit.ILghts.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;

import com.raushankit.ILghts.model.board.BoardAuthUser;
import com.raushankit.ILghts.paging.BoardMemberPagingSource;

import io.reactivex.rxjava3.core.Flowable;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.ExperimentalCoroutinesApi;

public class BoardMemberViewModel extends AndroidViewModel {

    private final Flowable<PagingData<BoardAuthUser>> flowable;

    @ExperimentalCoroutinesApi
    public BoardMemberViewModel(Application application, @NonNull String boardId, @NonNull Long creationTime){
        super(application);
        BoardMemberPagingSource pagingSource = new BoardMemberPagingSource(boardId, creationTime);
        Pager<String, BoardAuthUser> pager = new Pager<>(
                new PagingConfig(10,
                        10,
                        false,
                        20,
                        10*999),
                () -> pagingSource
        );
        flowable = PagingRx.getFlowable(pager);
        CoroutineScope scope = ViewModelKt.getViewModelScope(this);
        PagingRx.cachedIn(flowable, scope);
    }

    public Flowable<PagingData<BoardAuthUser>> getFlowable() {
        return flowable;
    }
}
