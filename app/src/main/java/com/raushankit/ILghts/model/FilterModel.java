package com.raushankit.ILghts.model;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import java.util.Objects;

@SuppressWarnings("unused")
@Keep
public class FilterModel {

    private String fieldName;

    private String type;

    private String value;

    public static class Type {
        public static final String FIELD = "field";
        public static final String NULL = "null";
    }

    public FilterModel() {}

    public FilterModel(String fieldName, String type, String value) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FilterModel that = (FilterModel) o;
        return Objects.equals(fieldName, that.fieldName) && Objects.equals(type, that.type) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldName, type, value);
    }

    @NonNull
    @Override
    public String toString() {
        return "FilterModel{" +
                "fieldName='" + fieldName + '\'' +
                ", type='" + type + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
