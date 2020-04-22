package com.example.coolrss.utils;

/*
 * Created by dutnguyen on 4/20/2020.
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    // extract any image url from a string
    public static String getImageUrlInString(String inputStr) {
        String regex = "https?:/(?:/[^/]+)+\\.(?:jpg|gif|png)";
        Pattern pat = Pattern.compile(regex);
        Matcher matcher = pat.matcher(inputStr);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

    // get Logo of a website using free API clearbit
    public static String getLogoInWebsite(String webUrl) {
        String startStr = "http://";
        if (webUrl.contains("https://")) {
            startStr = "https://";
        }
        webUrl = webUrl.replace(startStr, "");
        if (webUrl.indexOf("/") > 0) {
            webUrl = webUrl.substring(0, webUrl.indexOf("/"));
        }
        return "http://logo.clearbit.com/" + webUrl;
    }

    // get date time format like "2m", "1h", ...
    public static String formatDateTime(String datetimeStr) {
        Date date = Calendar.getInstance().getTime();
        try {
            SimpleDateFormat originFotmat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
            date = originFotmat.parse(datetimeStr);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Long inputTimeMillis = date.getTime();
        Long currentTimeMillis = java.lang.System.currentTimeMillis();
        if (currentTimeMillis > inputTimeMillis) {
            long difference = currentTimeMillis - inputTimeMillis;
            long seconds = difference / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;

            if (seconds < 0) {
                return null;
            } else if (seconds < 60) {
                return seconds + "s";
            } else if (minutes < 60) {
                return minutes + "m";
            } else if (hours < 24) {
                return hours + "h";
            } else if (hours < 48) {
                return "Yesterday";
            } else {
                SimpleDateFormat convertFormat = new SimpleDateFormat("d MMM yyyy");
                return convertFormat.format(date);
            }
        }
        return null;
    }
}
