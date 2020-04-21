package com.example.coolrss.service;

/*
 * Created by dutnguyen on 4/18/2020.
 */

import com.example.coolrss.model.RSSFeed;

public interface GetFeedResultListener {
    void onSuccess(RSSFeed rssFeed);

    void onError(String errorMessage);
}
