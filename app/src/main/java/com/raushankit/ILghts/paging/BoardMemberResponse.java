package com.raushankit.ILghts.paging;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.raushankit.ILghts.model.board.BoardAuthUser;

import java.util.ArrayList;
import java.util.List;

@Keep
public class BoardMemberResponse {
    private final DataSnapshot snapshot;
    private final List<BoardAuthUser> list;
    private final String nextKey;

    public BoardMemberResponse(int totalItem, @NonNull DataSnapshot snapshot) {
        this.snapshot = snapshot;
        list = new ArrayList<>();
        snapshot.getChildren()
                .forEach(data -> list.add(data.getValue(BoardAuthUser.class)));
        if(list.size() < totalItem){
            nextKey = null;
        }else{
            nextKey = list.get(totalItem-1).getName();
        }
    }

    public DataSnapshot getSnapshot() {
        return snapshot;
    }

    public List<BoardAuthUser> getList() {
        return list;
    }

    public String getNextKey() {
        return nextKey;
    }

    @NonNull
    @Override
    public String toString() {
        return "BoardMemberResponse{" +
                "snapshot=" + snapshot +
                ", list=" + list +
                ", nextKey='" + nextKey + '\'' +
                '}';
    }
}
