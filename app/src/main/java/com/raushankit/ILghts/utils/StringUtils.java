package com.raushankit.ILghts.utils;

import android.text.format.DateFormat;

import androidx.annotation.StringRes;

import com.google.firebase.database.DatabaseError;
import com.raushankit.ILghts.R;

import java.util.Calendar;
import java.util.Locale;

public class StringUtils {

    private static final Integer[] themes = {
            R.style.Theme_ILights_1,
            R.style.Theme_ILights_2,
            R.style.Theme_ILights_3,
            R.style.Theme_ILights_4,
            R.style.Theme_ILights_5,
            R.style.Theme_ILights_6,
            R.style.Theme_ILights_7,
            R.style.Theme_ILights_8,
            R.style.Theme_ILights_9,
            R.style.Theme_ILights_10
    };

    public static final int[] colors = {
            R.color.shamrock_green,
            R.color.sky_blue,
            R.color.pink,
            R.color.google_red,
            R.color.scarlet_red,
            R.color.monte_carlo,
            R.color.ultra_blue,
            R.color.brunswick_green,
            R.color.dying_rose,
            R.color.dull_pink
    };

    public static final String[] colorNames = {
            "shamrock green", "deep sky blue", "neon pink",
            "red orange", "scarlet red", "monte carlo",
            "ultra blue", "brunswick green", "dying rose",
            "dull pink"
    };

    public static String capitalize(String str) {
        if (str == null) return null;
        String[] splits = str.toLowerCase(Locale.getDefault()).split(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < splits.length; i++) {
            String eachWord = splits[i];
            if (i > 0 && eachWord.length() > 0) {
                sb.append(" ");
            }
            String cap = eachWord.substring(0, 1).toUpperCase(Locale.ROOT)
                    + eachWord.substring(1);
            sb.append(cap);
        }
        return sb.toString();
    }

    public static String formattedTime(Long millis) {
        if(millis == null) { return  ""; }
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        return DateFormat.format("dd-MM-yyyy hh:mm:ss aaa", cal).toString();
    }

    public static long TIMESTAMP(){
        return Calendar.getInstance()
                .getTimeInMillis();
    }

    public static int getTheme(int index) {
        return (int) themes[Math.max(0, Math.min(themes.length - 1, index))];
    }

    @StringRes
    public static int getDataBaseErrorMessageFromCode(int code){
        switch (code){
            case DatabaseError.DISCONNECTED:
                return R.string.database_error_disconnected;
            case DatabaseError.NETWORK_ERROR:
                return R.string.database_error_network_error;
            case DatabaseError.MAX_RETRIES:
                return R.string.database_error_max_retries;
            case DatabaseError.PERMISSION_DENIED:
                return R.string.database_error_permission_denied;
            case DatabaseError.UNAVAILABLE:
                return R.string.database_error_unavailable;
            default:
                return R.string.database_error_unknown_error;
        }
    }
}
