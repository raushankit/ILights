package com.raushankit.ILghts.observer;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raushankit.ILghts.model.UpdatePriority;

public class UpdateTypeLiveData extends LiveData<UpdatePriority> {
    private static final String TAG = "UpdateTypeLiveData";

    private final DatabaseReference db;
    private final ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            postValue(snapshot.getValue(UpdatePriority.class));
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    public UpdateTypeLiveData(int versionCode){
        db = FirebaseDatabase.getInstance().getReference("/metadata/version/"+versionCode);
    }

    @Override
    protected void onActive() {
        db.addListenerForSingleValueEvent(listener);
        super.onActive();
    }
}
