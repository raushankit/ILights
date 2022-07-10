package com.raushankit.ILghts.model.board;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.raushankit.ILghts.entity.BoardVisibility;

import java.util.Objects;

@Keep
@SuppressWarnings("unused")
public class BoardBasicModel implements Parcelable {

    private String name;
    private String description;
    private int visibility = -1;

    public BoardBasicModel(){}

    public BoardBasicModel(String name, String description, int visibility) {
        this.name = name;
        this.description = description;
        this.visibility = visibility;
    }


    protected BoardBasicModel(Parcel in) {
        name = in.readString();
        description = in.readString();
        visibility = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
        dest.writeInt(visibility);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BoardBasicModel> CREATOR = new Creator<BoardBasicModel>() {
        @Override
        public BoardBasicModel createFromParcel(Parcel in) {
            return new BoardBasicModel(in);
        }

        @Override
        public BoardBasicModel[] newArray(int size) {
            return new BoardBasicModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoardBasicModel)) return false;
        BoardBasicModel that = (BoardBasicModel) o;
        return Objects.equals(name, that.name) && Objects.equals(description, that.description) && visibility == that.visibility;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, visibility);
    }

    @NonNull
    @Override
    public String toString() {
        return "BoardBasicModel{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", visibility=" + visibility +
                '}';
    }
}
