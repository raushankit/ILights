package com.raushankit.ILghts.room;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingSource;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.raushankit.ILghts.model.Notification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class NotificationFetcher {
    private static final String TAG = "RecentNotificationFetch";
    private final String userId;
    private final DatabaseReference db;
    private Disposable disposable;
    private Query query;
    private final NotificationDao notificationDao;

    private final ChildEventListener realTimeListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            Notification notification = snapshot.getValue(Notification.class);
            if(snapshot.getKey() == null || notification == null) {
                Log.i(TAG, "onChildAdded: null notification: " + snapshot);
                return;
            }
            notification.setId(snapshot.getKey());
            BoardRoomDatabase.databaseExecutor.execute(() -> notificationDao.insert(notification));
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            Notification notification = snapshot.getValue(Notification.class);
            if(snapshot.getKey() == null || notification == null) {
                Log.i(TAG, "onChildChanged: null notification: " + snapshot);
                return;
            }
            notification.setId(snapshot.getKey());
            BoardRoomDatabase.databaseExecutor.execute(() -> notificationDao.insert(notification));
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            BoardRoomDatabase.databaseExecutor.execute(() -> notificationDao.deleteNotificationsByIdList(Collections.singletonList(snapshot.getKey())));
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.w(TAG, "onCancelled: ", error.toException());
        }
    };

    public NotificationFetcher(DatabaseReference remoteDb, BoardRoomDatabase database, @NonNull String userId){
        this.userId = userId;
        db = remoteDb
                .child("user_notif")
                .child(userId);
        notificationDao = database.notificationDao();
        getLatestNotifications();
    }

    public PagingSource<Integer, Notification> getSource(){
        return notificationDao.getNotificationsPaging();
    }

    private void getLatestNotifications() {
        disposable = notificationDao.getLatestNotification()
                        .subscribeOn(Schedulers.io())
                                .subscribe(notification -> {
                                    Log.w(TAG, "getLatestNotifications: data = " + notification);
                                    db.orderByChild("time")
                                            .startAfter(notification.getTime(), notification.getId())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    insertIntoDb(snapshot, notification);
                                                    Log.d(TAG, "onDataChange: data = " + snapshot);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Log.w(TAG, "onCancelled: all latest notifications" );
                                                }
                                            });
                                }, throwable -> db.orderByChild("time")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                insertIntoDb(snapshot, null);
                                                Log.d(TAG, "onDataChange: data = " + snapshot);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Log.w(TAG, "onCancelled: all latest notifications" );
                                            }
                                        }));
    }

    private void insertIntoDb(@NonNull DataSnapshot snapshot, @Nullable Notification dbNotification) {
        final List<Notification> notifications = new ArrayList<>();
        snapshot.getChildren()
                .forEach(data -> {
                    Notification notification = data.getValue(Notification.class);
                    if(notification != null && data.getKey() != null){
                        notification.setId(data.getKey());
                        if(query == null) {
                            query = db.orderByChild("time")
                                    .startAfter(notification.getTime(), notification.getId());
                        }
                        notifications.add(notification);
                    } else {
                        Log.w(TAG, "insertIntoDb: notification null");
                    }
                });
        BoardRoomDatabase.databaseExecutor.execute(() -> notificationDao.insert(notifications));
        if(dbNotification != null && query == null) {
            query = db.orderByChild("time")
                    .startAfter(dbNotification.getTime(), dbNotification.getId());
        }
        if(query != null) {
            query.addChildEventListener(realTimeListener);
        }
    }

    public void removeSubscription() {
        disposable.dispose();
        if(query != null) {
            query.removeEventListener(realTimeListener);
        }
        Log.i(TAG, "removeSubscription: removed all subscription");
    }

}
