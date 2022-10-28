package com.raushankit.ILghts.room;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.ExperimentalPagingApi;
import androidx.paging.LoadType;
import androidx.paging.PagingConfig;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxRemoteMediator;

import com.google.firebase.database.DatabaseReference;
import com.raushankit.ILghts.model.board.BoardAuthUser;
import com.raushankit.ILghts.response.BoardMemberResponse;

import java.io.IOException;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

@SuppressLint("UnsafeOptInUsageError")
public class BoardMemberRemoteMediator extends RxRemoteMediator<Integer, BoardAuthUser> {
    private static final String TAG = "BoardMemberRemoteMediator";
    private final String boardId;
    private final DatabaseReference db;
    private final BoardRoomDatabase database;
    private final BoardMemberDao memberDao;

    public BoardMemberRemoteMediator(DatabaseReference remoteDb, BoardRoomDatabase database, @NonNull String boardId){
        this.boardId = boardId;
        this.database = database;
        db = remoteDb
                .child("board_auth")
                .child(boardId);
        memberDao = database.boardMemberDao();
    }

    @ExperimentalPagingApi
    @NonNull
    @Override
    public Single<MediatorResult> loadSingle(@NonNull LoadType loadType, @NonNull PagingState<Integer, BoardAuthUser> state) {
        Long loadKey = null;
        PagingConfig config = state.getConfig();
        Log.d(TAG, "loadSingle() called with: loadType = [" + loadType + "], state = [" + state + "]");
        switch (loadType) {
            case REFRESH:
                break;
            case PREPEND:
                return Single.just(new MediatorResult.Success(true));
            case APPEND:
                BoardAuthUser lastItem = state.lastItemOrNull();
                if (lastItem == null) {
                    return Single.just(new MediatorResult.Success(true));
                }
                loadKey = lastItem.getCreationTime();
                break;
        }
        return getData(loadKey==null? 0L: loadKey, loadKey == null? config.initialLoadSize: config.pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map((Function<BoardMemberResponse, MediatorResult>) response -> {
                    Log.i(TAG, "loadSingle: response = " + response);
                    database.runInTransaction(() -> {
                        if(loadType == LoadType.REFRESH){
                            memberDao.deleteUsersByBoardIdList(boardId, response.getUserIds());
                        }
                        memberDao.insert(response.getList());
                    });
                    return new MediatorResult.Success(response.getNextKey() == null);
                })
                .onErrorResumeNext(e -> {
                    Log.i(TAG, "loadSingle: onErrorResumeNext");
                    if (e instanceof IOException) {
                        return Single.just(new MediatorResult.Error(e));
                    }
                    return Single.error(e);
                });
    }

    private Single<BoardMemberResponse> getData(Long time, int num){
        Log.i(TAG, "getData() called with: time = [" + time + "], num = [" + num + "]");
        return Single.create(emitter -> db.orderByChild("creationTime")
                .startAfter(time)
                .limitToFirst(num)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        emitter.onSuccess(new BoardMemberResponse(boardId, num, task.getResult()));
                        Log.i(TAG, "getData: success ");
                    }else{
                        Log.i(TAG, "getData: error " + task.getException());
                        emitter.onError(task.getException());
                    }
                }));
    }

    public void updateBoardUser(String userId, int level){
        BoardRoomDatabase.databaseExecutor.execute(() -> memberDao.updateUserLevelByBoardIdAndUserId(boardId, userId, level));
    }

    public void deleteUser(String userId){
        BoardRoomDatabase.databaseExecutor.execute(() -> memberDao.deleteUsersByBoardIdAndUserId(boardId, userId));
    }
}
