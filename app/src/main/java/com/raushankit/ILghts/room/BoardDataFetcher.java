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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.raushankit.ILghts.entity.ListenerType;
import com.raushankit.ILghts.model.board.BoardCredModel;
import com.raushankit.ILghts.model.board.FavBoard;
import com.raushankit.ILghts.model.room.BoardEditableData;
import com.raushankit.ILghts.model.room.BoardRoomData;
import com.raushankit.ILghts.model.room.BoardRoomUserData;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Single;
import kotlin.Triple;

class BoardDataFetcher {
    private static final String TAG = "BoardDataFetcher";
    private static final int REMOVAL_DELAY = 2000;
    private final MediatorLiveData<List<BoardRoomUserData>> userBoardData = new MediatorLiveData<>();
    private final DatabaseReference db;
    private final BoardDao boardDao;
    private final Handler handler;
    private final UserBoardLiveData userBoardLiveData;
    private final UserBoardCount userBoardCount;
    private final LiveData<List<BoardRoomUserData>> userBoardsList;
    private final Map<String, BoardUpdateLiveData> mp = new HashMap<>();
    private final Map<String, BoardUpdateLiveData> oldMp = new HashMap<>();

    BoardDataFetcher(final DatabaseReference db, final BoardRoomDatabase roomDb, final String userId){
        boardDao = roomDb.boardDao();
        this.db = db;
        handler = new Handler(Looper.getMainLooper());
        userBoardCount = new UserBoardCount(userId);
        userBoardLiveData = new UserBoardLiveData(userId);
        userBoardsList = boardDao.getUserBoards();
        init();
    }

    private void init(){
        Log.i(TAG, "init() called");
        userBoardData.addSource(userBoardCount, count -> {
            Log.w(TAG, "init: no " + count);
            if(count == 0) {
                userBoardData.setValue(Collections.emptyList());
            }
        });
        userBoardData.addSource(userBoardLiveData, stringIntegerPair -> {
            Log.d(TAG, "getData: remote data = " + stringIntegerPair);
            fetchDataSingleTime(stringIntegerPair.component1(), stringIntegerPair.component2(), stringIntegerPair.component3());
        });
        userBoardData.addSource(userBoardsList, dataList -> {
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

    Single<BoardCredModel> getBoardAuthResult(@NonNull String boardId){
        return Single.create(emitter -> db.child("board_cred")
                .child(boardId)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        emitter.onSuccess(task.getResult().getValue(BoardCredModel.class));
                    }else{
                        emitter.onSuccess(new BoardCredModel());
                    }
                }));
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
        userBoardData.removeSource(userBoardCount);
        userBoardData.removeSource(userBoardLiveData);
        userBoardData.removeSource(userBoardsList);
        mp.forEach((k,v) -> {
            userBoardData.removeSource(v);
            v.removeListeners();
        });
        mp.clear();
    }

    public LiveData<List<BoardRoomUserData>> getUserBoardsList() {
        return userBoardData;
    }

    class UserBoardLiveData extends LiveData<Triple<ListenerType,String,Integer>> {
        private boolean isDataReceived;
        private final DatabaseReference userDb;

        private final ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.w(TAG, "onChildAdded: ");
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

        public UserBoardLiveData(@NonNull String userId) {
            userDb = db.child("user_boards")
                    .child(userId)
                    .child("boards");
            Log.i(TAG, "UserBoardLiveData: inside constructor");
            isDataReceived = false;
        }

        @Override
        protected void onActive() {
            Log.d(TAG, "onActive: UserBoardLiveData");
            userDb.addChildEventListener(listener);
        }

        @Override
        protected void onInactive() {
            Log.d(TAG, "onInactive: UserBoardLiveData");
            userDb.removeEventListener(listener);
            deleteAllUserBoards();
            oldMp.forEach((k,v) -> {
                userBoardData.removeSource(v);
                v.removeListeners();
            });
            oldMp.clear();
            oldMp.putAll(mp);
            mp.clear();
            isDataReceived = false;
        }
    }

    class UserBoardCount extends LiveData<Integer> {
        private Integer count;
        private final Query userDb;

        private final ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count = snapshot.getValue() == null? 0 : 1;
                setValue(count);
                Log.w(TAG, "onActive: get method = " + snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "onActive: get method = " + error);
            }
        };

        public UserBoardCount(@NonNull String userId) {
            userDb = db.child("user_boards")
                    .child(userId)
                    .child("boards")
                    .orderByKey()
                    .limitToFirst(1);
            Log.i(TAG, "UserBoardCount: inside constructor");
            count = -1;
        }

        @Override
        protected void onActive() {
            Log.d(TAG, "onActive: UserBoardCount");
            userDb.addValueEventListener(listener);
        }

        @Override
        protected void onInactive() {
            Log.d(TAG, "onInactive: UserBoardCount");
            count = -1;
        }
    }

    class BoardUpdateLiveData extends LiveData<Pair<String, BoardEditableData>> implements Runnable{
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
            Log.i(TAG, "BoardUpdateLiveData() called with: id = [" + id + "]");
            remoteDb = db.child("board_meta")
                    .child(id)
                    .child("data");
        }

        @Override
        protected void onActive() {
            Log.i(TAG, "onActive() called BoardUpdateLiveData");
            if (isRemovalPending) {
                handler.removeCallbacks(this);
            } else {
                remoteDb.addValueEventListener(listener);
            }
            isRemovalPending = false;
        }

        @Override
        protected void onInactive() {
            Log.i(TAG, "onInactive() called BoardUpdateLiveData");
            handler.postDelayed(this, REMOVAL_DELAY);
            isRemovalPending = true;
        }

        public void removeListeners() {
            if (!isRemovalPending) { return; }
            remoteDb.removeEventListener(listener);
            Log.d(TAG, "forced removeListeners: removed listeners BoardUpdateLiveData");
            handler.removeCallbacks(this);
            isRemovalPending = false;
        }

        @Override
        public void run() {
            remoteDb.removeEventListener(listener);
            isRemovalPending = false;
            Log.d(TAG, "run: removing listeners BoardUpdateLiveData");
        }
    }

}
