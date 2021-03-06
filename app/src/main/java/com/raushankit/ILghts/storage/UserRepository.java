package com.raushankit.ILghts.storage;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.raushankit.ILghts.model.Role;
import com.raushankit.ILghts.model.User;
import com.raushankit.ILghts.model.VersionInfo;
import com.raushankit.ILghts.observer.RoleLiveData;
import com.raushankit.ILghts.observer.UserLiveData;
import com.raushankit.ILghts.observer.VersionInfoObserver;

public class UserRepository {
    private static final String TAG = "USER_REPOSITORY";
    private static int instances = 0;
    private static UserRepository INSTANCE;
    private final UserLiveData userLiveData;
    private final RoleLiveData roleLiveData;
    private final VersionInfoObserver versionLiveData;

    private UserRepository() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = "ebuebeubyeydeyhdtvhdgevcrfdh";
        if (user != null) {
            userId = user.getUid();
        } else {
            Log.e(TAG, "UserRepository: user is null");
        }
        userLiveData = new UserLiveData("/users/" + userId);
        roleLiveData = new RoleLiveData("/role/" + userId);
        versionLiveData = new VersionInfoObserver("/metadata/version");
    }

    public static UserRepository newInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UserRepository();
        }
        instances++;
        Log.w(TAG, "newInstance: number = " + instances);
        return INSTANCE;
    }

    public boolean isNewInstance() {
        return instances == 1;
    }

    public void signOutEvent() {
        INSTANCE = null;
        instances = 0;
    }

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public LiveData<Role> getRoleLiveData() {
        return roleLiveData;
    }

    public LiveData<VersionInfo> getVersionLiveData() {
        return versionLiveData;
    }
}
