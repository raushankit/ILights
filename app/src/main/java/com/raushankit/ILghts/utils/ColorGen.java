package com.raushankit.ILghts.utils;

import android.graphics.Color;

import androidx.annotation.ColorInt;

import java.util.Random;

public class ColorGen {

    @ColorInt
    public static int getRandomDarkColor(float factor){
        Random randomGenerator = new Random();
        int color = Color.rgb((randomGenerator.nextInt(255)%156 + 100),
                (randomGenerator.nextInt(255)%156) + 100,
                (randomGenerator.nextInt(255)%156 + 100));
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        return Color.argb(a, Math.max((int) (r * factor), 0),
                Math.max((int) (g * factor), 0),
                Math.max((int) (b * factor), 0));
    }
}
