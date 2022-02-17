package com.raushankit.ILghts.flivedata;

import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ValueDelayLiveData<T> extends LiveData<T> implements Runnable {
    private static final String TAG = "ValueDelayLiveData";
    private static final int REMOVAL_DELAY = 2000;
    private final Class<T> cls;
    private final DatabaseReference db;
    private final Handler mHandler;
    private final ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            postValue(snapshot.getValue(cls));
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.w(TAG, "onCancelled: Data fetch error for class " + cls.getSimpleName(), error.toException());
        }
    };
    private boolean isRemovalPending = false;

    public ValueDelayLiveData(@NonNull String path, Class<T> cls) {
        this.cls = cls;
        db = FirebaseDatabase.getInstance().getReference(path);
        mHandler = new Handler();
    }

    public void removeListeners() {
        if (isRemovalPending) {
            mHandler.removeCallbacks(this);
            isRemovalPending = false;
            db.removeEventListener(valueEventListener);
            Log.e(TAG, "forced removeListeners: removed listeners");
        }
    }

    @Override
    public void run() {
        db.removeEventListener(valueEventListener);
        FirebaseDatabase.getInstance().getReference("test/" + cls.getSimpleName()).setValue(true);
        isRemovalPending = false;
        Log.w(TAG, "run: removing listeners for " + cls.getSimpleName());
    }

    @Override
    protected void onActive() {
        if (isRemovalPending) {
            mHandler.removeCallbacks(this);
        } else {
            db.addValueEventListener(valueEventListener);
        }
        isRemovalPending = false;
    }

    @Override
    protected void onInactive() {
        mHandler.postDelayed(this, REMOVAL_DELAY);
        isRemovalPending = true;
    }
}