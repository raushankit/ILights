package com.raushankit.ILghts.model.board;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Keep
@SuppressWarnings("unused")
public class BoardPinsModel implements Parcelable{

    private String board;
    private String usablePins;
    private int n;

    public BoardPinsModel(){}

    public BoardPinsModel(String board, String usablePins, int n) {
        this.board = board;
        this.usablePins = usablePins;
        this.n = n;
    }

    public BoardPinsModel(int n) {
        this.n = n;
    }

    protected BoardPinsModel(Parcel in) {
        board = in.readString();
        usablePins = in.readString();
        n = in.readInt();
    }

    public static final Creator<BoardPinsModel> CREATOR = new Creator<BoardPinsModel>() {
        @Override
        public BoardPinsModel createFromParcel(Parcel in) {
            return new BoardPinsModel(in);
        }

        @Override
        public BoardPinsModel[] newArray(int size) {
            return new BoardPinsModel[size];
        }
    };

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public String getUsablePins() {
        return usablePins;
    }

    public void setUsablePins(String usablePins) {
        this.usablePins = usablePins;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoardPinsModel)) return false;
        BoardPinsModel that = (BoardPinsModel) o;
        return n == that.n && Objects.equals(board, that.board) && Objects.equals(usablePins, that.usablePins);
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, usablePins, n);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(board);
        parcel.writeString(usablePins);
        parcel.writeInt(n);
    }

    @NonNull
    @Override
    public String toString() {
        return "BoardPinsModel{" +
                "board='" + board + '\'' +
                ", usablePins='" + usablePins + '\'' +
                ", n=" + n +
                '}';
    }
}
