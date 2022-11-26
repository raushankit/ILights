package com.raushankit.ILghts.room;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.raushankit.ILghts.fetcher.BoardDataFetcher;
import com.raushankit.ILghts.fetcher.BoardPublicFetcher;
import com.raushankit.ILghts.fetcher.BoardSearchUserFetcher;
import com.raushankit.ILghts.model.FilterModel;
import com.raushankit.ILghts.model.board.BoardCredModel;
import com.raushankit.ILghts.model.board.BoardSearchUserModel;
import com.raushankit.ILghts.model.board.FavBoard;
import com.raushankit.ILghts.model.room.BoardRoomUserData;
import com.raushankit.ILghts.response.BoardSearchResponse;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public class BoardRepository {
    private static final String TAG = "BoardRepository";

    private static volatile BoardRepository INSTANCE;
    private final BoardDataFetcher boardFetcher;
    private final BoardPublicFetcher boardPublicFetcher;
    private final BoardSearchUserFetcher boardSearchUserFetcher;

    private BoardRepository(Application application) {
        Log.d(TAG, "BoardRepository: private constructor");
        BoardRoomDatabase roomDatabase = BoardRoomDatabase.getDatabase(application);
        String userId = FirebaseAuth.getInstance().getUid();
        assert userId != null;
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        boardFetcher = new BoardDataFetcher(db, roomDatabase, userId);
        boardSearchUserFetcher = new BoardSearchUserFetcher(db);
        boardPublicFetcher = new BoardPublicFetcher(db, roomDatabase, userId);
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

    public Single<List<BoardSearchUserModel>> getSearchableUsersByQuery(@NonNull String boardId, @NonNull String query){
        return boardSearchUserFetcher.getSearchableUsersByQuery(boardId, query);
    }

    public Single<Integer> addUsersToBoard(Map<String, Object> mp){
        return boardSearchUserFetcher.putUsersInBoard(mp);
    }

    public LiveData<List<BoardRoomUserData>> getData() {
        return boardFetcher.getUserBoardsList();
    }

    public void forceCleanBoardUserList(){
        boardFetcher.forceCleanBoardUserList();
    }

    public Flowable<BoardSearchResponse> getPublicBoards(@NonNull FilterModel model) {
        return boardPublicFetcher.getData(model);
    }
}
