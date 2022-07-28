package com.raushankit.ILghts.room;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.LoadType;
import androidx.paging.PagingConfig;
import androidx.paging.PagingSource;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxRemoteMediator;

import com.google.firebase.database.DatabaseReference;
import com.raushankit.ILghts.model.Notification;
import com.raushankit.ILghts.response.NotificationResponse;

import java.io.IOException;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

@SuppressLint("UnsafeOptInUsageError")
public class NotificationRemoteMediator extends RxRemoteMediator<Integer, Notification> {
    private static final String TAG = "NotificationRemoteMedia";
    private final String userId;
    private final DatabaseReference db;
    private final BoardRoomDatabase database;
    private final NotificationDao notificationDao;

    public NotificationRemoteMediator(DatabaseReference remoteDb, BoardRoomDatabase database, @NonNull String userId){
        this.userId = userId;
        this.database = database;
        db = remoteDb
                .child("user_notif")
                .child(userId);
        notificationDao = database.notificationDao();
    }

    public PagingSource<Integer, Notification> getSource(){
        return notificationDao.getNotificationsPaging();
    }

    @NonNull
    @Override
    public Single<MediatorResult> loadSingle(@NonNull LoadType loadType, @NonNull PagingState<Integer, Notification> state) {
        Long loadKey = null;
        PagingConfig config = state.getConfig();
        Log.d(TAG, "loadSingle() called with: loadType = [" + loadType + "], state = [" + state + "]");
        switch (loadType) {
            case REFRESH:
                break;
            case PREPEND:
                return Single.just(new MediatorResult.Success(true));
            case APPEND:
                Notification lastItem = state.lastItemOrNull();
                if (lastItem == null) {
                    return Single.just(new MediatorResult.Success(true));
                }
                loadKey = lastItem.getTime();
                break;
        }
        return getData(loadKey==null? Long.MIN_VALUE: loadKey, loadKey == null? config.initialLoadSize: config.pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map((Function<NotificationResponse, MediatorResult>) response -> {
                    Log.i(TAG, "loadSingle: response = " + response);
                    database.runInTransaction(() -> {
                        if(loadType == LoadType.REFRESH){
                            notificationDao.deleteNotificationsByIdList(response.getIds());
                        }
                        notificationDao.insert(response.getList());
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

    private Single<NotificationResponse> getData(Long time, int num){
        Log.i(TAG, "getData() called with: time = [" + time + "], num = [" + num + "]");
        return Single.create(emitter -> db.orderByChild("time")
                .startAfter(time)
                .limitToFirst(num)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        emitter.onSuccess(new NotificationResponse(num, task.getResult()));
                        Log.i(TAG, "getData: success ");
                    }else{
                        Log.i(TAG, "getData: error " + task.getException());
                        emitter.onError(task.getException());
                    }
                }));
    }
}
