package com.raushankit.ILghts.model;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Objects;

@Entity(tableName = "notification_table")
@IgnoreExtraProperties
@Keep
@SuppressWarnings("unused")
public class Notification {

    @NonNull
    @PrimaryKey
    private String id;
    private String body;
    private Long time;
    private String type;

    @Embedded
    private NotificationData data;

    @ColumnInfo(defaultValue = "false")
    private boolean seen;

    @Ignore
    public Notification() {}

    @Ignore
    public Notification(@NonNull String id, String body, Long time, String type, boolean seen) {
        this.id = id;
        this.body = body;
        this.time = time;
        this.type = type;
        this.seen = seen;
    }

    public Notification(@NonNull String id, String body, Long time, String type, NotificationData data, boolean seen) {
        this.id = id;
        this.body = body;
        this.time = time;
        this.type = type;
        this.data = data;
        this.seen = seen;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public NotificationData getData() {
        return data;
    }

    public void setData(NotificationData data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification)) return false;
        Notification that = (Notification) o;
        return seen == that.seen && id.equals(that.id) && Objects.equals(body, that.body) && Objects.equals(time, that.time) && Objects.equals(type, that.type) && Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, body, time, type, data, seen);
    }

    @NonNull
    @Override
    public String toString() {
        return "Notification{" +
                "id='" + id + '\'' +
                ", body='" + body + '\'' +
                ", time=" + time +
                ", type='" + type + '\'' +
                ", data=" + data +
                ", seen=" + seen +
                '}';
    }

    @Ignore
    public static final DiffUtil.ItemCallback<Notification> DIFF_UTIL = new DiffUtil.ItemCallback<Notification>() {
        @Override
        public boolean areItemsTheSame(@NonNull Notification oldItem, @NonNull Notification newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Notification oldItem, @NonNull Notification newItem) {
            return oldItem.equals(newItem);
        }
    };
}
