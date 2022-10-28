package com.raushankit.ILghts.model.room;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Objects;

@SuppressWarnings("unused")
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

    @ColumnInfo(name = "owner_email")
    private String ownerEmail;

    @ColumnInfo(name = "time")
    private Long time;

    @ColumnInfo(name = "last_updated")
    private Long lastUpdated;

    @Ignore
    public BoardRoomData(){
    }

    public BoardRoomData(@NonNull String boardId, BoardEditableData data, String visibility, String ownerId, String ownerName, String ownerEmail, Long time, Long lastUpdated) {
        this.boardId = boardId;
        this.data = data;
        this.visibility = visibility;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.ownerEmail = ownerEmail;
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

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoardRoomData roomData = (BoardRoomData) o;
        return boardId.equals(roomData.boardId) && Objects.equals(data, roomData.data) && Objects.equals(visibility, roomData.visibility) && Objects.equals(ownerId, roomData.ownerId) && Objects.equals(ownerName, roomData.ownerName) && Objects.equals(ownerEmail, roomData.ownerEmail) && Objects.equals(time, roomData.time) && Objects.equals(lastUpdated, roomData.lastUpdated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(boardId, data, visibility, ownerId, ownerName, ownerEmail, time, lastUpdated);
    }

    @NonNull
    @Override
    public String toString() {
        return "BoardRoomData{" +
                "boardId='" + boardId + '\'' +
                ", data=" + data +
                ", visibility='" + visibility + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", ownerEmail='" + ownerEmail + '\'' +
                ", time=" + time +
                ", lastUpdated=" + lastUpdated +
                '}';
    }

    @Exclude
    @Ignore
    public static final DiffUtil.ItemCallback<BoardRoomData> DIFF_UTIL = new DiffUtil.ItemCallback<BoardRoomData>() {
        @Override
        public boolean areItemsTheSame(@NonNull BoardRoomData oldItem, @NonNull BoardRoomData newItem) {
            return oldItem.getBoardId().equals(newItem.getBoardId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull BoardRoomData oldItem, @NonNull BoardRoomData newItem) {
            return oldItem.equals(newItem);
        }
    };
}
