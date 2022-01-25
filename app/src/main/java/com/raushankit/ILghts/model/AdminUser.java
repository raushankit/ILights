package com.raushankit.ILghts.model;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Objects;

@Keep
@IgnoreExtraProperties
@SuppressWarnings("unused")
public class AdminUser {
    private String uid;
    private User user;

    public AdminUser(){

    }

    public AdminUser(String uid, User user) {
        this.uid = uid;
        this.user = user;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AdminUser)) return false;
        AdminUser adminUser = (AdminUser) o;
        return Objects.equals(uid, adminUser.uid) && Objects.equals(user, adminUser.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, user);
    }

    @NonNull
    @Override
    public String toString() {
        return "AdminUser{" +
                "uid='" + uid + '\'' +
                ", user=" + user +
                '}';
    }

    public static final DiffUtil.ItemCallback<AdminUser> DIFF_CALLBACK = new DiffUtil.ItemCallback<AdminUser>() {
        @Override
        public boolean areItemsTheSame(@NonNull AdminUser oldItem, @NonNull AdminUser newItem) {
            return oldItem.uid.equals(newItem.uid);
        }

        @Override
        public boolean areContentsTheSame(@NonNull AdminUser oldItem, @NonNull AdminUser newItem) {
            return oldItem.equals(newItem);
        }
    };
}
