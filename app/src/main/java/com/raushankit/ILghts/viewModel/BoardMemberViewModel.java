package com.raushankit.ILghts.viewModel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.raushankit.ILghts.model.board.BoardAuthUser;
import com.raushankit.ILghts.room.BoardMemberDao;
import com.raushankit.ILghts.room.BoardMemberRemoteMediator;
import com.raushankit.ILghts.room.BoardRoomDatabase;
import com.raushankit.ILghts.utils.callbacks.CallBack;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.core.Flowable;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.ExperimentalCoroutinesApi;

public class BoardMemberViewModel extends AndroidViewModel {
    private static final String TAG = "BoardMemberViewModel";
    private final BoardMemberRemoteMediator remoteMediator;
    private final Flowable<PagingData<BoardAuthUser>> flowable;
    private final BoardMemberDao memberDao;
    private final DatabaseReference remoteDb;
    private final String boardId;

    @SuppressLint("UnsafeOptInUsageError")
    @ExperimentalCoroutinesApi
    public BoardMemberViewModel(Application application, @NonNull String boardId){
        super(application);
        Log.e(TAG, "BoardMemberViewModel: " + boardId);
        BoardRoomDatabase db = BoardRoomDatabase.getDatabase(application);
        remoteDb =  FirebaseDatabase.getInstance().getReference();
        this.boardId = boardId;
        memberDao = db.boardMemberDao();
        BoardRoomDatabase.databaseExecutor.execute(() -> {
            Log.i(TAG, "BoardMemberViewModel: init_data" + memberDao.getPagingMembersListByBoardId(boardId));
        });
        remoteMediator = new BoardMemberRemoteMediator(remoteDb, db, boardId);
        @SuppressLint("UnsafeOptInUsageError") Pager<Integer, BoardAuthUser> pager = new Pager<>(
                new PagingConfig(15),
                null,
                remoteMediator,
                () -> memberDao.getPagingMembersByBoardId(boardId)
        );
        flowable = PagingRx.getFlowable(pager);
        CoroutineScope scope = ViewModelKt.getViewModelScope(this);
        PagingRx.cachedIn(flowable, scope);
    }

    public Flowable<PagingData<BoardAuthUser>> getFlowable() {
        return flowable;
    }

    @SuppressLint("UnsafeOptInUsageError")
    public void promoteUser(String userId, int level, @NonNull CallBack<DatabaseError> errorCallBack){
        Map<String, Object> mp = new HashMap<>();
        mp.put("board_auth/" + boardId + "/" + userId + "/level", level);
        mp.put("user_boards/" + userId + "/boards/" + boardId, level);
        remoteDb.updateChildren(mp, (error, ref) -> {
            if(error == null){
                remoteMediator.updateBoardUser(userId, level);
            }
            errorCallBack.onClick(error);
        });
    }

    @SuppressLint("UnsafeOptInUsageError")
    public void deleteUser(String userId, @NonNull CallBack<DatabaseError> errorCallBack){
        Map<String, Object> mp = new HashMap<>();
        mp.put("board_auth/" + boardId + "/" + userId, null);
        mp.put("user_boards/" + userId + "/boards/" + boardId, null);
        mp.put("user_boards/" + userId + "/num", ServerValue.increment(-1));
        remoteDb.updateChildren(mp, (error, ref) -> {
            if(error == null){
                remoteMediator.deleteUser(userId);
            }
            errorCallBack.onClick(error);
        });
    }

    @Override
    protected void onCleared() {
        Log.e(TAG, "onCleared: ");
        super.onCleared();
    }
}
