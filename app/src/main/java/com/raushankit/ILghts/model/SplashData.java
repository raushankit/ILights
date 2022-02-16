package com.raushankit.ILghts.model;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

@Keep
@SuppressWarnings("unused")
public class SplashData {

    private Role role;
    private UpdatePriority updatePriority;

    public SplashData() {
    }

    public SplashData(Role role, UpdatePriority updatePriority) {
        this.role = role;
        this.updatePriority = updatePriority;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public UpdatePriority getUpdatePriority() {
        return updatePriority;
    }

    public void setUpdatePriority(UpdatePriority updatePriority) {
        this.updatePriority = updatePriority;
    }

    @NonNull
    @Override
    public String toString() {
        return "SplashData{" +
                "role=" + role +
                ", updatePriority=" + updatePriority +
                '}';
    }
}
