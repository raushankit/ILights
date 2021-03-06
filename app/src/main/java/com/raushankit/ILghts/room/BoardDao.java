package com.raushankit.ILghts.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.raushankit.ILghts.model.board.FavBoard;
import com.raushankit.ILghts.model.room.BoardEditableData;
import com.raushankit.ILghts.model.room.BoardRoomData;
import com.raushankit.ILghts.model.room.BoardRoomUserData;

import java.util.List;

@Dao
public abstract class BoardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insert(BoardRoomData data);

    @Query("DELETE FROM board_table")
    abstract void deleteAllBoards();

    @Query("UPDATE board_table SET title = :title, description = :description WHERE id = :id")
    abstract void updateBoardById(String id, String title, String description);

    @Query("SELECT * FROM board_table ORDER BY id ASC")
    abstract LiveData<List<BoardRoomData>> getBoards();

    @Query("SELECT * FROM board_table WHERE id = :id LIMIT 1")
    abstract BoardRoomData getBoardRoomDataById(String id);

    @Query("SELECT last_updated FROM board_table WHERE id = :id")
    abstract Long getLastUpdateTimeById(String id);

    //

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insert(BoardRoomUserData data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insert(List<BoardRoomUserData> dataList);

    @Query("UPDATE board_user_table SET title = :title, description = :description WHERE id = :id")
    abstract void updateBoardUserById(String id, String title, String description);

    @Query("DELETE FROM board_user_table")
    abstract void deleteAllUserBoards();

    @Query("DELETE FROM board_table WHERE id = :id")
    abstract void deleteBoardById(String id);

    @Query("DELETE FROM board_user_table WHERE id = :id")
    abstract void deleteUserBoardById(String id);

    @Query("UPDATE board_user_table SET access_level = :level WHERE id = :id")
    abstract void updateUserBoardLevelById(String id, int level);

    @Query("SELECT board_user_table.*, favourite_boards.favourite " +
            "FROM board_user_table LEFT JOIN favourite_boards " +
            "ON board_user_table.id=favourite_boards.id " +
            "ORDER BY favourite_boards.favourite DESC, " +
            "board_user_table.access_level DESC, " +
            "board_user_table.title ASC")
    abstract LiveData<List<BoardRoomUserData>> getUserBoards();

    //
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insert(FavBoard data);

    @Query("DELETE FROM favourite_boards")
    abstract void deleteAllFavBoards();

    @Transaction
    void insertRemoteData(int level, BoardRoomData roomData){
        insert(roomData);
        insert(new BoardRoomUserData(level, roomData));
    }

    @Transaction
    void updateBoardWithUserById(String id, BoardEditableData data){
        updateBoardById(id, data.getTitle(), data.getDescription());
        updateBoardUserById(id, data.getTitle(), data.getDescription());
    }

    @Transaction
    void deleteBoard(String id){
        deleteBoardById(id);
        deleteUserBoardById(id);
    }

}
