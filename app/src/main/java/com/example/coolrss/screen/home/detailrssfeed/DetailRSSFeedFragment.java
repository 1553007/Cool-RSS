package com.example.coolrss.screen.home.detailrssfeed;

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
import com.example.coolrss.screen.home.readmore.ReadMoreFragment;
import com.example.coolrss.utils.RSSUtils;
import com.example.coolrss.utils.ReturnObj;
import com.example.coolrss.utils.StringUtils;
import com.google.android.material.textview.MaterialTextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class DetailRSSFeedFragment extends Fragment {
    private String LOG_TAG = ReadMoreFragment.class.getSimpleName();
    private Context mContext;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mListItemsRecyclerView;
    private MaterialTextView mTextEmpty;
    private ListRSSItemsAdapter mListRSSItemsAdapter;
    private RSSFeed mRSSFeed = new RSSFeed();
    private RSSFeedRepository rssFeedRepository;
    private OnItemsLoadListener onItemsLoadListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        try {
            onItemsLoadListener = (OnItemsLoadListener) mContext;
        } catch (ClassCastException e) {
            throw new ClassCastException(mContext.toString() + " must implement DetailRSSFeedFragment.OnItemsLoadListener");
        }
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

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            // execute Get feed task if there is a link in RSS feed
            if (mRSSFeed != null && !mRSSFeed.getLink().isEmpty()) {
                new GetFeedTask().execute((Void) null);
            } else {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void onReceiveRSSFeed(RSSFeed rssFeed) {
        setFeed(rssFeed);
    }

    // Perform get feed task in background thread
    private class GetFeedTask extends AsyncTask<Void, Void, ReturnObj> {
        private String urlStr;
        private RSSFeed retRSSFeed = new RSSFeed();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mSwipeRefreshLayout.setRefreshing(true);
            urlStr = mRSSFeed.getLink();
        }

        @Override
        protected ReturnObj doInBackground(Void... voids) {
            if (urlStr.isEmpty()) {
                return new ReturnObj(true, ReturnObj.TYPE.UI_ERROR,
                        "Enter a valid url");
            }

            try {
                urlStr = StringUtils.addHttpUrl(urlStr);
                retRSSFeed = RSSUtils.parseRSSFeedFromURL(urlStr);
            } catch (NetworkOnMainThreadException | XmlPullParserException | IOException e) {
                return new ReturnObj(true, ReturnObj.TYPE.EXCEPTION,
                        e.getMessage());
            }
            return new ReturnObj(false);
        }

        @Override
        protected void onPostExecute(ReturnObj obj) {
            super.onPostExecute(obj);
            if (obj.isError()) {
                switch (obj.getType()) {
                    case EXCEPTION:
                    case UI_ERROR:
                        Toast.makeText(mContext, obj.getErrorMessage(), Toast.LENGTH_SHORT).show();
                        break;
                    case NO_ERROR:

                        break;
                    default:
                }
            }
            setFeed(retRSSFeed);
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void setFeed(RSSFeed feed) {
        if (feed != null && !feed.getLink().isEmpty()) {
            mRSSFeed = feed;
            if (!mRSSFeed.getListRSSItems().isEmpty()) {
                mTextEmpty.setVisibility(View.INVISIBLE);
            } else {
                mTextEmpty.setVisibility(View.VISIBLE);
            }
            mListRSSItemsAdapter.setListContent(mRSSFeed.getListRSSItems());
            onItemsLoadListener.onListItemsLoad(mRSSFeed);
            //TODO: update RSS Feed last build date
            //TODO: add RSS list Items
        }
    }

    // RSS Items new load -> update Read more list
    public interface OnItemsLoadListener {
        void onListItemsLoad(RSSFeed feed);
    }
}
