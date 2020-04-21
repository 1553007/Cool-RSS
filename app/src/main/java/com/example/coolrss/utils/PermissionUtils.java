package com.example.coolrss.utils;

/*
 * Created by dutnguyen on 4/18/2020.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;

public class PermissionUtils {
    public static Boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return true;
                }
            }
        }
        return false;
    }
}
