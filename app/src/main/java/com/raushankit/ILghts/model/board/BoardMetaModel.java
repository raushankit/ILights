package com.raushankit.ILghts.model.board;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

@Keep
@IgnoreExtraProperties
@SuppressWarnings("unused")
public class BoardMetaModel {
    private String title;
    private String description;
    private String visibility;
    private String ownerId;
    private String ownerName;
    private String boardId;
    private Long time;

    public BoardMetaModel() {}

    public BoardMetaModel(String title, String description, String visibility, String ownerId, String ownerName, String boardId, Long time) {
        this.title = title;
        this.description = description;
        this.visibility = visibility;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.boardId = boardId;
        this.time = time;
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

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    @Override
    @NonNull
    public String toString() {
        return "BoardMetaModel{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", visibility='" + visibility + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", boardId='" + boardId + '\'' +
                ", time=" + time +
                '}';
    }
}
