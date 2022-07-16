package com.raushankit.ILghts.room;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.raushankit.ILghts.model.room.BoardRoomData;
import com.raushankit.ILghts.observer.BoardRoomIdLivedata;

import java.util.List;
import java.util.Map;

public class BoardRepository {

    private static volatile BoardRepository INSTANCE;
    private final DatabaseReference remoteDb;
    private final String userId;
    private final BoardRoomIdLivedata boardRoomIdLivedata;

    private final BoardDao boardDao;
    private final LiveData<List<BoardRoomData>> data;

    private BoardRepository(Application application) {
        BoardRoomDatabase db = BoardRoomDatabase.getDatabase(application);
        remoteDb = FirebaseDatabase.getInstance().getReference();
        userId = FirebaseAuth.getInstance().getUid();
        boardDao = db.boardDao();
        data = boardDao.getBoards();
        assert userId != null;
        boardRoomIdLivedata = new BoardRoomIdLivedata(userId);
    }

    public static BoardRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (BoardRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BoardRepository(application);
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<List<BoardRoomData>> getData() {

        return data;
    }

    public void insert(BoardRoomData boardRoomData) {
        BoardRoomDatabase.databaseWriteExecutor.execute(() -> boardDao.insert(boardRoomData));
    }

    public LiveData<Map<String, Integer>> getBoardIds() {
        return boardRoomIdLivedata;
    }

}
