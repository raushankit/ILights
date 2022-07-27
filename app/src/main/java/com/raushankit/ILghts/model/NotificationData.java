package com.raushankit.ILghts.model;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Objects;

@IgnoreExtraProperties
@Keep
@SuppressWarnings("unused")
public class NotificationData {

    private String boardId;
    private String userId;

    public NotificationData() {}

    public NotificationData(String boardId, String userId) {
        this.boardId = boardId;
        this.userId = userId;
    }

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NotificationData)) return false;
        NotificationData that = (NotificationData) o;
        return Objects.equals(boardId, that.boardId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(boardId, userId);
    }

    @NonNull
    @Override
    public String toString() {
        return "NotificationData{" +
                "boardId='" + boardId + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }


}
