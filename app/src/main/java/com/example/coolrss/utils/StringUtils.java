package com.example.coolrss.utils;

/*
 * Created by dutnguyen on 4/20/2020.
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    public static String getImageUrlInString(String inputStr) {
        String regex = "https?:/(?:/[^/]+)+\\.(?:jpg|gif|png)";
        Pattern pat = Pattern.compile(regex);
        Matcher matcher = pat.matcher(inputStr);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

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
}
