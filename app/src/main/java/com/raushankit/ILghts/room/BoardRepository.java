package com.raushankit.ILghts.room;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raushankit.ILghts.entity.ListenerType;
import com.raushankit.ILghts.model.board.FavBoard;
import com.raushankit.ILghts.model.room.BoardEditableData;
import com.raushankit.ILghts.model.room.BoardRoomData;
import com.raushankit.ILghts.model.room.BoardRoomUserData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import kotlin.Triple;

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

    public LiveData<List<BoardRoomUserData>> getData() {
        return boardFetcher.getUserBoardsList();
    }

    public void forceCleanBoardUserList(){
        boardFetcher.forceCleanBoardUserList();
    }
}
