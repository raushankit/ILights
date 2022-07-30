package com.raushankit.ILghts.room;

import androidx.annotation.NonNull;
import androidx.paging.PagingSource;

import com.google.firebase.database.DatabaseReference;
import com.raushankit.ILghts.model.Notification;

public class NotificationFetcher {
    private static final String TAG = "RecentNotificationFetch";
    private static final int PAGE_SIZE = 60;
    private final String userId;
    private final DatabaseReference db;
    private final BoardRoomDatabase database;
    private final NotificationDao notificationDao;

    public NotificationFetcher(DatabaseReference remoteDb, BoardRoomDatabase database, @NonNull String userId){
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

    public void fetchData(Notification notification){
        db.orderByChild("time")
                .startAfter(notification.getTime(), notification.getId())
                .limitToFirst(PAGE_SIZE)
                .get()
                .addOnCompleteListener(task -> {

                });
    }

}
