package com.example.coolrss.screen.home.history;

/*
 * Created by dutnguyen on 4/23/2020.
 */

import com.example.coolrss.model.RSSFeed;
import com.example.coolrss.utils.ReturnObj;

import java.util.List;

public interface OnHistoryViewUpdateListener {
    void start();
    void stop(ReturnObj returnObj);
    void onSuccess(List<RSSFeed> rssFeedList);
    void onFailure(String errorMessage);
}
