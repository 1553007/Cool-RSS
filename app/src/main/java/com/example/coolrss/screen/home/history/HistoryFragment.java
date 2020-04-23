package com.example.coolrss.screen.home.history;

/*
 * Created by dutnguyen on 4/21/2020.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.coolrss.R;
import com.example.coolrss.adapter.ListRSSFeedsAdapter;
import com.example.coolrss.dbhelper.AppDatabaseHelper;
import com.example.coolrss.dbhelper.repository.RSSFeedRepository;
import com.example.coolrss.model.RSSFeed;
import com.example.coolrss.screen.home.readmore.ReadMoreFragment;
import com.example.coolrss.utils.ReturnObj;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {
    private String LOG_TAG = ReadMoreFragment.class.getSimpleName();
    private Context mContext;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mListFeedsRecyclerView;
    private MaterialTextView mTextEmpty;
    private ListRSSFeedsAdapter mListRSSFeedsAdapter;
    private RSSFeedRepository rssFeedRepository;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        // get db instance for update RSS feeds
        rssFeedRepository = RSSFeedRepository.getInstance(AppDatabaseHelper.getInstance(mContext));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.history_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initViews(view);
    }

    private void initViews(View view) {
        mSwipeRefreshLayout = view.findViewById(R.id.list_feeds_swipe_layout);
        mListFeedsRecyclerView = view.findViewById(R.id.list_feeds_recycler_view);
        mTextEmpty = view.findViewById(R.id.text_list_feeds_empty);

        // Setup recycler view
        mListFeedsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        LinearLayoutManager layoutManager = (LinearLayoutManager) mListFeedsRecyclerView.getLayoutManager();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mListFeedsRecyclerView.getContext(), layoutManager.getOrientation());
        mListFeedsRecyclerView.addItemDecoration(dividerItemDecoration);
        mListRSSFeedsAdapter = new ListRSSFeedsAdapter(mContext);
        mListFeedsRecyclerView.setAdapter(mListRSSFeedsAdapter);

        // Setup listener when user pull swipe layout to refresh
        mSwipeRefreshLayout.setOnRefreshListener(this::onRefresh);
        onRefresh();
    }

    // update current list
    private void setList(List<RSSFeed> listItems) {
        if (!listItems.isEmpty()) {
            mTextEmpty.setVisibility(View.INVISIBLE);
        } else {
            mTextEmpty.setVisibility(View.VISIBLE);
        }
        mListRSSFeedsAdapter.setListContent(listItems);
    }

    public void onRefresh() {
        new GetFeedTask().execute((Void) null);
    }

    // Perform get feed task in background thread
    private class GetFeedTask extends AsyncTask<Void, Void, ReturnObj> {
        private List<RSSFeed> retListFeeds;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mSwipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected ReturnObj doInBackground(Void... voids) {
            // get list of all RSS feeds accessed from db
            retListFeeds = new ArrayList<>(rssFeedRepository.getAll());
            return new ReturnObj();
        }

        @Override
        protected void onPostExecute(ReturnObj returnObj) {
            super.onPostExecute(returnObj);
            if (!returnObj.isError()) {
                setList(retListFeeds);
            } else {
                switch (returnObj.getType()) {
                    case ERROR_EXCEPTION:
                        // handle exception error
                    case UI_ERROR:
                        // show error message
                        break;
                    case NO_ERROR:

                        break;
                    default:
                }
            }
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
