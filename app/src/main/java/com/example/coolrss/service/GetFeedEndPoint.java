package com.example.coolrss.service;

/*
 * Created by dutnguyen on 4/18/2020.
 */

import com.example.coolrss.model.RSSFeed;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface GetFeedEndPoint {
    @GET
    Call<RSSFeed> getFeed(@Url String url);
}
