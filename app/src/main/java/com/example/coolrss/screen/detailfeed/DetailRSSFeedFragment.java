package com.example.coolrss.screen.detailfeed;

/*
 * Created by dutnguyen on 4/21/2020.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.coolrss.R;
import com.example.coolrss.adapter.ListRSSItemsAdapter;
import com.example.coolrss.dbhelper.AppDatabaseHelper;
import com.example.coolrss.dbhelper.repository.RSSFeedRepository;
import com.example.coolrss.model.RSSFeed;
import com.example.coolrss.model.RSSItem;
import com.example.coolrss.screen.home.readmore.ReadMoreFragment;
import com.example.coolrss.utils.PermissionUtils;
import com.example.coolrss.utils.RSSUtils;
import com.example.coolrss.utils.ReturnObj;
import com.example.coolrss.utils.StringUtils;
import com.google.android.material.textview.MaterialTextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

public class DetailRSSFeedFragment extends Fragment {
    private String LOG_TAG = ReadMoreFragment.class.getSimpleName();
    private Context mContext;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mListItemsRecyclerView;
    private MaterialTextView mTextEmpty;
    private ListRSSItemsAdapter mListRSSItemsAdapter;
    private RSSFeed mRSSFeed = new RSSFeed();
    private RSSFeedRepository rssFeedRepository;
    private OnListUpdateListener onListUpdateListener;

    public DetailRSSFeedFragment(String feedLink) {
        mRSSFeed.setLink(feedLink);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        // prepare listener when a new feed is loaded
        try {
            onListUpdateListener = (OnListUpdateListener) mContext;
        } catch (ClassCastException e) {
            throw new ClassCastException(mContext.toString() + " must implement DetailRSSFeedFragment.OnListUpdateListener");
        }
        // get db instance for update RSS feeds
        rssFeedRepository = RSSFeedRepository.getInstance(AppDatabaseHelper.getInstance(mContext));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.rss_list_items, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initViews(view);
    }

    private void initViews(View view) {
        mSwipeRefreshLayout = view.findViewById(R.id.list_items_swipe_layout);
        mListItemsRecyclerView = view.findViewById(R.id.list_items_recycler_view);
        mTextEmpty = view.findViewById(R.id.text_list_items_empty);

        // Setup recycler view
        mListItemsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        LinearLayoutManager layoutManager = (LinearLayoutManager) mListItemsRecyclerView.getLayoutManager();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mListItemsRecyclerView.getContext(), layoutManager.getOrientation());
        mListItemsRecyclerView.addItemDecoration(dividerItemDecoration);
        mListRSSItemsAdapter = new ListRSSItemsAdapter(mContext);
        mListItemsRecyclerView.setAdapter(mListRSSItemsAdapter);

        // Setup listener when user pull swipe layout to refresh
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            // execute Get feed task if there is a link in RSS feed
            if (mRSSFeed != null && !mRSSFeed.getLink().isEmpty()) {
                onRefresh();
            } else {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        onRefresh();
    }

    // update current list
    private void refreshListItems() {
        if (mRSSFeed != null && !mRSSFeed.getLink().isEmpty()) {
            List<RSSItem> listItems = mRSSFeed.getListRSSItems();
            if (!listItems.isEmpty()) {
                mTextEmpty.setVisibility(View.INVISIBLE);
            } else {
                mTextEmpty.setVisibility(View.VISIBLE);
            }
            mListRSSItemsAdapter.setListContent(listItems);
        }
    }

    public void onRefresh() {
        new GetFeedTask().execute((Void) null);
    }

    // Perform get feed task in background thread
    private class GetFeedTask extends AsyncTask<Void, Void, ReturnObj> {
        private String urlStr;
        private RSSFeed retRSSFeed = new RSSFeed();
        private Context currentContext;
        private boolean hasUpdate = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            currentContext = mContext;
            mSwipeRefreshLayout.setRefreshing(true);
            urlStr = mRSSFeed.getLink();
        }

        @Override
        protected ReturnObj doInBackground(Void... voids) {
            if (urlStr.isEmpty()) {
                return new ReturnObj(ReturnObj.TYPE.UI_ERROR, "Please enter a valid url");
            }

            try {
                urlStr = StringUtils.addHttpUrl(urlStr);
                // try to parse RSS feed from URL using Internet
                retRSSFeed = RSSUtils.parseRSSFeedFromURL(urlStr);
                // add feed into db if get successful
                rssFeedRepository.add(retRSSFeed);
                hasUpdate = true;
            } catch (NetworkOnMainThreadException | XmlPullParserException | IOException e) {
                // if an exception occurs -> load list RSS feeds in db
                // include: no Internet exception
                retRSSFeed = rssFeedRepository.getFeed(urlStr).get(0);
                if (!PermissionUtils.isInternetAvailable(currentContext)) {
                    return new ReturnObj(ReturnObj.TYPE.CONNECTIVITY_EXCEPTION, "Please check your Internet connection.");
                }
                return new ReturnObj(ReturnObj.TYPE.ERROR_EXCEPTION, e.getMessage());
            }
            return new ReturnObj();
        }

        @Override
        protected void onPostExecute(ReturnObj retObject) {
            super.onPostExecute(retObject);
            if (retObject.isError()) {
                switch (retObject.getType()) {
                    case CONNECTIVITY_EXCEPTION:
                        // show no connection error (dialog / message)
                        Toast.makeText(mContext, retObject.getErrorMessage(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(mContext, "Loaded list RSS feeds from database successfully", Toast.LENGTH_LONG).show();
                        break;
                    case ERROR_EXCEPTION:
                    case UI_ERROR:
                        Toast.makeText(mContext, retObject.getErrorMessage(), Toast.LENGTH_SHORT).show();
                        break;
                    case NO_ERROR:

                        break;
                    default:
                }
            }
            if (hasUpdate) {
                onListUpdateListener.onListUpdate();
            }
            mRSSFeed = retRSSFeed;
            refreshListItems();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    // RSS Items updated
    public interface OnListUpdateListener {
        void onListUpdate();
    }
}
