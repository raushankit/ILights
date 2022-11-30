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
    private String userName;
    private String userEmail;
    private String boardName;

    public NotificationData() {}

    public NotificationData(String boardId, String userId, String userName, String userEmail, String boardName) {
        this.boardId = boardId;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.boardName = boardName;
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

    public String getUserName() {
        return userName;
    }

    public String getBoardName() {
        return boardName;
    }

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationData that = (NotificationData) o;
        return Objects.equals(boardId, that.boardId) && Objects.equals(userId, that.userId) && Objects.equals(userName, that.userName) && Objects.equals(userEmail, that.userEmail) && Objects.equals(boardName, that.boardName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(boardId, userId, userName, userEmail, boardName);
    }

    @NonNull
    @Override
    public String toString() {
        return "NotificationData{" +
                "boardId='" + boardId + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", boardName='" + boardName + '\'' +
                '}';
    }
}
