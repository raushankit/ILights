package com.raushankit.ILghts.paging;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxPagingSource;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.raushankit.ILghts.model.board.BoardAuthUser;
import com.raushankit.ILghts.response.BoardMemberResponse;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class BoardMemberPagingSource extends RxPagingSource<Long, BoardAuthUser> {
    private static final String TAG = "BoardMemberPagingSource";
    private final Long boardCreationTime;
    private final DatabaseReference dbRef;
    private final String boardId;

    public BoardMemberPagingSource(@NonNull String boardId, @NonNull Long boardCreationTime){
        Log.i(TAG, "BoardMemberPagingSource() called with: boardId = [" + boardId + "], boardCreationTime = [" + boardCreationTime + "]");
        this.boardCreationTime = boardCreationTime;
        this.boardId = boardId;
        dbRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("board_auth")
                .child(boardId);
    }

    @Nullable
    @Override
    public Long getRefreshKey(@NonNull PagingState<Long, BoardAuthUser> pagingState) {
        Integer anchorPosition = pagingState.getAnchorPosition();
        Log.i(TAG, "getRefreshKey: anchorPosition = " + anchorPosition);
        if(anchorPosition == null){
            return null;
        }
        LoadResult.Page<Long, BoardAuthUser> anchorPage = pagingState.closestPageToPosition(anchorPosition);
        Log.i(TAG, "getRefreshKey: anchorPage = " + anchorPage);
        if(anchorPage == null){ return null; }
        Long prevKey = anchorPage.getPrevKey();
        if(prevKey != null){ return prevKey + 1; }
        Long nextKey = anchorPage.getNextKey();
        if(nextKey != null){ return nextKey - 1; }
        return null;
    }

    private LoadResult<Long, BoardAuthUser> toLoadResult(BoardMemberResponse response){
        Log.i(TAG, "toLoadResult: + " + response);
        return new LoadResult.Page<>(
                response.getList(),
                null,
                response.getNextKey(),
                LoadResult.Page.COUNT_UNDEFINED,
                LoadResult.Page.COUNT_UNDEFINED
        );
    }

    @NonNull
    @Override
    public Single<LoadResult<Long, BoardAuthUser>> loadSingle(@NonNull LoadParams<Long> loadParams) {
        Long key = loadParams.getKey();
        if(key == null){ key = boardCreationTime; }
        Log.i(TAG, "loadSingle() called with: loadParams = [" + loadParams.getLoadSize() + ", " + loadParams.getKey() + "]");
        return getData(key, loadParams.getLoadSize())
                .subscribeOn(Schedulers.io())
                .map(this::toLoadResult)
                .onErrorReturn(LoadResult.Error::new);
    }

    private Single<BoardMemberResponse> getData(Long time, int num){
        Log.i(TAG, "getData() called with: time = [" + time + "], num = [" + num + "]");
        return Single.create(emitter -> dbRef.orderByChild("creationTime")
                .startAfter(time)
                .limitToFirst(num)
                .get()
                .addOnCompleteListener(task -> {
                    Log.i(TAG, "getData: addOnCompleteListener: " + task.isSuccessful());
                    if(task.isSuccessful()){
                        BoardMemberResponse response = new BoardMemberResponse(boardId, num, task.getResult());
                        emitter.onSuccess(response);
                        Log.i(TAG, "getData: success");
                    }else{
                        emitter.onError(task.getException());
                        Log.i(TAG, "getData: error " + task.getException());
                    }
                }));
    }

}
