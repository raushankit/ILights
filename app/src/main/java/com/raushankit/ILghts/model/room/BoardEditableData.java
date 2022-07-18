package com.raushankit.ILghts.model.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;

import java.util.Objects;

public class BoardEditableData {

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
}
