package com.raushankit.ILghts;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class BaseApp extends Application {

    private static final String TAG = "BaseApplication";
    private FirebaseAuth mAuth;

    @Override
    public void onCreate() {
        super.onCreate();
        mAuth = FirebaseAuth.getInstance();

    }

}
