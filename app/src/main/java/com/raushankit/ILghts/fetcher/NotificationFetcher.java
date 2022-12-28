package com.raushankit.ILghts.fetcher;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.paging.PagingSource;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.raushankit.ILghts.entity.NotificationType;
import com.raushankit.ILghts.model.Notification;
import com.raushankit.ILghts.room.BoardRoomDatabase;
import com.raushankit.ILghts.room.NotificationDao;
import com.raushankit.ILghts.utils.StringUtils;
import com.raushankit.ILghts.utils.callbacks.CallBack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class NotificationFetcher {
    private static final String TAG = "RecentNotificationFetch";
    private final String userId;
    private final DatabaseReference db;
    private Disposable disposable;
    private Query query;
    private final NotificationDao notificationDao;
    public static final String NOTIFICATION_ACTION_USER = "Your access request for board (%s) was %s";
    public static final String NOTIFICATION_ACTION_ADMIN = "%s (%s) %s as %s to board %s.";

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
        db = remoteDb;
        notificationDao = database.notificationDao();
        getLatestNotifications();
    }

    public PagingSource<Integer, Notification> getSource(){
        return notificationDao.getNotificationsPaging();
    }

    public void updateSeenStatus(Notification notification) {
        if(!notification.isSeen()) {
            BoardRoomDatabase.databaseExecutor.execute(() -> notificationDao.updateSeen(notification.getId(), true));
        }
    }

    private void getLatestNotifications() {
        disposable = notificationDao.getLatestNotification()
                        .subscribeOn(Schedulers.io())
                                .subscribe(notification -> {
                                    Log.w(TAG, "getLatestNotifications: data = " + notification);
                                    db.child("user_notif").child(userId).orderByChild("time")
                                            .endBefore(notification.getTime(), notification.getId())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    insertIntoDb(snapshot, notification);
                                                    Log.d(TAG, "onDataChange: data[remote] = " + snapshot);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Log.w(TAG, "onCancelled: all latest notifications" );
                                                }
                                            });
                                }, throwable -> db.child("user_notif").child(userId).orderByChild("time")
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
                            query = db.child("user_notif").child(userId).orderByChild("time")
                                    .endBefore(notification.getTime(), notification.getId());
                        }
                        notifications.add(notification);
                    } else {
                        Log.w(TAG, "insertIntoDb: notification null");
                    }
                });
        BoardRoomDatabase.databaseExecutor.execute(() -> notificationDao.insert(notifications));
        if(dbNotification != null && query == null) {
            query = db.child("user_notif").child(userId).orderByChild("time")
                    .endBefore(dbNotification.getTime(), dbNotification.getId());
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

    public void requestAction(Notification notification, boolean flag,  CallBack<String> callBack) {
        Log.d(TAG, "requestAction: notification:: " + notification);
        Map<String, Object> mp = new HashMap<>();
        long timestamp = StringUtils.TIMESTAMP();
        int level = NotificationType.ACTION_EDITOR_REQUEST.equals(notification.getType()) || NotificationType.ACTION_USER_PROMOTE.equals(notification.getType())? 2: 1;
        String key = "board_auth/" + notification.getData().getBoardId() + "/" + notification.getData().getUserId();
        if(flag) {
            mp.put(key + "/name", notification.getData().getUserName().toLowerCase(Locale.getDefault()));
            mp.put(key + "/email", notification.getData().getUserEmail().toLowerCase(Locale.getDefault()));
            mp.put(key + "/level", level);
            mp.put(key + "/creationTime", timestamp);
            mp.put("user_boards/" + notification.getData().getUserId() + "/boards/" + notification.getData().getBoardId(), level);
            mp.put("user_boards/" + notification.getData().getUserId() + "/num", ServerValue.increment(NotificationType.ACTION_EDITOR_REQUEST.equals(notification.getType()) || NotificationType.ACTION_USER_REQUEST.equals(notification.getType())? 1: 0));
        }
        key = "user_notif/" + notification.getData().getUserId() + "/" + UUID.randomUUID().toString();
        mp.put(key + "/body", String.format(NOTIFICATION_ACTION_USER,
                notification.getData().getBoardName(), flag? "accepted": "rejected"));
        mp.put(key + "/time", -1* timestamp);
        mp.put(key + "/type", NotificationType.TEXT);
        key = "user_notif/" + userId + "/" + UUID.randomUUID().toString();
        mp.put("user_notif/" + userId + "/" + notification.getId() + "/type", NotificationType.ACTION_DONE);
        mp.put(key + "/body", String.format(NOTIFICATION_ACTION_ADMIN, flag? "Accepted": "Rejected",
                notification.getData().getUserName(), notification.getData().getUserEmail(),
                level == 1? "user": "editor", notification.getData().getBoardName()));
        mp.put(key + "/time", -1* timestamp);
        mp.put(key + "/type", NotificationType.TEXT);
        mp.put("board_requests/" + notification.getData().getUserId() + "/" + notification.getData().getBoardId(), null);
        db.updateChildren(mp, (error, ref) -> {
            callBack.onClick(error == null? null: error.getMessage());
            if(error == null) {
                BoardRoomDatabase.databaseExecutor.execute(() -> notificationDao.updateTypeAndSeen(notification.getId(), NotificationType.ACTION_DONE, true));
            }
        });
    }

    public void updateAllSeen() {
        BoardRoomDatabase.databaseExecutor.execute(notificationDao::updateAllSeen);
    }

    public LiveData<Integer> countUnseenNotifications() {
        return notificationDao.countUnseen();
    }
}
