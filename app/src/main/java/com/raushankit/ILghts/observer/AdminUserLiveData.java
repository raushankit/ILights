package com.raushankit.ILghts.observer;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.raushankit.ILghts.model.User;

import java.util.LinkedHashMap;
import java.util.Map;

public class AdminUserLiveData extends LiveData<Map<String, User>> {
    private final Map<String, User> mp = new LinkedHashMap<>();
    private final Query mQuery;

    private final ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            snapshot.getChildren().forEach(dataSnapshot -> mp.put(dataSnapshot.getKey(), dataSnapshot.getValue(User.class)));
            postValue(mp);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            postValue(null);
        }
    };

    public AdminUserLiveData(@NonNull Query mQuery){
        this.mQuery = mQuery;
    }

    @Override
    protected void onActive() {
        mQuery.addListenerForSingleValueEvent(listener);
    }

    @Override
    protected void onInactive() {
        mp.clear();
    }
}
