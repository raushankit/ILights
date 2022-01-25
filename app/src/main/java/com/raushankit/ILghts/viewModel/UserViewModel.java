package com.raushankit.ILghts.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.raushankit.ILghts.model.PinListData;
import com.raushankit.ILghts.model.Role;
import com.raushankit.ILghts.model.User;
import com.raushankit.ILghts.model.VersionInfo;
import com.raushankit.ILghts.storage.UserRepository;

import java.util.List;

public class UserViewModel extends ViewModel {
    private final UserRepository uRepo;

    public UserViewModel(){
        this.uRepo = UserRepository.newInstance();
    }

    public void resetRepository(){
        uRepo.signOutEvent();
    }

    public LiveData<User> getUserData(){
        return uRepo.getUserLiveData();
    }

    public LiveData<Role> getRoleData(){
        return uRepo.getRoleLiveData();
    }

    public LiveData<VersionInfo> getVersionData(){
        return uRepo.getVersionLiveData();
    }

    public LiveData<List<PinListData>> getPinData() {
        return uRepo.fetchPinData();
    }

}
