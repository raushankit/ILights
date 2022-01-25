package com.raushankit.ILghts.model;

import androidx.annotation.Keep;

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

    public int getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(int accessLevel) {
        this.accessLevel = accessLevel;
    }

    @Override
    public String toString() {
        return "Role{" +
                "accessLevel=" + accessLevel +
                '}';
    }
}
