package com.raushankit.ILghts.observer;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PulseLiveData extends LiveData<Long> {
    private static final String TAG = "PulseLiveData";
    private final DatabaseReference db;

    private final ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            postValue(snapshot.getValue(Long.class));
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.e(TAG, "onCancelled: " + error.getMessage());
        }
    };

    public PulseLiveData(){db = FirebaseDatabase.getInstance().getReference("metadata/board_data/heart_beat");}

    @Override
    protected void onActive() {
        db.addValueEventListener(listener);
    }

    @Override
    protected void onInactive() {
        db.removeEventListener(listener);
    }
}
