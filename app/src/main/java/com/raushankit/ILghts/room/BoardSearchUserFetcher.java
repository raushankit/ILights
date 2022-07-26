package com.raushankit.ILghts.room;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.raushankit.ILghts.model.board.BoardSearchUserModel;
import com.raushankit.ILghts.response.BoardSearchResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.reactivex.rxjava3.core.Single;

public class BoardSearchUserFetcher {
    private static final String TAG = "BoardSearchUserFetcher";
    private final DatabaseReference db;

    public BoardSearchUserFetcher(@NonNull DatabaseReference db){
        this.db = db;
    }

    public Single<List<BoardSearchUserModel>> getSearchableUsersByQuery(@NonNull String boardId, @NonNull String query){
        return Single.zip(
                getUsersByQuery(query)
                        .onErrorReturn(throwable -> new BoardSearchResponse.Users(throwable.getMessage())),
                getBoardMembersByQuery(boardId, query)
                        .onErrorReturn(throwable -> new BoardSearchResponse.Members(throwable.getMessage())),
                (users, members) -> {
                    Set<String> st = members.getUsersSet();
                    List<BoardSearchUserModel> modelList;
                    if(st.size() == 1){
                        String id = st.iterator().next();
                        if(id.startsWith("ERROR")){
                            modelList = new ArrayList<>();
                            modelList.add(new BoardSearchUserModel("ERROR",id.substring(5),""));
                            return modelList;
                        }
                    }
                    modelList = users.getUsersAsList();
                    modelList
                            .forEach(it -> it.setMember(st.contains(it.getUserId())));
                    modelList.sort((t1, t2) -> Boolean.compare(t1.isMember(), t2.isMember()));
                    return modelList;
                }
        );
    }

    private Single<BoardSearchResponse.Users> getUsersByQuery(@NonNull String query){
        return Single.create(emitter -> {
            db.child("users")
                    .orderByChild("name")
                    .startAt(query)
                    .endAt(query + "\uf8ff")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Log.w(TAG, "getUsersByQuery: users = " + task.getResult());
                            emitter.onSuccess(new BoardSearchResponse.Users(task.getResult()));
                        } else {
                            emitter.onError(task.getException());
                        }
                    });
        });
    }

    private Single<BoardSearchResponse.Members> getBoardMembersByQuery(@NonNull String boardId, @NonNull String query){
        return Single.create(emitter -> {
            db.child("board_auth")
                    .child(boardId)
                    .orderByChild("name")
                    .startAt(query)
                    .endAt(query + "\uf8ff")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Log.w(TAG, "getUsersByQuery: members = " + task.getResult());
                            emitter.onSuccess(new BoardSearchResponse.Members(task.getResult()));
                        } else {
                            emitter.onError(task.getException());
                        }
                    });
        });
    }
}
