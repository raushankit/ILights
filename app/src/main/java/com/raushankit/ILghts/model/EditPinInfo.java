package com.raushankit.ILghts.model;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.Objects;

@Keep
@SuppressWarnings("unused")
public class EditPinInfo {

    public static final DiffUtil.ItemCallback<EditPinInfo> DIFF_CALLBACK = new DiffUtil.ItemCallback<EditPinInfo>() {
        @Override
        public boolean areItemsTheSame(@NonNull EditPinInfo oldItem, @NonNull EditPinInfo newItem) {
            return oldItem.getPinNumber() == newItem.getPinNumber();
        }

        @Override
        public boolean areContentsTheSame(@NonNull EditPinInfo oldItem, @NonNull EditPinInfo newItem) {
            return oldItem.equals(newItem);
        }
    };
    private int pinNumber;
    private PinInfo pinInfo;

    public EditPinInfo(int pinNumber, PinInfo pinInfo) {
        this.pinNumber = pinNumber;
        this.pinInfo = pinInfo;
    }

    public int getPinNumber() {
        return pinNumber;
    }

    public void setPinNumber(int pinNumber) {
        this.pinNumber = pinNumber;
    }

    public PinInfo getPinInfo() {
        return pinInfo;
    }

    public void setPinInfo(PinInfo pinInfo) {
        this.pinInfo = pinInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EditPinInfo)) return false;
        EditPinInfo that = (EditPinInfo) o;
        return pinNumber == that.pinNumber && Objects.equals(pinInfo, that.pinInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pinNumber, pinInfo);
    }

    @Override
    @NonNull
    public String toString() {
        return "EditPinInfo{" +
                "pinNumber=" + pinNumber +
                ", pinInfo=" + pinInfo +
                '}';
    }
}
