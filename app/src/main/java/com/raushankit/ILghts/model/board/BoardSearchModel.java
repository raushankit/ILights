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

    private Long time_neg;

    public BoardSearchModel() {}

    public BoardSearchModel(String name, String email, Long time, Long time_neg) {
        this.name = name;
        this.email = email;
        this.time = time;
        this.time_neg = time_neg;
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

    public Long getTime_neg() {
        return time_neg;
    }

    public void setTime_neg(Long time_neg) {
        this.time_neg = time_neg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoardSearchModel that = (BoardSearchModel) o;
        return Objects.equals(name, that.name) && Objects.equals(email, that.email) && Objects.equals(time, that.time) && Objects.equals(time_neg, that.time_neg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, time, time_neg);
    }

    @NonNull
    @Override
    public String toString() {
        return "BoardSearchModel{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", time=" + time +
                ", time_neg=" + time_neg +
                '}';
    }
}
