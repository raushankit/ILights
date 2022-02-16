package com.raushankit.ILghts.observer;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.LinkedHashMap;
import java.util.Map;

public class PinStatusLiveData extends LiveData<Map<String, Boolean>> {
    private static final String TAG = "PinStatusLiveData";
    private final DatabaseReference db;
    private final Map<String, Boolean> mp;

    private final ChildEventListener listener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            mp.put(snapshot.getKey(), snapshot.getValue(Boolean.class));
            postValue(mp);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            mp.put(snapshot.getKey(), snapshot.getValue(Boolean.class));
            postValue(mp);
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            mp.remove(snapshot.getKey());
            postValue(mp);
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.e(TAG, "onCancelled: error: ", error.toException());
        }
    };

    public PinStatusLiveData() {
        db = FirebaseDatabase.getInstance().getReference("/control/status");
        mp = new LinkedHashMap<>();
    }

    @Override
    protected void onActive() {
        db.addChildEventListener(listener);
    }

    @Override
    protected void onInactive() {
        db.removeEventListener(listener);
        mp.clear();
    }
}
