package com.raushankit.ILghts.model.board;


import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

@Keep
@IgnoreExtraProperties
@SuppressWarnings("unused")
public class BoardAuthUser {
    private String name;
    private int level;

    public BoardAuthUser() {}

    public BoardAuthUser(String name, int level) {
        this.name = name;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @NonNull
    @Override
    public String toString() {
        return "BoardAuthUser{" +
                "name='" + name + '\'' +
                ", level='" + level + '\'' +
                '}';
    }
}
