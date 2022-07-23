package com.raushankit.ILghts.room;

import android.app.Application;
import android.util.Log;

public class BoardMemberRepository {
    private static final String TAG = "BoardMemberRepository";
    private static volatile BoardMemberRepository INSTANCE;

    private BoardMemberRepository(Application application){

    }

    public static BoardMemberRepository getInstance(Application application) {
        Log.d(TAG, "getInstance()");
        if (INSTANCE == null) {
            synchronized (BoardMemberRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BoardMemberRepository(application);
                }
            }
        }
        return INSTANCE;
    }
}
