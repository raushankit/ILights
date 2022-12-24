package com.raushankit.ILghts.model;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import java.util.Objects;

@SuppressWarnings("unused")
@Keep
public class UserCombinedData {

    private User user;

    private String apiKey;

    public UserCombinedData() {
    }

    public UserCombinedData(User user, String apiKey) {
        this.user = user;
        this.apiKey = apiKey;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserCombinedData that = (UserCombinedData) o;
        return Objects.equals(user, that.user) && Objects.equals(apiKey, that.apiKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, apiKey);
    }

    @NonNull
    @Override
    public String toString() {
        return "UserCombinedData{" +
                "user=" + user +
                ", apiKey='" + apiKey + '\'' +
                '}';
    }
}
