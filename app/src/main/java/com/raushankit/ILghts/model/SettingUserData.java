package com.raushankit.ILghts.model;

import androidx.annotation.Keep;

@Keep
public class SettingUserData {

    private User user;
    private Role role;

    public SettingUserData() {
    }

    public SettingUserData(User user, Role role) {
        this.user = user;
        this.role = role;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "SettingUserData{" +
                "user=" + user +
                ", role=" + role +
                '}';
    }
}
