package com.example.coolrss.screen.detailfeed;

/*
 * Created by dutnguyen on 4/23/2020.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.os.NetworkOnMainThreadException;

import com.example.coolrss.dbhelper.AppDatabaseHelper;
import com.example.coolrss.dbhelper.repository.RSSFeedRepository;
import com.example.coolrss.model.RSSFeed;
import com.example.coolrss.utils.PermissionUtils;
import com.example.coolrss.utils.RSSUtils;
import com.example.coolrss.utils.ReturnObj;
import com.example.coolrss.utils.StringUtils;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class DetailFeedPresenter {
    private RSSFeedRepository rssFeedRepository;
    private Context mContext;

    public DetailFeedPresenter(DetailFeedView view, Context context) {
        view.setPresenter(this);
        mContext = context;
        // get db instance for update RSS feeds
        rssFeedRepository = RSSFeedRepository.getInstance(AppDatabaseHelper.getInstance(context));
    }

    void getFeedDetail(RSSFeed feed, OnDetailViewUpdateListener listener) {
        new GetFeedDetailTask(feed, listener).execute((Void) null);
    }

    // Perform get feed detail task in background thread
    private class GetFeedDetailTask extends AsyncTask<Void, Void, ReturnObj> {
        private String urlStr;
        private RSSFeed taskRSSFeed;
        private Context currentContext;
        private boolean hasUpdate = false;
        private OnDetailViewUpdateListener taskListener;

        GetFeedDetailTask(RSSFeed rssFeed, OnDetailViewUpdateListener listener) {
            taskRSSFeed = rssFeed;
            taskListener = listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            currentContext = mContext;
            taskListener.start();
            // get link 2 times !!
            urlStr = taskRSSFeed.getLink();
        }

        @Override
        protected ReturnObj doInBackground(Void... voids) {
            if (urlStr.isEmpty()) {
                return new ReturnObj(ReturnObj.TYPE.UI_ERROR, "Please enter a valid url");
            }

            try {
                urlStr = StringUtils.addHttpUrl(urlStr);
                // try to parse RSS feed from URL using Internet
                taskRSSFeed = RSSUtils.parseRSSFeedFromURL(urlStr);
                // add feed into db if get successful
                rssFeedRepository.add(taskRSSFeed);
                hasUpdate = true;
            } catch (NetworkOnMainThreadException | XmlPullParserException | IOException e) {
                // if an exception occurs -> load list RSS feeds in db
                // include: no Internet exception
                taskRSSFeed = rssFeedRepository.getFeed(urlStr).get(0);
                if (!PermissionUtils.isInternetAvailable(currentContext)) {
                    return new ReturnObj(ReturnObj.TYPE.CONNECTIVITY_EXCEPTION, "Please check your Internet connection.");
                }
                return new ReturnObj(ReturnObj.TYPE.ERROR_EXCEPTION, e.getMessage());
            }
            return new ReturnObj();
        }

        @Override
        protected void onPostExecute(ReturnObj returnObj) {
            super.onPostExecute(returnObj);
            if (hasUpdate) {
                taskListener.onListHasUpdate();
            }
            taskListener.onSuccess(taskRSSFeed);
            taskListener.stop(returnObj);
        }
    }
}
