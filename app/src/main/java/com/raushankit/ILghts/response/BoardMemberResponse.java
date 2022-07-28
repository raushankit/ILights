package com.raushankit.ILghts.response;

import android.util.Log;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.raushankit.ILghts.model.board.BoardAuthUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Keep
public class BoardMemberResponse {
    private final List<BoardAuthUser> list;
    private final List<String> userIds;
    private final Long nextKey;
    private final Long currentKey;

    public BoardMemberResponse(String boardId, int totalItem, @NonNull DataSnapshot snapshot) {
        list = new ArrayList<>();
        userIds = new ArrayList<>();
        snapshot.getChildren()
                .forEach(data -> {
                    BoardAuthUser user = data.getValue(BoardAuthUser.class);
                    if (user != null) {
                        user.setBoardId(boardId);
                        user.setUserId(Objects.requireNonNull(data.getKey()));
                        user.setEmail(user.getEmail());
                        userIds.add(data.getKey());
                        list.add(user);
                    }
                });
        if (list.size() < totalItem) {
            nextKey = null;
        } else {
            Log.e("BoardNEXT_KEY", "BoardMemberResponse: " + list.get(totalItem - 1));
            nextKey = list.get(totalItem - 1).getCreationTime();
        }
        if(list.isEmpty()){
            currentKey = null;
        }else{
            currentKey = list.get(0).getCreationTime();
        }
    }

    public List<BoardAuthUser> getList() {
        return list;
    }

    public Long getNextKey() {
        return nextKey;
    }

    public Long getCurrentKey() {
        return currentKey;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    @Override
    @NonNull
    public String toString() {
        return "BoardMemberResponse{" +
                "list=" + list +
                ", userIds=" + userIds +
                ", nextKey=" + nextKey +
                ", currentKey=" + currentKey +
                '}';
    }
}

