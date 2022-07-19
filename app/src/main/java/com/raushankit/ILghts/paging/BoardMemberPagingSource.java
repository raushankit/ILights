package com.raushankit.ILghts.paging;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxPagingSource;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.raushankit.ILghts.model.board.BoardAuthUser;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class BoardMemberPagingSource extends RxPagingSource<String, BoardAuthUser> {
    private static final String TAG = "BoardMemberPagingSource";
    private final Long boardCreationTime;
    private final DatabaseReference dbRef;

    public BoardMemberPagingSource(@NonNull String boardId, @NonNull Long boardCreationTime){
        Log.i(TAG, "BoardMemberPagingSource() called with: boardId = [" + boardId + "], boardCreationTime = [" + boardCreationTime + "]");
        this.boardCreationTime = boardCreationTime;
        dbRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("users");
                //.child(boardId);
    }

    @Nullable
    @Override
    public String getRefreshKey(@NonNull PagingState<String, BoardAuthUser> pagingState) {
        /*Integer anchorPosition = pagingState.getAnchorPosition();
        Log.i(TAG, "getRefreshKey: anchorPosition = " + anchorPosition);
        if(anchorPosition == null){
            return null;
        }
        LoadResult.Page<String, BoardAuthUser> anchorPage = pagingState.closestPageToPosition(anchorPosition);
        Log.i(TAG, "getRefreshKey: anchorPage = " + anchorPage);
        if(anchorPage == null){ return null; }
        Long prevKey = anchorPage.getPrevKey();
        if(prevKey != null){ return prevKey + 1; }
        Long nextKey = anchorPage.getNextKey();
        if(nextKey != null){ return nextKey - 1; }*/
        return null;
    }

    private LoadResult<String, BoardAuthUser> toLoadResult(BoardMemberResponse response){
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
    public Single<LoadResult<String, BoardAuthUser>> loadSingle(@NonNull LoadParams<String> loadParams) {
        String key = loadParams.getKey();
        if(key == null){ key = "a"; }
        Log.i(TAG, "loadSingle() called with: loadParams = [" + loadParams.getLoadSize() + ", " + loadParams.getKey() + "]");
        return getData(key, loadParams.getLoadSize())
                .subscribeOn(Schedulers.io())
                .map(this::toLoadResult)
                .onErrorReturn(LoadResult.Error::new);
    }

    private Single<BoardMemberResponse> getData(String name, int num){
        Log.i(TAG, "getData() called with: name = [" + name + "], num = [" + num + "]");
        return Single.create(emitter -> dbRef.orderByChild("name")
                .startAfter(name)
                .limitToFirst(num)
                .get()
                .addOnCompleteListener(task -> {
                    Log.i(TAG, "getData: addOnCompleteListener + " + task.isSuccessful());
                    if(task.isSuccessful()){
                        BoardMemberResponse response = new BoardMemberResponse(num, task.getResult());
                        emitter.onSuccess(response);
                        Log.i(TAG, "getData: success ");
                    }else{
                        emitter.onError(task.getException());
                        Log.i(TAG, "getData: error " + task.getException());
                    }
                }));
    }

}
