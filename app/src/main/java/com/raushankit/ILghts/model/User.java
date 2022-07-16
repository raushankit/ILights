package com.raushankit.ILghts.model;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.raushankit.ILghts.utils.StringUtils;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Keep
@IgnoreExtraProperties
@SuppressWarnings("unused")
public class User {

    private String name;
    private String email;

    public User() {

    }

    public User(String name, String email) {
        this.name = name.toLowerCase(Locale.ROOT);
        this.email = email.toLowerCase(Locale.ROOT);
    }

    public String getName() {
        return StringUtils.capitalize(name);
    }

    public void setName(String name) {
        this.name = name.toLowerCase(Locale.ROOT);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(name, user.name) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email);
    }

    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> mp = new LinkedHashMap<>();
        mp.put("name", name.toLowerCase(Locale.getDefault()));
        mp.put("email", email.toLowerCase(Locale.getDefault()));
        return mp;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
