package com.raushankit.ILghts.observer;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raushankit.ILghts.model.VersionInfo;

public class VersionInfoObserver extends LiveData<VersionInfo> {
    private static final String TAG = "USER_LIVEDATA";
    private final DatabaseReference db;

    private final ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            postValue(snapshot.getValue(VersionInfo.class));
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.e(TAG, "onCancelled: " + error.getMessage());
        }
    };

    public VersionInfoObserver(@NonNull String path) {
        db = FirebaseDatabase.getInstance().getReference(path);
    }

    @Override
    protected void onActive() {
        db.addValueEventListener(listener);
    }

    @Override
    protected void onInactive() {
        db.removeEventListener(listener);
    }
}
