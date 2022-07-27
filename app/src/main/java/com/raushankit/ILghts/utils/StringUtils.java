package com.raushankit.ILghts.utils;

import android.text.format.DateFormat;

import androidx.annotation.StringRes;

import com.google.firebase.database.DatabaseError;
import com.raushankit.ILghts.R;

import java.util.Calendar;
import java.util.Locale;

public class StringUtils {

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

    public static String formattedTime(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        return DateFormat.format("dd-MM-yyyy hh:mm:ss aaa", cal).toString();
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
