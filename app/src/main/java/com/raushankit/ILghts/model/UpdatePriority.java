package com.raushankit.ILghts.model;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

@Keep
@IgnoreExtraProperties
@SuppressWarnings("unused")
public class UpdatePriority {

    @PropertyName("update_priority")
    private int priority;

    public UpdatePriority(){

    }

    @PropertyName("update_priority")
    public int getPriority() {
        return priority;
    }

    @PropertyName("update_priority")
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @NonNull
    @Override
    public String toString() {
        return "UpdatePriority{" +
                "priority=" + priority +
                '}';
    }
}
