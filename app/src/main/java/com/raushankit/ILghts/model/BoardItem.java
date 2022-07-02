package com.raushankit.ILghts.model;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.Objects;

@Keep
@SuppressWarnings("unused")
public class BoardItem {
    private String id;
    private String title;
    private String description;

    public BoardItem(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
    @NonNull
    public String toString() {
        return "BoardItem{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoardItem)) return false;
        BoardItem boardItem = (BoardItem) o;
        return id.equals(boardItem.id) && title.equals(boardItem.title) && description.equals(boardItem.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description);
    }

    public static final DiffUtil.ItemCallback<BoardItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<BoardItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull BoardItem oldItem, @NonNull BoardItem newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull BoardItem oldItem, @NonNull BoardItem newItem) {
            return oldItem.equals(newItem);
        }
    };
}
