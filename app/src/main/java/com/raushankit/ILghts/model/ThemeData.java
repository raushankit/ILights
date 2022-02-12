package com.raushankit.ILghts.model;

import androidx.annotation.Keep;

@Keep
@SuppressWarnings("unused")
public class ThemeData {

    private String themeType;

    private boolean batterySaverOn;

    public ThemeData(){}

    public ThemeData(String themeType, boolean batterySaverOn) {
        this.themeType = themeType;
        this.batterySaverOn = batterySaverOn;
    }

    public String getThemeType() {
        return themeType;
    }

    public boolean isBatterySaverOn() {
        return batterySaverOn;
    }
}
