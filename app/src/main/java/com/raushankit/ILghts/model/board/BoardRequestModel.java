package com.raushankit.ILghts.model.board;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import java.util.Objects;

@Keep
@SuppressWarnings("unused")
public class BoardRequestModel {

    private String requesterId;

    private String ownerId;

    private Long timeStamp;

    private int level;

    public BoardRequestModel() {
    }

    public BoardRequestModel(String requesterId, String ownerId, Long timeStamp, int level) {
        this.requesterId = requesterId;
        this.ownerId = ownerId;
        this.timeStamp = timeStamp;
        this.level = level;
    }

    public String getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoardRequestModel that = (BoardRequestModel) o;
        return level == that.level && Objects.equals(requesterId, that.requesterId) && Objects.equals(ownerId, that.ownerId) && Objects.equals(timeStamp, that.timeStamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requesterId, ownerId, timeStamp, level);
    }

    @NonNull
    @Override
    public String toString() {
        return "BoardRequestModel{" +
                "requesterId='" + requesterId + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", timeStamp=" + timeStamp +
                ", level=" + level +
                '}';
    }
}
