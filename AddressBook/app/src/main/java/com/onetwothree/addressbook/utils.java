package com.onetwothree.addressbook;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by liyang21 on 2019/6/25.
 */

public class utils {

    static private final String TAG = "Utils";

    static public String formatDate(long time) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        return format.format(new Date(time));
    }

    static public String formatDuration(long time) {
        long s = time % 60;
        long m = time / 60;
        long h = time / 60 / 60;
        StringBuilder strBuilder = new StringBuilder();
        if (h > 0) {
            strBuilder.append(h).append("小时");
        }
        if (m > 0) {
            strBuilder.append(m).append("分");
        }
        strBuilder.append(s).append("秒");
        return strBuilder.toString();
    }

    static public boolean isNumber(char c) {
        return c >= '0' && c <= '9';
    }

    static public String PhoneNumberDeformat(String Number) {
        String result = new String();
        for (int i = 0; i < Number.length(); ++ i) {
            char ch = Number.charAt(i);
            if (isNumber(ch)) {
                result = result.concat(String.valueOf(ch));
            }
        }
        return result;
    }

    static public Date GenerateDate(int Year, int Mon, int Day, int HH, int MM, int SS) {
        String tmp = String.valueOf(Year) + "-" + String.format("%02d", Mon) + "-" +
                String.format("%02d", Day) + " " + String.format("%02d", HH) + ":" +
                String.format("%02d", MM) + ":" + String.format("%02d", SS);
        Log.v(TAG, "generate String = " + tmp);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date date = sdf.parse(tmp);
            Log.v(TAG, "get time:" + String.valueOf(date.getTime()) + " " + date);
            return date;
        } catch (ParseException err) {
            Log.v(TAG, "wtf ???");
            return new Date();
        }
    }

}
