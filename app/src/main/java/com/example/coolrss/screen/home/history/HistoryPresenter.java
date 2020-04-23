package com.example.coolrss.screen.home.history;

/*
 * Created by dutnguyen on 4/23/2020.
 */

import android.content.Context;
import android.os.AsyncTask;

import com.example.coolrss.dbhelper.AppDatabaseHelper;
import com.example.coolrss.dbhelper.repository.RSSFeedRepository;
import com.example.coolrss.model.RSSFeed;
import com.example.coolrss.utils.ReturnObj;

import java.util.ArrayList;
import java.util.List;

public class HistoryPresenter {
    private RSSFeedRepository rssFeedRepository;

    public HistoryPresenter(HistoryView view, Context context) {
        view.setPresenter(this);
        // get db instance for update RSS feeds
        rssFeedRepository = RSSFeedRepository.getInstance(AppDatabaseHelper.getInstance(context));
    }

    void getListFeedsHistory(OnHistoryViewUpdateListener listener) {
        new GetListFeedsHistoryTask(listener).execute((Void) null);
    }

    // Perform get feeds history task in background thread
    private class GetListFeedsHistoryTask extends AsyncTask<Void, Void, ReturnObj> {
        private List<RSSFeed> taskListFeeds;
        private OnHistoryViewUpdateListener taskListener;

        public GetListFeedsHistoryTask(OnHistoryViewUpdateListener listener) {
            taskListener = listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            taskListener.start();
        }

        @Override
        protected ReturnObj doInBackground(Void... voids) {
            // get list of all RSS feeds accessed from db
            taskListFeeds = new ArrayList<>(rssFeedRepository.getAll());
            return new ReturnObj();
        }

        @Override
        protected void onPostExecute(ReturnObj returnObj) {
            super.onPostExecute(returnObj);
            taskListener.onSuccess(taskListFeeds);
            taskListener.stop(returnObj);
        }
    }
}
