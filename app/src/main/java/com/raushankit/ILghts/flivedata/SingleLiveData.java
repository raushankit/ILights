package com.raushankit.ILghts.flivedata;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SingleLiveData<T> extends LiveData<T> {
    private static final String TAG = "SingleLiveData";
    private final DatabaseReference db;
    private final Class<T> cls;
    private final ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            postValue(snapshot.getValue(cls));
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    public SingleLiveData(@NonNull String path, Class<T> cls) {
        this.cls = cls;
        db = FirebaseDatabase.getInstance().getReference(path);
    }

    @Override
    protected void onActive() {
        db.addListenerForSingleValueEvent(listener);
    }
}
