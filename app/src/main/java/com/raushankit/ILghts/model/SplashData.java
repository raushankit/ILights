package com.raushankit.ILghts.model;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

@Keep
@SuppressWarnings("unused")
public class SplashData {

    private Role role;
    private int priority;

    public SplashData() {}

    public SplashData(Role role, int priority) {
        this.role = role;
        this.priority = priority;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    @NonNull
    public String toString() {
        return "SplashData{" +
                "role=" + role +
                ", priority=" + priority +
                '}';
    }
}
