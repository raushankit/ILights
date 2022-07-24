package com.raushankit.ILghts.room;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

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

class BoardDataFetcher{
    private static final String TAG = "BoardDataFetcher";
    private static final int REMOVAL_DELAY = 4000;
    private final MediatorLiveData<List<BoardRoomUserData>> userBoardData = new MediatorLiveData<>();
    private final DatabaseReference db;
    private final BoardDao boardDao;
    private final Handler handler;
    private final UserBoardLiveData userBoardLiveData;
    private final LiveData<List<BoardRoomUserData>> userBoardsList;
    private final Map<String, BoardUpdateLiveData> mp = new HashMap<>();
    private final Map<String, BoardUpdateLiveData> oldMp = new HashMap<>();

    BoardDataFetcher(final BoardRoomDatabase roomDb, final String userId){
        boardDao = roomDb.boardDao();
        db = FirebaseDatabase.getInstance()
                .getReference();
        handler = new Handler(Looper.getMainLooper());
        userBoardLiveData = new UserBoardLiveData(userId);
        userBoardsList = boardDao.getUserBoards();
        init();
    }

    private void init(){
        Log.i(TAG, "init() called");
        userBoardData.addSource(userBoardLiveData, stringIntegerPair -> {
            Log.d(TAG, "getData: remote data = " + stringIntegerPair);
            fetchDataSingleTime(stringIntegerPair.component1(), stringIntegerPair.component2(), stringIntegerPair.component3());
        });
        userBoardData.addSource(boardDao.getUserBoards(), dataList -> {
            if(userBoardLiveData.isDataReceived){
                userBoardData.setValue(dataList);
            }else{
                Log.d(TAG, "getData: waiting for the remote data");
            }
        });
    }

    private void fetchDataSingleTime(ListenerType type, @NonNull String id, int level){
        BoardRoomDatabase.databaseExecutor.execute(() -> {
            switch (type){
                case ADDED:
                    BoardRoomData boardRoomData = boardDao.getBoardRoomDataById(id);
                    if(boardRoomData != null){
                        Log.d(TAG, "fetchDataSingleTime: boardRoomData exists = " + boardRoomData);
                        boardDao.insert(new BoardRoomUserData(level, boardRoomData));
                        handler.post(() -> addListenerForUpdate(id));
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
                    BoardDataFetcher.BoardUpdateLiveData updateLiveData = mp.getOrDefault(id, null);
                    if(updateLiveData != null){
                        handler.post(() -> userBoardData.removeSource(updateLiveData));
                    }
                    break;
                default:
                    Log.d(TAG, "fetchDataSingleTime: no suitable type of event");
            }
        });
    }

    private void fetchBoardByIdFromRemote(String boardId, int level, boolean addUpdateListener){
        db.child("board_meta").child(boardId)
                        .get().addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                BoardRoomData data = task.getResult()
                                        .getValue(BoardRoomData.class);
                                if(data != null){
                                    data.setBoardId(boardId);
                                    insert(level, data);
                                    if(addUpdateListener)
                                        addListenerForUpdate(boardId);
                                    Log.i(TAG, "fetchBoardByIdFromRemote: " + data);
                                }
                            }else{
                                Log.i(TAG, "fetchBoardByIdFromRemote: " + task.getException());
                            }
                });
    }

    void insertFavBoard(FavBoard board){
        BoardRoomDatabase.databaseExecutor
                .execute(() -> boardDao.insert(board));
    }

    private void addListenerForUpdate(@NonNull String id){
        if(oldMp.containsKey(id)){
            Log.d(TAG, "oldMp ContainsKey");
            mp.put(id, oldMp.remove(id));
            return;
        }
        BoardUpdateLiveData boardUpdateLiveData = new BoardUpdateLiveData(id);
        userBoardData.addSource(boardUpdateLiveData, stringLongPair -> {
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
        userBoardData.removeSource(userBoardLiveData);
        userBoardData.removeSource(userBoardsList);
        userBoardLiveData.removeListeners();
        mp.forEach((k,v) -> {
            userBoardData.removeSource(v);
            v.removeListeners();
        });
        mp.clear();
    }

    public LiveData<List<BoardRoomUserData>> getUserBoardsList() {
        return userBoardData;
    }

    class UserBoardLiveData extends LiveData<Triple<ListenerType,String,Integer>> implements Runnable{
        private static final String TAG = "UserBoardLiveData";
        private boolean isDataReceived;
        private final DatabaseReference userDb;

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

        public UserBoardLiveData(@NonNull String userId) {
            userDb = db.child("user_boards")
                    .child(userId)
                    .child("boards");
            isDataReceived = false;
        }

        @Override
        protected void onActive() {
            Log.d(TAG, "onActive: ");
            if (isRemovalPending.get()) {
                handler.removeCallbacks(this);
            } else {
                userDb.addChildEventListener(listener);
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
                userDb.removeEventListener(listener);
                isDataReceived = false;
                Log.d(TAG, "forced removeListeners: removed listeners");
                handler.removeCallbacks(this);
                isRemovalPending.set(false);
            }
        }

        @Override
        public void run() {
            userDb.removeEventListener(listener);
            deleteAllUserBoards();
            oldMp.forEach((k,v) -> {
                userBoardData.removeSource(v);
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
        private final DatabaseReference remoteDb;
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
            remoteDb = db.child("board_meta")
                    .child(id)
                    .child("data");
        }

        @Override
        protected void onActive() {
            if (isRemovalPending) {
                handler.removeCallbacks(this);
            } else {
                remoteDb.addValueEventListener(listener);
            }
            isRemovalPending = false;
        }

        @Override
        protected void onInactive() {
            handler.postDelayed(this, REMOVAL_DELAY);
            isRemovalPending = true;
        }

        public void removeListeners() {
            if (!isRemovalPending) { return; }
            remoteDb.removeEventListener(listener);
            Log.d(TAG, "forced removeListeners: removed listeners");
            handler.removeCallbacks(this);
            isRemovalPending = false;
        }

        @Override
        public void run() {
            remoteDb.removeEventListener(listener);
            isRemovalPending = false;
            Log.d(TAG, "run: removing listeners");
        }
    }

}
