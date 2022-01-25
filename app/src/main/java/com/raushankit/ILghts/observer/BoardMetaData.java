package com.raushankit.ILghts.observer;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raushankit.ILghts.model.BoardPinData;

public class BoardMetaData extends LiveData<BoardPinData> {
    private static final String TAG = "BoardMetaData";
    private final DatabaseReference db;

    private final ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            postValue(snapshot.getValue(BoardPinData.class));
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.e(TAG, "onCancelled: " + error.getMessage());
        }
    };

    public BoardMetaData(){
        db = FirebaseDatabase.getInstance().getReference("/control/board_data");
    }

    @Override
    protected void onActive() {
        db.addListenerForSingleValueEvent(listener);
    }

    @Override
    protected void onInactive() {
        db.removeEventListener(listener);
    }

}
