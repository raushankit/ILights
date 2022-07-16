package com.raushankit.ILghts.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.raushankit.ILghts.model.room.BoardRoomData;

import java.util.List;

@Dao
public interface BoardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(BoardRoomData data);

    @Query("DELETE FROM board_table")
    void deleteAll();

    @Query("SELECT * FROM board_table ORDER BY id ASC")
    LiveData<List<BoardRoomData>> getBoards();

    @Query("SELECT EXISTS(SELECT * FROM board_table WHERE id = :id)")
    boolean doesBoardExist(String id);
}
