package com.raushankit.ILghts.room;

import androidx.lifecycle.LiveData;
import androidx.paging.PagingSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.raushankit.ILghts.model.Notification;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Notification notification);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Notification> notifications);

    @Query("DELETE FROM notification_table")
    void deleteAll();

    @Query("SELECT * FROM notification_table WHERE id = :id")
    Notification getNotificationById(String id);

    @Query("DELETE FROM notification_table WHERE id IN (:ids)")
    void deleteNotificationsByIdList(List<String> ids);

    @Query("SELECT * FROM notification_table ORDER BY time ASC")
    LiveData<Notification> getNotifications();

    @Query("SELECT * FROM notification_table ORDER BY time ASC")
    PagingSource<Integer, Notification> getNotificationsPaging();

    @Query("SELECT * FROM notification_table ORDER BY time ASC LIMIT 1")
    Single<Notification> getLatestNotification();

    @Query("UPDATE notification_table SET seen = :seen WHERE id = :id")
    void updateSeen(String id, boolean seen);
}
