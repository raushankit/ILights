package com.raushankit.ILghts.model.board;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Objects;

@Keep
@IgnoreExtraProperties
@SuppressWarnings("unused")
public class BoardSearchUserModel {

    private String userId;
    private String name;
    private String email;
    private boolean member;

    public BoardSearchUserModel() {}

    public BoardSearchUserModel(String userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
    }

    public BoardSearchUserModel(String userId, String name, String email, boolean member) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.member = member;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isMember() {
        return member;
    }

    public void setMember(boolean member) {
        this.member = member;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoardSearchUserModel)) return false;
        BoardSearchUserModel that = (BoardSearchUserModel) o;
        return Objects.equals(userId, that.userId) && Objects.equals(name, that.name) && Objects.equals(email, that.email) && Objects.equals(member, that.member);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, name, email);
    }

    @NonNull
    @Override
    public String toString() {
        return "BoardSearchUserModel{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", member=" + member +
                '}';
    }

    public static final DiffUtil.ItemCallback<BoardSearchUserModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<BoardSearchUserModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull BoardSearchUserModel oldItem, @NonNull BoardSearchUserModel newItem) {
            return oldItem.getUserId().equals(newItem.getUserId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull BoardSearchUserModel oldItem, @NonNull BoardSearchUserModel newItem) {
            return oldItem.equals(newItem);
        }
    };
}
