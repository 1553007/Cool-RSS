package com.example.coolrss.service;

/*
 * Created by dutnguyen on 4/18/2020.
 */

import android.content.Context;

import com.example.coolrss.model.RSSFeed;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Url;

public class GetFeedService {
    private Context mContext;
    private Retrofit mRetrofit;
    private GetFeedEndPoint mGetFeedEndPoint;

    public GetFeedService(Context context) {
        mContext = context;
        mRetrofit = RetrofitAPIClient.getInstance(mContext);
        mGetFeedEndPoint = mRetrofit.create(GetFeedEndPoint.class);
    }

    public void getFeed(@Url String url, GetFeedResultListener onFinishedListener) {
        mGetFeedEndPoint.getFeed(url).enqueue(new Callback<RSSFeed>() {
            @Override
            public void onResponse(Call<RSSFeed> call, Response<RSSFeed> response) {
                int responseCode = response.raw().code();
                if (responseCode == 200) {
                    RSSFeed rssFeed = response.body();
                    onFinishedListener.onSuccess(rssFeed);
                } else {
                    onFinishedListener.onError("Please enter a valid url");
                }
            }

            @Override
            public void onFailure(Call<RSSFeed> call, Throwable t) {
                onFinishedListener.onError(t.getMessage());
            }
        });
    }
}
