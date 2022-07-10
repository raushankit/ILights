package com.raushankit.ILghts.model.board;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import java.util.Objects;

@Keep
@SuppressWarnings("unused")
public class BoardCredentialModel implements Parcelable {

    private String id;
    private String username;
    private String password;

    public BoardCredentialModel() {}

    public BoardCredentialModel(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public BoardCredentialModel(String id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    protected BoardCredentialModel(Parcel in) {
        id = in.readString();
        username = in.readString();
        password = in.readString();
    }

    public static final Creator<BoardCredentialModel> CREATOR = new Creator<BoardCredentialModel>() {
        @Override
        public BoardCredentialModel createFromParcel(Parcel in) {
            return new BoardCredentialModel(in);
        }

        @Override
        public BoardCredentialModel[] newArray(int size) {
            return new BoardCredentialModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoardCredentialModel)) return false;
        BoardCredentialModel that = (BoardCredentialModel) o;
        return Objects.equals(id, that.id) && Objects.equals(username, that.username) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password);
    }

    @NonNull
    @Override
    public String toString() {
        return "BoardCredentialModel{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(username);
        parcel.writeString(password);
    }
}
