package com.raushankit.ILghts.flivedata;

import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ChildDelayLiveData<T> extends LiveData<Map<String, T>> implements Runnable {
    private static final String TAG = "ChildDelayLiveData";
    private static final int REMOVAL_DELAY = 2000;
    private final Class<T> cls;
    private final DatabaseReference db;
    private final Map<String, T> mp;
    private final Handler mHandler;
    private final ChildEventListener listener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            mp.put(snapshot.getKey(), snapshot.getValue(cls));
            postValue(mp);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            mp.put(snapshot.getKey(), snapshot.getValue(cls));
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
            Log.w(TAG, "onCancelled: error", error.toException());
        }
    };
    private boolean isRemovalPending = false;

    public ChildDelayLiveData(@NonNull String path, Class<T> cls) {
        this.cls = cls;
        db = FirebaseDatabase.getInstance().getReference(path);
        mHandler = new Handler();
        mp = new HashMap<>();
    }

    @Override
    protected void onActive() {
        if (isRemovalPending) {
            mHandler.removeCallbacks(this);
        } else {
            db.addChildEventListener(listener);
        }
        isRemovalPending = false;
    }

    @Override
    protected void onInactive() {
        mHandler.postDelayed(this, REMOVAL_DELAY);
        isRemovalPending = true;
    }

    public void removeListeners() {
        if (isRemovalPending) {
            db.removeEventListener(listener);
            mp.clear();
            Log.w(TAG, "forced removeListeners: removed listeners");
            mHandler.removeCallbacks(this);
            isRemovalPending = false;
        }
    }

    @Override
    public void run() {
        db.removeEventListener(listener);
        mp.clear();
        isRemovalPending = false;
        Log.w(TAG, "run: removing listeners for " + cls.getSimpleName());
    }
}
