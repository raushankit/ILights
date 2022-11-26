package com.raushankit.ILghts.response;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.raushankit.ILghts.model.board.BoardSearchModel;
import com.raushankit.ILghts.model.room.BoardRoomData;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class BoardSearchResponse {

    private final int pageSize;

    private final Map<String, BoardRoomData> data;

    private final Map<String, BoardSearchModel> checkMap;

    private final Map<String, Boolean> userBoardIds;

    private boolean endOfPage;

    private String lastKey;

    private BoardSearchModel lastValue;

    private String error;

    private int pageNumber;

    private boolean noData;

    private boolean loading;

    public BoardSearchResponse(final int pageSize, Map<String, Boolean> userBoardIds) {
        this.pageSize = pageSize;
        data = new HashMap<>();
        checkMap = new HashMap<>();
        lastKey = null;
        endOfPage = false;
        lastValue = null;
        error = null;
        pageNumber = 0;
        noData = false;
        loading = false;
        this.userBoardIds = userBoardIds;
    }

    public int getPageSize() {
        return pageSize;
    }

    public Map<String, BoardRoomData> getData() {
        return data;
    }

    public boolean isEndOfPage() {
        return endOfPage;
    }

    public String getLastKey() {
        return lastKey;
    }

    public BoardSearchModel getLastValue() {
        return lastValue;
    }

    public Map<String, BoardSearchModel> getCheckMap() {
        return checkMap;
    }

    public String getError() {
        return error;
    }

    public boolean isNoData() {
        return noData;
    }

    public Map<String, Boolean> getUserBoardIds() {
        return userBoardIds;
    }

    public void setUserBoardIds(String id, boolean flag) {
        this.userBoardIds.put(id, flag);
    }

    public void increasePage(int delta) {
        pageNumber += delta;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setError(Exception e) {
        error = e == null? null: e.getMessage();
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public boolean processResponse(DataSnapshot snapshot) {
        noData = snapshot == null || !snapshot.hasChildren();
        endOfPage = noData? !data.isEmpty():snapshot.getChildrenCount() < pageSize;
        if(noData) { return false; }
        snapshot.getChildren()
                .forEach(snap -> {
                    lastKey = snap.getKey();
                    checkMap.put(snap.getKey(), snap.getValue(BoardSearchModel.class));
                });
        lastValue = checkMap.get(lastKey);
        return true;
    }

    public void evictOnFetch(BoardRoomData data) {
        checkMap.remove(data.getBoardId());
        this.data.put(data.getBoardId(), data);
    }

    @NonNull
    @Override
    public String toString() {
        return "BoardSearchResponse{" +
                "pageSize=" + pageSize +
                ", data=" + data.size() +
                ", checkMap=" + checkMap.size() +
                ", userBoardIds=" + userBoardIds.size() +
                ", endOfPage=" + endOfPage +
                ", lastKey='" + lastKey + '\'' +
                ", lastValue=" + lastValue +
                ", error='" + error + '\'' +
                ", pageNumber=" + pageNumber +
                ", noData=" + noData +
                ", loading=" + loading +
                '}';
    }
}
