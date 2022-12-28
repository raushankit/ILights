package com.raushankit.ILghts;

import android.app.Application;

public class BaseApp extends Application {

    private int themeIndex;

    public int getThemeIndex() {
        return themeIndex;
    }

    public void setThemeIndex(int themeIndex) {
        this.themeIndex = themeIndex;
    }
}
