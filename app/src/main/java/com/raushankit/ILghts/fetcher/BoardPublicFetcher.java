package com.raushankit.ILghts.fetcher;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.raushankit.ILghts.model.FilterModel;
import com.raushankit.ILghts.model.room.BoardRoomData;
import com.raushankit.ILghts.response.BoardSearchResponse;
import com.raushankit.ILghts.room.BoardDao;
import com.raushankit.ILghts.room.BoardRoomDatabase;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;

public class BoardPublicFetcher {

    private static final String TAG = "BoardPublicFetcher";

    private final BoardDao boardDao;
    private final DatabaseReference db;
    private Query latestQuery;
    private final int pageSize;
    private BoardSearchResponse response;
    private final String userId;
    private boolean gotBoardIds;
    private final AtomicBoolean allow;

    public BoardPublicFetcher(final DatabaseReference db, final BoardRoomDatabase roomDb, final String userId) {
        pageSize = 20;
        this.boardDao = roomDb.boardDao();
        this.db = db;
        this.userId = userId;
        this.gotBoardIds = false;
        allow = new AtomicBoolean(true);
    }

    private Query getQuery(@NonNull FilterModel model) {
        Query query = null;
        Map<String, Boolean> mp = response == null? new LinkedHashMap<>() : response.getUserBoardIds();
        response = new BoardSearchResponse(pageSize, mp);
        if(FilterModel.Type.NULL.equals(model.getType())) {
            query = db.child("board_public")
                    .orderByKey()
                    .limitToFirst(pageSize);
        } else if(FilterModel.Type.FIELD.equals(model.getType())) {
            if(model.getFieldIndex() == 0 || model.getFieldIndex() == 1) {
                query = db.child("board_public")
                        .orderByChild(model.getFieldIndex() == 0? "name": "email")
                        .startAt(model.getValue())
                        .endAt(model.getValue() + "\uf8ff")
                        .limitToFirst(pageSize);
            } else if(model.getFieldIndex() == 2) {
                query = db.child("board_public")
                        .orderByChild(model.getValue().startsWith("Asc")? "time_neg": "time")
                        .limitToFirst(pageSize);
            } else {
                Log.w(TAG, "getNextPageQuery: unknown index: " + model.getFieldIndex());
            }
        }
        return query;
    }

    private Query getNextPageQuery(@NonNull FilterModel model) {
        Query query = null;
        if(FilterModel.Type.NULL.equals(model.getType())) {
            query = db.child("board_public")
                    .orderByKey()
                    .startAfter(response.getLastKey())
                    .limitToFirst(pageSize);
        } else if(FilterModel.Type.FIELD.equals(model.getType())) {
            if(model.getFieldIndex() == 0 || model.getFieldIndex() == 1) {
                query = db.child("board_public")
                        .orderByChild(model.getFieldIndex() == 0? "name": "email")
                        .startAfter(model.getValue(), response.getLastKey())
                        .endAt(model.getValue() + "\uf8ff")
                        .limitToFirst(pageSize);
            } else if(model.getFieldIndex() == 2) {
                query = db.child("board_public")
                        .orderByChild(model.getValue().startsWith("Asc")? "time_neg": "time")
                        .startAfter(model.getValue().startsWith("Asc")
                                ? response.getLastValue().getTime()
                                : response.getLastValue().getTime_neg(), response.getLastKey())
                        .limitToFirst(pageSize);
            } else {
                Log.w(TAG, "getNextPageQuery: unknown index: " + model.getFieldIndex());
            }
        }
        return query;
    }


    public Flowable<BoardSearchResponse> getData(@NonNull FilterModel model) {
        return Flowable.create(emitter -> {
            latestQuery = model.isRetry()? latestQuery: model.isNextPage()? getNextPageQuery(model): getQuery(model);
            Log.e(TAG, "getData: I am here");
            if(response.isEndOfPage() || !allow.compareAndSet(true, false)) {
                if(response.isEndOfPage()) {
                    Log.i(TAG, "getData: already end of page");
                    emitter.onComplete();
                } else {
                    Log.i(TAG, "getData: method running still");
                }
            } else {
                Log.e(TAG, "getData: inside else branch");
                response.increasePage(model.isRetry()? 0: 1);
                response.setLoading(true);
                if(gotBoardIds) {
                    Log.e(TAG, "getData: have board ids");
                    executeQuery(emitter);
                } else {
                    db.child("user_boards").child(userId).child("boards")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Log.e(TAG, "onDataChange: requests children1: " + snapshot);
                                    snapshot.getChildren().forEach(child -> response.setUserBoardIds(child.getKey(), true));
                                    db.child("board_requests").orderByChild("userId").equalTo(userId)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    Log.e(TAG, "onDataChange: requests children2: " + snapshot.hasChildren());
                                                    gotBoardIds = true;
                                                    snapshot.getChildren().forEach(child -> response.setUserBoardIds(child.getKey(), false));
                                                    emitter.onNext(response);
                                                    executeQuery(emitter);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    response.setLoading(false);
                                                    response.setError(error.toException());
                                                    emitter.onNext(response);
                                                    allow.set(true);
                                                }
                                            });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    response.setError(error.toException());
                                    response.setLoading(false);
                                    emitter.onNext(response);
                                    allow.set(true);
                                }
                            });
                }
            }
        }, BackpressureStrategy.BUFFER);
    }

    private void executeQuery(FlowableEmitter<BoardSearchResponse> emitter) {
        latestQuery.get().addOnCompleteListener(task -> {
            Log.w(TAG, "getData: task: " + task.isSuccessful());
            if(task.isSuccessful()) {
                boolean flag = response.processResponse(task.getResult());
                if(flag) {
                    getBoardData(emitter);
                } else {
                    response.setLoading(false);
                    emitter.onNext(response);
                    allow.set(true);
                }
                Log.w(TAG, "getData: resp = " + response);
            } else {
                Log.w(TAG, "getData: error", task.getException());
                response.setError(task.getException());
                response.setLoading(false);
                emitter.onNext(response);
                allow.set(true);
            }
        });
    }

    private void getBoardData(FlowableEmitter<BoardSearchResponse> emitter) {
        BoardRoomDatabase.databaseExecutor.execute(() -> {
            List<BoardRoomData> dbData = boardDao.getBoardRoomDataByIds(
                    new ArrayList<>(response.getCheckMap().keySet())
            );
            Log.w(TAG, "getBoardData: found data for: " + dbData.stream().map(BoardRoomData::getBoardId).collect(Collectors.toList()));
            dbData.forEach(response::evictOnFetch);
            emitter.onNext(response);
            AtomicInteger size = new AtomicInteger(response.getCheckMap().size());
            response.getCheckMap().forEach((k, v) -> db.child("board_meta").child(k)
                    .get().addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            BoardRoomData data = task.getResult()
                                    .getValue(BoardRoomData.class);
                            allow.set(size.decrementAndGet() == 0);
                            response.setLoading(size.get() != 0);
                            if(data != null){
                                data.setBoardId(k);
                                BoardRoomDatabase.databaseExecutor.execute(() -> boardDao.insert(data));
                                response.evictOnFetch(data);
                                emitter.onNext(response);
                                Log.d(TAG, "fetchBoardByIdFromRemote id: " + k);
                            } else {
                                response.getCheckMap().remove(k);
                                if(!response.isLoading()) { emitter.onNext(response); }
                            }
                        }else{
                            Log.d(TAG, "fetchBoardByIdFromRemote: " + task.getException());
                            response.setError(task.getException());
                            response.setLoading(false);
                            emitter.onNext(response);
                        }
                    }));
        });

    }
}
