package com.raushankit.ILghts.room;

import androidx.lifecycle.LiveData;
import androidx.paging.PagingSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.raushankit.ILghts.model.board.BoardAuthUser;

import java.util.List;

@Dao
public interface BoardMemberDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(BoardAuthUser user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<BoardAuthUser> users);

    @Query("DELETE FROM board_members")
    void deleteAll();

    @Query("SELECT * FROM board_members WHERE boardId = :boardId ORDER BY creationTime ASC")
    List<BoardAuthUser> getPagingMembersListByBoardId(String boardId);

    @Query("DELETE FROM board_members WHERE boardId = :boardId AND userId IN (:userIds)")
    void deleteUsersByBoardIdList(String boardId, List<String> userIds);

    @Query("DELETE FROM board_members WHERE boardId = :boardId")
    void deleteUsersByBoardId(String boardId);

    @Query("UPDATE board_members SET level = :level WHERE boardId = :boardId AND userId = :userId")
    void updateUserLevelByBoardIdAndUserId(String boardId, String userId, int level);

    @Query("DELETE FROM board_members WHERE boardId = :boardId AND userId = :userId")
    void deleteUsersByBoardIdAndUserId(String boardId, String userId);

    @Query("SELECT * FROM board_members WHERE boardId = :boardId")
    LiveData<BoardAuthUser> getMembersByBoardId(String boardId);

    @Query("SELECT * FROM board_members WHERE boardId = :boardId ORDER BY creationTime ASC")
    PagingSource<Integer, BoardAuthUser> getPagingMembersByBoardId(String boardId);
}
