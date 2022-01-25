package com.raushankit.ILghts.storage;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.raushankit.ILghts.model.PinData;
import com.raushankit.ILghts.model.PinInfo;
import com.raushankit.ILghts.model.PinListData;
import com.raushankit.ILghts.model.Role;
import com.raushankit.ILghts.model.User;
import com.raushankit.ILghts.model.VersionInfo;
import com.raushankit.ILghts.observer.PinInfoLivedata;
import com.raushankit.ILghts.observer.PinStatusLiveData;
import com.raushankit.ILghts.observer.PinUpdateLiveData;
import com.raushankit.ILghts.observer.RoleLiveData;
import com.raushankit.ILghts.observer.UserLiveData;
import com.raushankit.ILghts.observer.VersionInfoObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserRepository {
    private static final String TAG = "USER_REPOSITORY";
    private static UserRepository INSTANCE;
    private String userId = "ebuebeubyeydeyhdtvhdgevcrfdh";
    private final LiveData<User> userLiveData;
    private final LiveData<Role> roleLiveData;
    private final LiveData<VersionInfo> versionLiveData;
    private final PinInfoLivedata pinInfoLivedata;
    private final PinUpdateLiveData pinUpdateLiveData;
    private final PinStatusLiveData pinStatusLiveData;

    private UserRepository(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            userId = user.getUid();
        }else{
            Log.e(TAG, "UserRepository: user is null");
        }
        userLiveData = new UserLiveData("/users/" + userId);
        roleLiveData = new RoleLiveData("/role/" + userId);
        versionLiveData = new VersionInfoObserver("/metadata");
        pinInfoLivedata = new PinInfoLivedata();
        pinStatusLiveData = new PinStatusLiveData();
        pinUpdateLiveData = new PinUpdateLiveData();
    }

    public static UserRepository newInstance() {
        if(INSTANCE == null){
            INSTANCE = new UserRepository();
        }
        return INSTANCE;
    }

    public void signOutEvent(){
        INSTANCE = null;
    }

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public LiveData<Role> getRoleLiveData(){
        return roleLiveData;
    }

    public LiveData<VersionInfo> getVersionLiveData(){
        return versionLiveData;
    }

    public LiveData<List<PinListData>> fetchPinData(){
        MediatorLiveData<List<PinListData>> data = new MediatorLiveData<>();

        data.addSource(pinInfoLivedata, stringPinInfoMap -> data.setValue(combineData()));
        data.addSource(pinStatusLiveData, stringBooleanMap -> data.setValue(combineData()));
        data.addSource(pinUpdateLiveData, stringPinDataMap -> data.setValue(combineData()));

        return data;
    }

    private List<PinListData> combineData() {
        List<PinListData> list = new ArrayList<>();
        Map<String, PinInfo> mpi = pinInfoLivedata.getValue();
        Map<String, PinData> mpu = pinUpdateLiveData.getValue();
        Map<String, Boolean> mps = pinStatusLiveData.getValue();

        if(mpi != null && mps != null && mpu != null){
            mpi.forEach((k,v) -> {
                PinData pinData = mpu.get(k);
                Boolean status = mps.get(k);
                if(pinData != null && status != null){
                    list.add(new PinListData(Integer.parseInt(k),v.getName(),pinData.getUserName(),pinData.getTimeStamp(), status, pinData.getUserUid().equals(userId)));
                }
            });
        }
        return list;
    }
}
