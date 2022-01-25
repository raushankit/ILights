package com.raushankit.ILghts.model;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.Objects;

@SuppressWarnings("unused")
public class PinListData {

    private int pinNumber;
    private String pinName;
    private String changedBy;
    private long changedAt;
    private boolean status;
    private boolean you;

    public PinListData(){

    }

    public PinListData(int pinNumber, String pinName, String changedBy, long changedAt, boolean status, boolean you) {
        this.pinNumber = pinNumber;
        this.pinName = pinName;
        this.changedBy = changedBy;
        this.changedAt = changedAt;
        this.status = status;
        this.you = you;
    }

    public int getPinNumber() {
        return pinNumber;
    }

    public void setPinNumber(int pinNumber) {
        this.pinNumber = pinNumber;
    }

    public String getPinName() {
        return pinName;
    }

    public void setPinName(String pinName) {
        this.pinName = pinName;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public long getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(long changedAt) {
        this.changedAt = changedAt;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isYou() {
        return you;
    }

    public void setYou(boolean you) {
        this.you = you;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PinListData)) return false;
        PinListData that = (PinListData) o;
        return pinNumber == that.pinNumber && changedAt == that.changedAt && status == that.status && you == that.you && Objects.equals(pinName, that.pinName) && Objects.equals(changedBy, that.changedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pinNumber, pinName, changedBy, changedAt, status, you);
    }

    @NonNull
    @Override
    public String toString() {
        return "PinListData{" +
                "pinNumber=" + pinNumber +
                ", pinName='" + pinName + '\'' +
                ", changedBy='" + changedBy + '\'' +
                ", changedAt=" + changedAt +
                ", status=" + status +
                ", you=" + you +
                '}';
    }

    public static final DiffUtil.ItemCallback<PinListData> DIFF_CALLBACK = new DiffUtil.ItemCallback<PinListData>() {
        @Override
        public boolean areItemsTheSame(@NonNull PinListData oldItem, @NonNull PinListData newItem) {
            return oldItem.getPinNumber() == newItem.getPinNumber();
        }

        @Override
        public boolean areContentsTheSame(@NonNull PinListData oldItem, @NonNull PinListData newItem) {
            return oldItem.equals(newItem);
        }
    };
}
