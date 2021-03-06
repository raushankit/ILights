package com.raushankit.ILghts.model.room;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Objects;

@Keep
@IgnoreExtraProperties
@Entity(tableName = "board_table")
public class BoardRoomData {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String boardId;

    @Embedded
    private BoardEditableData data;

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

    @Ignore
    public BoardRoomData(){
    }

    public BoardRoomData(@NonNull String boardId, BoardEditableData data, String visibility, String ownerId, String ownerName, Long time, Long lastUpdated) {
        this.boardId = boardId;
        this.data = data;
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

    public BoardEditableData getData() {
        return data;
    }

    public void setData(BoardEditableData data) {
        this.data = data;
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
        return boardId.equals(that.boardId) && Objects.equals(data, that.data) && Objects.equals(visibility, that.visibility) && Objects.equals(ownerId, that.ownerId) && Objects.equals(ownerName, that.ownerName) && Objects.equals(time, that.time) && Objects.equals(lastUpdated, that.lastUpdated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(boardId, data, visibility, ownerId, ownerName, time, lastUpdated);
    }

    @Override
    @NonNull
    public String toString() {
        return "BoardRoomData{" +
                "boardId='" + boardId + '\'' +
                ", data=" + data +
                ", visibility='" + visibility + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", time=" + time +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}
