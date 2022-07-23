package com.raushankit.ILghts.model.board;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Objects;

@Keep
@IgnoreExtraProperties
@SuppressWarnings("unused")
@Entity(tableName = "board_members"
        , primaryKeys = {"userId", "boardId"})
public class BoardAuthUser {
    @NonNull private String userId;
    private String email;
    private String name;
    private int level;
    private Long creationTime;
    @NonNull private String boardId;

    @Ignore
    public BoardAuthUser() {}

    @Ignore
    public BoardAuthUser(String email, String name, int level, Long creationTime) {
        this.email = email;
        this.name = name;
        this.level = level;
        this.creationTime = creationTime;
    }

    public BoardAuthUser(@NonNull String userId, String email, String name, int level, Long creationTime, @NonNull String boardId) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.level = level;
        this.creationTime = creationTime;
        this.boardId = boardId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Long creationTime) {
        this.creationTime = creationTime;
    }

    @NonNull
    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(@NonNull String boardId) {
        this.boardId = boardId;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    @Exclude
    @Ignore
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoardAuthUser)) return false;
        BoardAuthUser that = (BoardAuthUser) o;
        return level == that.level && userId.equals(that.userId) && Objects.equals(email, that.email) && Objects.equals(name, that.name) && Objects.equals(creationTime, that.creationTime) && boardId.equals(that.boardId);
    }

    @Exclude
    @Ignore
    @Override
    public int hashCode() {
        return Objects.hash(userId, email, name, level, creationTime, boardId);
    }

    @Exclude
    @Ignore
    @NonNull
    @Override
    public String toString() {
        return "BoardAuthUser{" +
                ", userId='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", level=" + level +
                ", creationTime=" + creationTime +
                ", boardId='" + boardId + '\'' +
                '}';
    }

    @Exclude
    @Ignore
    public static final DiffUtil.ItemCallback<BoardAuthUser> DIFF_UTIL = new DiffUtil.ItemCallback<BoardAuthUser>() {
        @Override
        public boolean areItemsTheSame(@NonNull BoardAuthUser oldItem, @NonNull BoardAuthUser newItem) {
            return oldItem.getUserId().equals(newItem.getUserId()) && oldItem.getBoardId().equals(newItem.getBoardId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull BoardAuthUser oldItem, @NonNull BoardAuthUser newItem) {
            return oldItem.equals(newItem);
        }
    };
}
