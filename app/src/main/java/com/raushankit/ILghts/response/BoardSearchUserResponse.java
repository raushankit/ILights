package com.raushankit.ILghts.response;

import com.google.firebase.database.DataSnapshot;
import com.raushankit.ILghts.model.board.BoardSearchUserModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BoardSearchUserResponse {

    public BoardSearchUserResponse() {
    }

    public static class Users extends BoardSearchUserResponse {
        private final List<BoardSearchUserModel> modelList = new ArrayList<>();
        public Users(DataSnapshot snapshot) {
            snapshot.getChildren()
                    .forEach(snapshot1 -> {
                        BoardSearchUserModel model = snapshot1.getValue(BoardSearchUserModel.class);
                        assert model != null;
                        model.setUserId(snapshot1.getKey());
                        model.setMember(false);
                        modelList.add(model);
                    });
        }

        public Users(String message) {
            modelList.add(new BoardSearchUserModel("ERROR",message,""));
        }

        public List<BoardSearchUserModel> getUsersAsList(){
            return modelList;
        }

    }

    public static class Members extends BoardSearchUserResponse {
        private final Set<String> st = new HashSet<>();
        public Members(DataSnapshot snapshot) {
            snapshot.getChildren()
                    .forEach(snapshot1 -> st.add(snapshot1.getKey()));
        }

        public Members(String message) {
            st.add("ERROR " + message);
        }

        public Set<String> getUsersSet(){
            return st;
        }
    }
}
