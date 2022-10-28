package com.raushankit.ILghts.fetcher;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.raushankit.ILghts.model.FilterModel;
import com.raushankit.ILghts.room.BoardDao;
import com.raushankit.ILghts.room.BoardRoomDatabase;

public class BoardPublicFetcher {

    private final BoardDao boardDao;
    private final DatabaseReference db;
    private Query query;

    public BoardPublicFetcher(final DatabaseReference db, final BoardRoomDatabase roomDb) {
        this.boardDao = roomDb.boardDao();
        this.db = db;
    }

    private void getQuery(@NonNull FilterModel model) {
        if(FilterModel.Type.NULL.equals(model.getType())) {
            query = db.orderByKey();
        } else if(FilterModel.Type.FIELD.equals(model.getType())) {
            query = db.orderByChild(model.getFieldName())
                    .startAt(model.getValue())
                    .endAt(model.getValue() + "\uf8ff");
        }
    }
}
