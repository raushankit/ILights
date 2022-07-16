package com.raushankit.ILghts.model.room;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Keep
@Entity(tableName = "board_table")
public class BoardRoomData {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String boardId;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "visibility")
    private String visibility;

    @ColumnInfo(name = "owner_id")
    private String ownerId;

    @ColumnInfo(name = "owner_name")
    private String ownerName;

    @ColumnInfo(name = "time")
    private Long time;

    @ColumnInfo(name = "last_updated")
    private Long lastUpdated;

    public BoardRoomData(){
    }

    public BoardRoomData(@NonNull String boardId, String title, String description, String visibility, String ownerId, String ownerName, Long time, Long lastUpdated) {
        this.boardId = boardId;
        this.title = title;
        this.description = description;
        this.visibility = visibility;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.time = time;
        this.lastUpdated = lastUpdated;
    }

    @NonNull
    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(@NonNull String boardId) {
        this.boardId = boardId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoardRoomData)) return false;
        BoardRoomData that = (BoardRoomData) o;
        return boardId.equals(that.boardId) && Objects.equals(title, that.title) && Objects.equals(description, that.description) && Objects.equals(visibility, that.visibility) && Objects.equals(ownerId, that.ownerId) && Objects.equals(ownerName, that.ownerName) && Objects.equals(time, that.time) && Objects.equals(lastUpdated, that.lastUpdated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(boardId, title, description, visibility, ownerId, ownerName, time, lastUpdated);
    }

    @Override
    @NonNull
    public String toString() {
        return "BoardRoomData{" +
                "boardId='" + boardId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", visibility='" + visibility + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", time=" + time +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}
