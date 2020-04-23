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
    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
    private final static SimpleDateFormat dateFormat_noZone = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
    private final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MMM yyyy");
    private final static SimpleDateFormat sqlDateFormat = new SimpleDateFormat("yyyy/MM/DDD.HH:mm:ss.Z");

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
        webUrl = removeHttpInUrl(webUrl);
        if (webUrl.indexOf("/") > 0) {
            webUrl = webUrl.substring(0, webUrl.indexOf("/"));
        }
        return "http://logo.clearbit.com/" + webUrl;
    }

    public static String removeHttpInUrl(String inputStr) {
        String startStr = "http://";
        if (inputStr.contains("https://")) {
            startStr = "https://";
        }
        return inputStr.replace(startStr, "");
    }

    public static String addHttpUrl(String inputStr) {
        String outputStr = inputStr;
        if (!inputStr.startsWith("http://") && !inputStr.startsWith("https://"))
            outputStr = "https://" + inputStr;
        return outputStr;
    }

    // get date time format like "2m", "1h", ...
    public static String getDiffDateTime(String datetimeStr) {
        Date date = Calendar.getInstance().getTime();
        try {
            date = dateFormat.parse(datetimeStr);
        } catch (ParseException e) {
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
                return simpleDateFormat.format(date);
            }
        }
        return null;
    }

    // generate String from date with format
    public static String getStringFromDate(Date date) {
        return dateFormat.format(date);
    }

    // generate String from date with format no Zone
    public static String getStringNoZoneFromDate(Date date) {
        return dateFormat_noZone.format(date);
    }

    // generate date from String with format
    public static Date getDateFromString(String inputStr) {
        try {
            return dateFormat.parse(inputStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Calendar.getInstance().getTime();
    }

    // generate String from date for save date time data in sql table
    public static String getSQLStringFromDate(Date date) {
        return sqlDateFormat.format(date);
    }

    // generate date from String with format
    public static Date getDateFromSQLString(String inputStr) {
        try {
            return sqlDateFormat.parse(inputStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Calendar.getInstance().getTime();
    }
}
