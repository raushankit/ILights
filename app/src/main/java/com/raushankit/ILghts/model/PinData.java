package com.raushankit.ILghts.model;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

@Keep
@IgnoreExtraProperties
@SuppressWarnings("unused")
public class PinData {

    @PropertyName("changer_id")
    private String userUid;

    @PropertyName("changed_at")
    private long timeStamp;

    @PropertyName("changed_by")
    private String userName;

    public PinData(){

    }

    public PinData(String userUid, long timeStamp, String userName) {
        this.userUid = userUid;
        this.timeStamp = timeStamp;
        this.userName = userName;
    }

    @PropertyName("changed_at")
    public long getTimeStamp() {
        return timeStamp;
    }

    @PropertyName("changed_at")
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @PropertyName("changed_by")
    public String getUserName() {
        return userName;
    }

    @PropertyName("changed_by")
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @PropertyName("changer_id")
    public String getUserUid() {
        return userUid;
    }

    @PropertyName("changer_id")
    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    @NonNull
    @Override
    public String toString() {
        return "PinData{" +
                "userUid='" + userUid + '\'' +
                ", timeStamp=" + timeStamp +
                ", userName='" + userName + '\'' +
                '}';
    }
}
