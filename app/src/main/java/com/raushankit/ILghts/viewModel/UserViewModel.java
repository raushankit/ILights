package com.raushankit.ILghts.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.raushankit.ILghts.flivedata.SingleLiveData;
import com.raushankit.ILghts.model.Role;
import com.raushankit.ILghts.model.User;
import com.raushankit.ILghts.model.UserCombinedData;
import com.raushankit.ILghts.model.VersionInfo;
import com.raushankit.ILghts.storage.UserRepository;


public class UserViewModel extends AndroidViewModel {
    private static final String TAG = "UserViewModel";
    private final UserRepository uRepo;
    private final MediatorLiveData<UserCombinedData> apiKeyAndUser = new MediatorLiveData<>();
    private final UserCombinedData apiKeyAndUserData = new UserCombinedData();

    public UserViewModel(@NonNull Application application) {
        super(application);
        this.uRepo = UserRepository.newInstance(application);
    }

    public void resetRepository() {
        uRepo.signOutEvent();
    }

    public boolean isNew() {
        return uRepo.isNewInstance();
    }

    public LiveData<User> getUserData() {
        return uRepo.getUserLiveData();
    }

    public LiveData<Role> getRoleData() {
        return uRepo.getRoleLiveData();
    }

    public LiveData<VersionInfo> getVersionData() {
        return uRepo.getVersionLiveData();
    }

    public LiveData<UserCombinedData> getCombinedData() {
        apiKeyAndUser.addSource(uRepo.getUserLiveData(), user -> {
            apiKeyAndUserData.setUser(user);
            if(apiKeyAndUserData.getApiKey() != null) {
                apiKeyAndUser.setValue(apiKeyAndUserData);
            }
        });
        apiKeyAndUser.addSource(new SingleLiveData<>("metadata/api_key/key", String.class), str -> {
            apiKeyAndUserData.setApiKey(str);
            if(apiKeyAndUserData.getUser() != null) {
                apiKeyAndUser.setValue(apiKeyAndUserData);
            }
        });
        return apiKeyAndUser;
    }

    public LiveData<Integer> countUnseenNotifications() {
        return uRepo.countUnseenNotifications();
    }

}
