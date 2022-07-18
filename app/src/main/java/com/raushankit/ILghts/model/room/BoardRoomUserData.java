package com.raushankit.ILghts.model.room;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Keep
@Entity(tableName = "board_user_table")
public class BoardRoomUserData {

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

    @ColumnInfo(name = "access_level")
    private int accessLevel;

    @Ignore
    public BoardRoomUserData(){}

    public BoardRoomUserData(@NonNull String boardId, BoardEditableData data, String visibility, String ownerId, String ownerName, Long time, Long lastUpdated, int accessLevel) {
        this.boardId = boardId;
        this.data = data;
        this.visibility = visibility;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.time = time;
        this.lastUpdated = lastUpdated;
        this.accessLevel = accessLevel;
    }

    @Ignore
    public BoardRoomUserData(int accessLevel, BoardRoomData boardRoomData){
        this.boardId = boardRoomData.getBoardId();
        this.data = boardRoomData.getData();
        this.visibility = boardRoomData.getVisibility();
        this.ownerId = boardRoomData.getOwnerId();
        this.ownerName = boardRoomData.getOwnerName();
        this.time = boardRoomData.getTime();
        this.lastUpdated = boardRoomData.getLastUpdated();
        this.accessLevel = accessLevel;
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

    public int getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(int accessLevel) {
        this.accessLevel = accessLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoardRoomUserData)) return false;
        BoardRoomUserData that = (BoardRoomUserData) o;
        return accessLevel == that.accessLevel && boardId.equals(that.boardId) && Objects.equals(data, that.data) && Objects.equals(visibility, that.visibility) && Objects.equals(ownerId, that.ownerId) && Objects.equals(ownerName, that.ownerName) && Objects.equals(time, that.time) && Objects.equals(lastUpdated, that.lastUpdated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(boardId, data, visibility, ownerId, ownerName, time, lastUpdated, accessLevel);
    }

    @NonNull
    @Override
    public String toString() {
        return "BoardRoomUserData{" +
                "boardId='" + boardId + '\'' +
                ", data=" + data +
                ", visibility='" + visibility + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", time=" + time +
                ", lastUpdated=" + lastUpdated +
                ", accessLevel=" + accessLevel +
                '}';
    }

    @Ignore
    public static final DiffUtil.ItemCallback<BoardRoomUserData> DIFF_CALLBACK = new DiffUtil.ItemCallback<BoardRoomUserData>() {
        @Override
        public boolean areItemsTheSame(@NonNull BoardRoomUserData oldItem, @NonNull BoardRoomUserData newItem) {
            return oldItem.getBoardId().equals(newItem.getBoardId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull BoardRoomUserData oldItem, @NonNull BoardRoomUserData newItem) {
            return oldItem.equals(newItem);
        }
    };
}
