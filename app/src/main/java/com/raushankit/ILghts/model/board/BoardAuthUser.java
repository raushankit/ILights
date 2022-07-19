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
@Entity(tableName = "board_members")
public class BoardAuthUser {
    private String email;
    private String name;
    private int level;
    private Long creationTime;

    public BoardAuthUser() {}

    public BoardAuthUser(String email, String name, int level, Long creationTime) {
        this.email = email;
        this.name = name;
        this.level = level;
        this.creationTime = creationTime;
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

    @Exclude
    @Ignore
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoardAuthUser)) return false;
        BoardAuthUser that = (BoardAuthUser) o;
        return level == that.level && Objects.equals(email, that.email) && Objects.equals(name, that.name) && Objects.equals(creationTime, that.creationTime);
    }

    @Exclude
    @Ignore
    @Override
    public int hashCode() {
        return Objects.hash(email, name, level, creationTime);
    }

    @Override
    @NonNull
    public String toString() {
        return "BoardAuthUser{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", level=" + level +
                ", creationTime=" + creationTime +
                '}';
    }

    @Exclude
    @Ignore
    public static final DiffUtil.ItemCallback<BoardAuthUser> DIFF_UTIL = new DiffUtil.ItemCallback<BoardAuthUser>() {
        @Override
        public boolean areItemsTheSame(@NonNull BoardAuthUser oldItem, @NonNull BoardAuthUser newItem) {
            return oldItem.getCreationTime().equals(newItem.getCreationTime());
        }

        @Override
        public boolean areContentsTheSame(@NonNull BoardAuthUser oldItem, @NonNull BoardAuthUser newItem) {
            return oldItem.equals(newItem);
        }
    };
}
