package com.raushankit.ILghts.utils;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.raushankit.ILghts.model.Role;
import com.raushankit.ILghts.model.User;

import java.util.LinkedHashMap;
import java.util.Map;

public class UserUpdates {
    private final DatabaseReference db;

    public UserUpdates(){
        db = FirebaseDatabase.getInstance().getReference();
    }

    public String createUserMetaData(@NonNull String uid, @NonNull User user){
        Map<String, Object> mp = new LinkedHashMap<>();
        mp.put("users/" + uid, user);
        mp.put("role/" + uid, new Role(1));
        final String[] message = new String[1];
        db.updateChildren(mp, (error, ref) -> {
            if(error != null){
                message[0] = error.getMessage();
            }
        });
        return message[0];
    }
}
