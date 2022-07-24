package com.raushankit.ILghts.room;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.raushankit.ILghts.model.board.BoardCredModel;
import com.raushankit.ILghts.model.board.FavBoard;
import com.raushankit.ILghts.model.room.BoardRoomUserData;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

public class BoardRepository {

    private static final String TAG = "BoardRepository";
    private static final int REMOVAL_DELAY = 4000;
    private static volatile BoardRepository INSTANCE;
    private final BoardDataFetcher boardFetcher;

    private BoardRepository(Application application) {
        Log.d(TAG, "BoardRepository: private constructor");
        BoardRoomDatabase db = BoardRoomDatabase.getDatabase(application);
        String userId = FirebaseAuth.getInstance().getUid();
        assert userId != null;
        boardFetcher = new BoardDataFetcher(db, userId);
    }

    public static BoardRepository getInstance(Application application) {
        Log.d(TAG, "BoardRepository: new instance");
        if (INSTANCE == null) {
            synchronized (BoardRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BoardRepository(application);
                }
            }
        }
        return INSTANCE;
    }

    public void setFavBoard(FavBoard board){
        boardFetcher.insertFavBoard(board);
    }

    public Single<BoardCredModel> getCredentialData(@NonNull String boardId){
        return boardFetcher.getBoardAuthResult(boardId);
    }

    public LiveData<List<BoardRoomUserData>> getData() {
        return boardFetcher.getUserBoardsList();
    }

    public void forceCleanBoardUserList(){
        boardFetcher.forceCleanBoardUserList();
    }
}
