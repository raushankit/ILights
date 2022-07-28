package com.raushankit.ILghts.response;

import android.util.Log;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.raushankit.ILghts.model.Notification;
import com.raushankit.ILghts.model.NotificationData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Keep
public class NotificationResponse {
    private static final String TAG = "NotificationResponse";
    private final List<Notification> list;
    private final List<String> ids;
    private final Long nextKey;
    private final Long currentKey;

    public NotificationResponse(int totalItem, @NonNull DataSnapshot snapshot) {
        list = new ArrayList<>();
        ids = new ArrayList<>();
        snapshot.getChildren()
                .forEach(data -> {
                    Notification notification = data.getValue(Notification.class);
                    if (notification != null) {
                        notification.setId(Objects.requireNonNull(data.getKey()));
                        if(notification.getData() == null){
                            notification.setData(new NotificationData());
                        }
                        ids.add(data.getKey());
                        list.add(notification);
                    }
                });
        if (list.size() < totalItem) {
            nextKey = null;
        } else {
            Log.e("BoardNEXT_KEY", "BoardMemberResponse: " + list.get(totalItem - 1));
            nextKey = list.get(totalItem - 1).getTime();
        }
        if(list.isEmpty()){
            currentKey = null;
        }else{
            currentKey = list.get(0).getTime();
        }
    }

    public List<Notification> getList() {
        return list;
    }

    public Long getNextKey() {
        return nextKey;
    }

    public Long getCurrentKey() {
        return currentKey;
    }

    public List<String> getIds() {
        return ids;
    }

    @NonNull
    @Override
    public String toString() {
        return "NotificationResponse{" +
                "list=" + list +
                ", ids=" + ids +
                ", nextKey=" + nextKey +
                ", currentKey=" + currentKey +
                '}';
    }
}
