package com.raushankit.ILghts.model;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import java.util.Objects;

@SuppressWarnings("unused")
@Keep
public class FilterModel implements Cloneable {

    private int fieldIndex;

    private String fieldName;

    private String type;

    private String value;

    @NonNull
    @Override
    public FilterModel clone() {
        try {
            return (FilterModel) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public static class Type {
        public static final String FIELD = "field";
        public static final String NULL = "null";
    }

    public FilterModel() {}

    public FilterModel(int fieldIndex, String fieldName, String type, String value) {
        this.fieldIndex = fieldIndex;
        this.fieldName = fieldName;
        this.type = type;
        this.value = value;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getFieldIndex() {
        return fieldIndex;
    }

    public void setFieldIndex(int fieldIndex) {
        this.fieldIndex = fieldIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FilterModel that = (FilterModel) o;
        return fieldIndex == that.fieldIndex && Objects.equals(fieldName, that.fieldName) && Objects.equals(type, that.type) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldIndex, fieldName, type, value);
    }

    @NonNull
    @Override
    public String toString() {
        return "FilterModel{" +
                "fieldIndex=" + fieldIndex +
                ", fieldName='" + fieldName + '\'' +
                ", type='" + type + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
