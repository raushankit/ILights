package com.raushankit.ILghts.utils;

import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Locale;

public class StringUtils {

    public static String capitalize(String str){
        if(str == null) return null;
        String[] splits = str.toLowerCase().split(" ");
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

    public static String formattedTime(long millis){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        return DateFormat.format("dd-MM-yyyy hh:mm:ss aaa", cal).toString();
    }
}
