package com.raushankit.ILghts.room;

import androidx.lifecycle.LiveData;
import androidx.paging.PagingSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.raushankit.ILghts.model.Notification;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

@Dao
public abstract class NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(Notification notification);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(List<Notification> notifications);

    @Query("DELETE FROM notification_table")
    public abstract void deleteAll();

    @Query("SELECT * FROM notification_table WHERE id = :id")
    public abstract Notification getNotificationById(String id);

    @Query("DELETE FROM notification_table WHERE id IN (:ids)")
    public abstract void deleteNotificationsByIdList(List<String> ids);

    @Query("SELECT * FROM notification_table ORDER BY time ASC")
    public abstract LiveData<Notification> getNotifications();

    @Query("SELECT * FROM notification_table ORDER BY time ASC")
    public abstract PagingSource<Integer, Notification> getNotificationsPaging();

    @Query("SELECT * FROM notification_table ORDER BY time ASC LIMIT 1")
    public abstract Single<Notification> getLatestNotification();

    @Query("UPDATE notification_table SET seen = :seen WHERE id = :id")
    public abstract void updateSeen(String id, boolean seen);

    @Query("SELECT COUNT(*) FROM notification_table WHERE seen = 0")
    public abstract LiveData<Integer> countUnseen();

    @Query("UPDATE notification_table SET type = :type WHERE id = :id")
    public abstract void updateType(String id, String type);

    @Transaction
    public void updateTypeAndSeen(String id, String type, boolean seen) {
        updateType(id, type);
        updateSeen(id, seen);
    }
}
