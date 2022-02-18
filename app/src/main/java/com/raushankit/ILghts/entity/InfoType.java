package com.raushankit.ILghts.entity;

public enum InfoType {
    PROFILE_VISIBILITY,
    EMAIL_VERIFIED,
    EMAIL_NOT_VERIFIED,
    ADMIN_VISIBILITY,
    ADMIN_INVISIBILITY,
    LOGIN_METHOD_GOOGLE,
    LOGIN_METHOD_EMAIL;

    public enum Keys {
        PROFILE,EMAIL_VERIFICATION,ADMIN,LOGIN_METHOD
    }
}
