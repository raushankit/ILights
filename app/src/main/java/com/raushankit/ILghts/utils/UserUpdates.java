package com.raushankit.ILghts.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.raushankit.ILghts.entity.NotificationType;
import com.raushankit.ILghts.model.Role;
import com.raushankit.ILghts.model.User;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class UserUpdates {
    private static final String TAG = "UserUpdates";
    private final DatabaseReference db;

    public UserUpdates() {
        db = FirebaseDatabase.getInstance().getReference();
    }

    public String createUserMetaData(@NonNull String uid, @NonNull User user) {
        Map<String, Object> mp = new LinkedHashMap<>();
        mp.put("users/" + uid, user.toMap());
        mp.put("role/" + uid, new Role(1));
        String key = "user_notif/" + uid + "/" + UUID.randomUUID().toString();
        mp.put(key + "/body", "Welcome!!");
        mp.put(key + "/time", -1* StringUtils.TIMESTAMP());
        mp.put(key + "/type", NotificationType.TEXT);
        final String[] message = new String[1];
        db.updateChildren(mp, (error, ref) -> {
            if (error != null) {
                message[0] = error.getMessage();
            }
        });
        return message[0];
    }

    public void setUpdateLog(String tag, Task<AppUpdateInfo> task){
        Map<String, Object> mp = new LinkedHashMap<>();
        mp.put("success", task.isSuccessful());
        if(task.isSuccessful()){
            AppUpdateInfo info = task.getResult();
            mp.put("updateAvailability", Utilities.debugUpdateAvailability(info.updateAvailability()));
            mp.put("availableVersionCode", info.availableVersionCode());
            mp.put("flexible", info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE));
            mp.put("immediate", info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE));
            mp.put("updatePriority", info.updatePriority());
            mp.put("packageName", info.packageName());
        }else{
            mp.put("error", (task.getException() != null && task.getException().getMessage() != null) ? task.getException().getMessage(): "unknown");
        }
        db.child("test/updates/"+ tag + "/" + Calendar.getInstance().getTimeInMillis()).updateChildren(mp, ((error, ref) -> {
            if(error != null){
                Log.w(TAG, "setUpdateLog: error: " + error.getMessage());
            }
        }));
    }
}
