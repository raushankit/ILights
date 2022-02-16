package com.raushankit.ILghts.model;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

@Keep
@IgnoreExtraProperties
@SuppressWarnings("unused")
public class VersionInfo {

    @PropertyName("version_name")
    private String versionName;

    @PropertyName("version_code")
    private Integer versionCode;

    public VersionInfo() {

    }

    public VersionInfo(String versionName, Integer versionCode) {
        this.versionName = versionName;
        this.versionCode = versionCode;
    }

    @PropertyName("version_name")
    public String getVersionName() {
        return versionName;
    }

    @PropertyName("version_name")
    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    @PropertyName("version_code")
    public Integer getVersionCode() {
        return versionCode;
    }

    @PropertyName("version_code")
    public void setVersionCode(Integer versionCode) {
        this.versionCode = versionCode;
    }

    @NonNull
    @Override
    public String toString() {
        return "VersionInfo{" +
                "versionName='" + versionName + '\'' +
                ", versionCode=" + versionCode +
                '}';
    }
}
