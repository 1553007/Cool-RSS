package com.example.coolrss.screen.home.readmore;

/*
 * Created by dutnguyen on 4/23/2020.
 */

import android.content.Context;
import android.os.AsyncTask;

import com.example.coolrss.dbhelper.AppDatabaseHelper;
import com.example.coolrss.dbhelper.repository.RSSFeedRepository;
import com.example.coolrss.model.RSSFeed;
import com.example.coolrss.utils.PermissionUtils;
import com.example.coolrss.utils.RSSUtils;
import com.example.coolrss.utils.ReturnObj;
import com.example.coolrss.utils.StringUtils;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadMorePresenter {
    private RSSFeedRepository rssFeedRepository;
    private Context mContext;

    public ReadMorePresenter(ReadMoreView view, Context context) {
        view.setPresenter(this);
        mContext = context;
        // get db instance for update RSS feeds
        rssFeedRepository = RSSFeedRepository.getInstance(AppDatabaseHelper.getInstance(context));
    }

    void searchListFeeds(String searchText, OnReadMoreViewUpdateListener listener) {
        new SearchListFeedsTask(searchText, listener).execute((Void) null);
    }

    // Perform get feed task in background thread
    private class SearchListFeedsTask extends AsyncTask<Void, Void, ReturnObj> {
        private String urlStr;
        private List<RSSFeed> retListFeeds;
        private Context currentContext;
        private OnReadMoreViewUpdateListener taskListener;

        public SearchListFeedsTask(String searchText, OnReadMoreViewUpdateListener listener) {
            urlStr = searchText;
            taskListener = listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            currentContext = mContext;
            taskListener.start();
        }

        @Override
        protected ReturnObj doInBackground(Void... voids) {
            if (urlStr.isEmpty()) {
                return new ReturnObj(ReturnObj.TYPE.UI_ERROR, "Please enter a valid url");
            }

            try {
                urlStr = StringUtils.addHttpUrl(urlStr);
                // try to parse RSS feed from URL using Internet
                RSSFeed retRSSFeed = RSSUtils.parseRSSFeedFromURL(urlStr);
                // add feed into db if get successful
                rssFeedRepository.add(retRSSFeed);
                retListFeeds = new ArrayList<>();
                retListFeeds.add(retRSSFeed);
            } catch (XmlPullParserException | IOException e) {
                // if an exception occurs -> load list RSS feeds in db
                // include: no Internet exception
                retListFeeds = new ArrayList<>(rssFeedRepository.getFeed(urlStr));
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
            taskListener.onSuccess(retListFeeds);
            taskListener.stop(returnObj);
        }
    }
}
