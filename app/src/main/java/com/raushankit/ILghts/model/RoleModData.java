package com.raushankit.ILghts.model;

import androidx.annotation.Keep;

@Keep
@SuppressWarnings("all")
public class RoleModData {
    private String uid;
    private Role role;

    public RoleModData(){}

    public RoleModData(String uid, int level) {
        this.uid = uid;
        this.role = new Role(Math.max(0,Math.min(3,level)));
    }

    public String getUid() {
        return uid;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public String toString() {
        return "RoleModData{" +
                "uid='" + uid + '\'' +
                ", role=" + role +
                '}';
    }
}
