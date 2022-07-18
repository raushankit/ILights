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
    private final DatabaseReference remoteDb;
    private final Handler mHandler;
    private final AtomicBoolean addSources = new AtomicBoolean(true);
    private final UserBoardLiveData userBoardLiveData;
    private final LiveData<List<BoardRoomUserData>> userBoardsList;

    private final Map<String, BoardUpdateLiveData> mp = new HashMap<>();
    private final Map<String, BoardUpdateLiveData> oldMp = new HashMap<>();
    private final BoardDao boardDao;
    private final MediatorLiveData<List<BoardRoomUserData>> data;

    private BoardRepository(Application application) {
        Log.d(TAG, "BoardRepository: private constructor");
        BoardRoomDatabase db = BoardRoomDatabase.getDatabase(application);
        remoteDb = FirebaseDatabase.getInstance().getReference();
        String userId = FirebaseAuth.getInstance().getUid();
        assert userId != null;
        mHandler = new Handler(Looper.getMainLooper());
        boardDao = db.boardDao();
        userBoardsList = boardDao.getUserBoards();
        data = new MediatorLiveData<>();
        deleteAllUserBoards();
        userBoardLiveData = new UserBoardLiveData("user_boards/" + userId + "/boards");
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

    public LiveData<List<BoardRoomUserData>> getData() {
        if(addSources.get()){
            data.addSource(userBoardLiveData, stringIntegerPair -> {
                Log.d(TAG, "getData: remote data = " + stringIntegerPair);
                fetchDataSingleTime(stringIntegerPair.component1(), stringIntegerPair.component2(), stringIntegerPair.component3());
            });
            data.addSource(userBoardsList, dataList -> {
                if(userBoardLiveData.isDataReceived){
                    data.postValue(dataList);
                }else{
                    Log.d(TAG, "getData: waiting for the remote data");
                }
            });
            addSources.set(false);
        }
        return data;
    }

    private void fetchDataSingleTime(ListenerType type, @NonNull String id, int level){
        BoardRoomDatabase.databaseExecutor.execute(() -> {
            switch (type){
                case ADDED:
                    BoardRoomData boardRoomData = boardDao.getBoardRoomDataById(id);
                    if(boardRoomData != null){
                        Log.d(TAG, "fetchDataSingleTime: boardRoomData exists = " + boardRoomData);
                        boardDao.insert(new BoardRoomUserData(level, boardRoomData));
                        mHandler.post(() -> addListenerForUpdate(id));
                    }else{
                        Log.d(TAG, "fetchDataSingleTime: boardRoomData doesn't exists fetching from remote db");
                        fetchBoardByIdFromRemote(id, level, true);
                    }
                    break;
                case CHANGED:
                    boardDao.updateUserBoardLevelById(id,level);
                    break;
                case REMOVED:
                    boardDao.deleteUserBoardById(id);
                    BoardUpdateLiveData updateLiveData = mp.getOrDefault(id, null);
                    if(updateLiveData != null){
                        mHandler.post(() -> data.removeSource(updateLiveData));
                    }
                    break;
                default:
                    Log.d(TAG, "fetchDataSingleTime: no suitable type of event");
            }
        });
    }

    private void fetchBoardByIdFromRemote(String boardId, int level, boolean addUpdateListener){
        remoteDb.child("board_meta").child(boardId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                BoardRoomData data1 = snapshot.getValue(BoardRoomData.class);
                if(data1 != null){
                    data1.setBoardId(boardId);
                    insert(level, data1);
                    if(addUpdateListener){
                        addListenerForUpdate(boardId);
                    }
                    Log.d(TAG, "onDataChange: data inserted");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addListenerForUpdate(@NonNull String id){
        if(oldMp.containsKey(id)){
            Log.d(TAG, "oldMp ContainsKey");
            mp.put(id, oldMp.remove(id));
            return;
        }
        BoardUpdateLiveData boardUpdateLiveData = new BoardUpdateLiveData(id);
        data.addSource(boardUpdateLiveData, stringLongPair -> {
            Log.d(TAG, "addListenerForUpdate: " + stringLongPair);
            if(stringLongPair.second == null){
                deleteBoardById(id);
            }else{
                mp.putIfAbsent(id, boardUpdateLiveData);
                updateBoardData(id, stringLongPair.second);
            }
        });
    }


    private void deleteAllUserBoards(){
        BoardRoomDatabase.databaseExecutor.execute(boardDao::deleteAllUserBoards);
    }

    public void insert(BoardRoomData boardRoomData) {
        BoardRoomDatabase.databaseExecutor.execute(() -> boardDao.insert(boardRoomData));
    }

    public void deleteBoardById(String id){
        BoardRoomDatabase.databaseExecutor.execute(() -> boardDao.deleteBoard(id));
    }

    public void updateBoardData(String uid, BoardEditableData data){
        BoardRoomDatabase.databaseExecutor.execute(() -> boardDao.updateBoardWithUserById(uid, data));
    }

    public void insert(int level, BoardRoomData boardRoomData) {
        BoardRoomDatabase.databaseExecutor.execute(() -> boardDao.insertRemoteData(level, boardRoomData));
    }

    public void forceCleanBoardUserList() {
        data.removeSource(userBoardLiveData);
        data.removeSource(userBoardsList);
        userBoardLiveData.removeListeners();
        mp.forEach((k,v) -> {
            data.removeSource(v);
            v.removeListeners();
        });
        mp.clear();
        addSources.set(true);
    }

    class UserBoardLiveData extends LiveData<Triple<ListenerType,String,Integer>> implements Runnable{

        private static final String TAG = "BoardEntityLiveData";
        private boolean isDataReceived;
        private final DatabaseReference db;
        private final Handler handler;

        private final ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                isDataReceived = true;
                setValue(new Triple<>(ListenerType.ADDED, snapshot.getKey(), snapshot.getValue(Integer.class)));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                isDataReceived = true;
                setValue(new Triple<>(ListenerType.CHANGED, snapshot.getKey(), snapshot.getValue(Integer.class)));
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                isDataReceived = true;
                setValue(new Triple<>(ListenerType.REMOVED, snapshot.getKey(), 0));
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "onCancelled: error", error.toException());
            }
        };
        private final AtomicBoolean isRemovalPending = new AtomicBoolean(false);

        public UserBoardLiveData(@NonNull String path) {
            db = FirebaseDatabase.getInstance().getReference(path);
            handler = new Handler();
            isDataReceived = false;
        }

        @Override
        protected void onActive() {
            Log.d(TAG, "onActive: ");
            if (isRemovalPending.get()) {
                handler.removeCallbacks(this);
            } else {
                db.addChildEventListener(listener);
            }
            isRemovalPending.set(false);
        }

        @Override
        protected void onInactive() {
            Log.d(TAG, "onInactive: ");
            handler.postDelayed(this, REMOVAL_DELAY);
            isRemovalPending.set(true);
        }

        public void removeListeners() {
            if (isRemovalPending.get()) {
                db.removeEventListener(listener);
                isDataReceived = false;
                Log.d(TAG, "forced removeListeners: removed listeners");
                handler.removeCallbacks(this);
                isRemovalPending.set(false);
            }
        }

        @Override
        public void run() {
            db.removeEventListener(listener);
            deleteAllUserBoards();
            oldMp.forEach((k,v) -> {
                data.removeSource(v);
                v.removeListeners();
            });
            oldMp.clear();
            oldMp.putAll(mp);
            mp.clear();
            isRemovalPending.set(false);
            isDataReceived = false;
            Log.d(TAG, "run: removing listeners for BoardEntityLiveData old mp = " + oldMp);
        }
    }

    class BoardUpdateLiveData extends LiveData<Pair<String, BoardEditableData>> implements Runnable{
        private static final String TAG = "BoardUpdateLiveData";
        private final DatabaseReference db;
        private final ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                setValue(new Pair<>(snapshot.getKey(), snapshot.getValue(BoardEditableData.class)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "onCancelled: error", error.toException());
            }
        };
        private boolean isRemovalPending = false;

        public BoardUpdateLiveData(@NonNull String id) {
            db = FirebaseDatabase.getInstance().getReference().child("board_meta").child(id).child("data");
        }

        @Override
        protected void onActive() {
            if (isRemovalPending) {
                mHandler.removeCallbacks(this);
            } else {
                db.addValueEventListener(listener);
            }
            isRemovalPending = false;
        }

        @Override
        protected void onInactive() {
            mHandler.postDelayed(this, REMOVAL_DELAY);
            isRemovalPending = true;
        }

        public void removeListeners() {
            if (isRemovalPending) {
                db.removeEventListener(listener);
                Log.d(TAG, "forced removeListeners: removed listeners");
                mHandler.removeCallbacks(this);
                isRemovalPending = false;
            }
        }

        @Override
        public void run() {
            db.removeEventListener(listener);
            isRemovalPending = false;
            Log.d(TAG, "run: removing listeners");
        }
    }

}
