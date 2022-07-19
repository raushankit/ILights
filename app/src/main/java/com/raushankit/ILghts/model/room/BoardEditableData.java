package com.raushankit.ILghts.model.room;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;

import com.google.firebase.database.Exclude;

import java.util.Objects;

public class BoardEditableData implements Parcelable {

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "description")
    private String description;

    @Ignore
    public BoardEditableData() {}

    public BoardEditableData(String title, String description) {
        this.title = title;
        this.description = description;
    }

    @Ignore
    protected BoardEditableData(Parcel in) {
        title = in.readString();
        description = in.readString();
    }

    @Exclude
    @Ignore
    public static final Creator<BoardEditableData> CREATOR = new Creator<BoardEditableData>() {
        @Override
        public BoardEditableData createFromParcel(Parcel in) {
            return new BoardEditableData(in);
        }

        @Override
        public BoardEditableData[] newArray(int size) {
            return new BoardEditableData[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoardEditableData)) return false;
        BoardEditableData that = (BoardEditableData) o;
        return Objects.equals(title, that.title) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description);
    }

    @Override
    @NonNull
    public String toString() {
        return "BoardEditableData{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(description);
    }
}
