package com.oleg.hubal.accelbase.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.Toast;

import com.oleg.hubal.accelbase.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by User on 01.11.2016.
 */

public class Utility {

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager.getActiveNetworkInfo() != null) {
            return true;
        } else {
            showToast(context, Constants.CONNECT_INTERNET);
            return false;
        }
    }

    public static long getDelayFromEditText(Editable text) {
        long delay = 1;
        String delayText = text.toString();
        if (!delayText.equals("")) {
            delay = Long.parseLong(delayText);
        }
        return delay * 1000;
    }

    public static void showToast(Context context, String toast) {
        Toast.makeText(context, toast, Toast.LENGTH_LONG).show();
    }

    public static String formatDate(String milliSeconds) {
        Long millis = Long.parseLong(milliSeconds);
        SimpleDateFormat formatter =
                new SimpleDateFormat(Constants.DATE_YEAR_FORMAT, Locale.ENGLISH);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return formatter.format(calendar.getTime());
    }

    public static String formatDate(Long milliSeconds, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static boolean isUserInputValid(String email, String password, Context context) {
        if (TextUtils.isEmpty(email)) {
            Utility.showToast(context, context.getString(R.string.enter_email));
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            Utility.showToast(context, context.getString(R.string.enter_password));
            return false;
        }
        if (password.length() < 6) {
            Utility.showToast(context, context.getString(R.string.minimum_password));
            return false;
        }
        return true;
    }
}
