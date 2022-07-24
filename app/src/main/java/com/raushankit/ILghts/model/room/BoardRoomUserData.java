package com.raushankit.ILghts.model.room;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.database.Exclude;

import java.util.Objects;

@SuppressWarnings("unused")
@Keep
@Entity(tableName = "board_user_table")
public class BoardRoomUserData implements Parcelable {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String boardId;

    @ColumnInfo(name = "favourite", defaultValue = "false")
    private boolean fav;

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

    public BoardRoomUserData(@NonNull String boardId, boolean fav,  BoardEditableData data, String visibility, String ownerId, String ownerName, Long time, Long lastUpdated, int accessLevel) {
        this.boardId = boardId;
        this.fav = fav;
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
        this.fav = false;
        this.data = boardRoomData.getData();
        this.visibility = boardRoomData.getVisibility();
        this.ownerId = boardRoomData.getOwnerId();
        this.ownerName = boardRoomData.getOwnerName();
        this.time = boardRoomData.getTime();
        this.lastUpdated = boardRoomData.getLastUpdated();
        this.accessLevel = accessLevel;
    }

    @Ignore
    protected BoardRoomUserData(Parcel in) {
        boardId = in.readString();
        fav = in.readByte() != 0;
        data = in.readParcelable(BoardEditableData.class.getClassLoader());
        visibility = in.readString();
        ownerId = in.readString();
        ownerName = in.readString();
        if (in.readByte() == 0) {
            time = null;
        } else {
            time = in.readLong();
        }
        if (in.readByte() == 0) {
            lastUpdated = null;
        } else {
            lastUpdated = in.readLong();
        }
        accessLevel = in.readInt();
    }

    @Ignore
    @Exclude
    public static final Creator<BoardRoomUserData> CREATOR = new Creator<BoardRoomUserData>() {
        @Override
        public BoardRoomUserData createFromParcel(Parcel in) {
            return new BoardRoomUserData(in);
        }

        @Override
        public BoardRoomUserData[] newArray(int size) {
            return new BoardRoomUserData[size];
        }
    };

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

    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoardRoomUserData)) return false;
        BoardRoomUserData that = (BoardRoomUserData) o;
        return fav == that.fav && accessLevel == that.accessLevel && boardId.equals(that.boardId) && Objects.equals(data, that.data) && Objects.equals(visibility, that.visibility) && Objects.equals(ownerId, that.ownerId) && Objects.equals(ownerName, that.ownerName) && Objects.equals(time, that.time) && Objects.equals(lastUpdated, that.lastUpdated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(boardId, fav, data, visibility, ownerId, ownerName, time, lastUpdated, accessLevel);
    }

    @NonNull
    @Override
    public String toString() {
        return "BoardRoomUserData{" +
                "boardId='" + boardId + '\'' +
                ", fav=" + fav +
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
    @Exclude
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(boardId);
        parcel.writeByte((byte) (fav ? 1 : 0));
        parcel.writeParcelable(data, i);
        parcel.writeString(visibility);
        parcel.writeString(ownerId);
        parcel.writeString(ownerName);
        if (time == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(time);
        }
        if (lastUpdated == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(lastUpdated);
        }
        parcel.writeInt(accessLevel);
    }
}
