package com.raushankit.ILghts.model.board;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Keep
@Entity(tableName = "favourite_boards")
public class FavBoard {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id", defaultValue = "")
    private String boardId;

    @ColumnInfo(name = "favourite")
    private boolean fav;

    @Ignore
    public FavBoard() {}

    public FavBoard(@NonNull String boardId, boolean fav) {
        this.boardId = boardId;
        this.fav = fav;
    }

    @NonNull
    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(@NonNull String boardId) {
        this.boardId = boardId;
    }

    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }
}

