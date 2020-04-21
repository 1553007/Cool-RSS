package com.example.coolrss.service;

/*
 * Created by dutnguyen on 4/18/2020.
 */

import android.content.Context;

import com.example.coolrss.exception.NoConnectivityException;
import com.example.coolrss.utils.PermissionUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

// Method is called before each and every API call
public class NetworkConnectionInterceptor implements Interceptor {
    private Context mContext;

    public NetworkConnectionInterceptor(Context context) {
        mContext = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (!PermissionUtils.isInternetAvailable(mContext)) {
            throw new NoConnectivityException();
        }
        Request.Builder builder = chain.request().newBuilder();
        return chain.proceed(builder.build());
    }
}
