package com.raushankit.ILghts.model;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

@Keep
@IgnoreExtraProperties
@SuppressWarnings("unused")
public class Role {

    @PropertyName("level")
    private int accessLevel;

    public Role(){

    }

    public Role(int accessLevel) {
        this.accessLevel = accessLevel;
    }

    @PropertyName("level")
    public int getAccessLevel() {
        return accessLevel;
    }

    @PropertyName("level")
    public void setAccessLevel(int accessLevel) {
        this.accessLevel = accessLevel;
    }

    @NonNull
    @Override
    public String toString() {
        return "Role{" +
                "level=" + accessLevel +
                '}';
    }
}
