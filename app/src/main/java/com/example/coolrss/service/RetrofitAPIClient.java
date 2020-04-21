package com.example.coolrss.service;

/*
 * Created by dutnguyen on 4/18/2020.
 */

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/*
    Currently use SimpleXmlConverterFactory, which is deprecated
    Maybe we can switch to use API to convert rss to json
    https://api.rss2json.com/v1/api.json?rss_url=<link to rss feed>
 */
public class RetrofitAPIClient {
    private static Retrofit instance = null;

    public static Retrofit getInstance(Context context) {
        if (instance == null) {
            OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new NetworkConnectionInterceptor(context))
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS);
            instance = new Retrofit.Builder().baseUrl("http://localhost/")
                    .addConverterFactory(SimpleXmlConverterFactory.create())
                    .client(okHttpClient.build())
                    .build();
        }
        return instance;
    }
}
