package com.raushankit.ILghts.viewModel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.raushankit.ILghts.R;
import com.raushankit.ILghts.flivedata.SingleLiveData;
import com.raushankit.ILghts.model.Role;
import com.raushankit.ILghts.model.User;
import com.raushankit.ILghts.model.UserCombinedData;
import com.raushankit.ILghts.model.VersionInfo;
import com.raushankit.ILghts.room.BoardRoomDatabase;
import com.raushankit.ILghts.storage.UserRepository;


public class UserViewModel extends AndroidViewModel {

    private final UserRepository uRepo;
    private final MediatorLiveData<UserCombinedData> apiKeyAndUser = new MediatorLiveData<>();
    private final UserCombinedData apiKeyAndUserData = new UserCombinedData();
    private final BoardRoomDatabase roomDatabase;
    private final Toast toast;

    public UserViewModel(@NonNull Application application) {
        super(application);
        this.uRepo = UserRepository.newInstance(application);
        this.roomDatabase = BoardRoomDatabase.getDatabase(application);
        toast = Toast.makeText(application, R.string.error, Toast.LENGTH_SHORT);
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

    public void clearBoardDb() {
        BoardRoomDatabase.databaseExecutor.execute(() -> {
            roomDatabase.boardDao().deleteAll();
            toast.setText(R.string.board_data_deleted);
            toast.show();
        });
    }

    public void clearNotificationsDb() {
        BoardRoomDatabase.databaseExecutor.execute(() -> {
            roomDatabase.notificationDao().deleteAll();
            toast.setText(R.string.notification_data_deleted);
            toast.show();
        });
    }

    public LiveData<Integer> countUnseenNotifications() {
        return uRepo.countUnseenNotifications();
    }

}
