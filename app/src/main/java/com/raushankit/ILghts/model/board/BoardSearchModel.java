package com.raushankit.ILghts.model.board;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import java.util.Objects;

@Keep
@SuppressWarnings("unused")
public class BoardSearchModel {

    private String name;

    private String email;

    private Long time;

    public BoardSearchModel() {}

    public BoardSearchModel(String name, String email, Long time) {
        this.name = name;
        this.email = email;
        this.time = time;
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

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoardSearchModel that = (BoardSearchModel) o;
        return Objects.equals(name, that.name) && Objects.equals(email, that.email) && Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, time);
    }

    @NonNull
    @Override
    public String toString() {
        return "BoardSearchModel{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", time=" + time +
                '}';
    }
}
