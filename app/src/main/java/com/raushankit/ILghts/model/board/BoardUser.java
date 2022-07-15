package com.raushankit.ILghts.model.board;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

@Keep
@IgnoreExtraProperties
@SuppressWarnings("unused")
public class BoardUser {

    private String uid;
    private int level;
    private String name;

    public BoardUser(){}

    public BoardUser(String uid, int level, String name) {
        this.uid = uid;
        this.level = level;
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return "BoardUser{" +
                "uid='" + uid + '\'' +
                ", level=" + level +
                ", name='" + name + '\'' +
                '}';
    }
}
