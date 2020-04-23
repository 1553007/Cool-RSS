package com.example.coolrss.screen.detailfeed;

/*
 * Created by dutnguyen on 4/23/2020.
 */

import com.example.coolrss.model.RSSFeed;
import com.example.coolrss.utils.ReturnObj;

public interface OnDetailViewUpdateListener {
    void start();
    void stop(ReturnObj returnObj);
    void onListHasUpdate();
    void onSuccess(RSSFeed feed);
    void onFailure(String errorMessage);
}
