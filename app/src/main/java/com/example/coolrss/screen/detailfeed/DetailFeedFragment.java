package com.example.coolrss.screen.detailfeed;

/*
 * Created by dutnguyen on 4/21/2020.
 */

import android.content.Context;
import android.os.Bundle;
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
import com.example.coolrss.model.RSSFeed;
import com.example.coolrss.model.RSSItem;
import com.example.coolrss.screen.home.readmore.ReadMoreFragment;
import com.example.coolrss.utils.ReturnObj;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class DetailFeedFragment extends Fragment
        implements DetailFeedView, OnDetailViewUpdateListener {
    private String LOG_TAG = ReadMoreFragment.class.getSimpleName();
    private Context mContext;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mListItemsRecyclerView;
    private MaterialTextView mTextEmpty;
    private ListRSSItemsAdapter mListRSSItemsAdapter;
    private RSSFeed mRSSFeed = new RSSFeed();
    private OnListUpdateListener onListUpdateListener;
    private DetailFeedPresenter mDetailFeedPresenter;
    private OnGetFeedTitleListener onGetFeedTitleListener;

    DetailFeedFragment(String feedLink) {
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
        // prepare listener for set title toolbar by feed title
        try {
            onGetFeedTitleListener = (OnGetFeedTitleListener) mContext;
        } catch (ClassCastException e) {
            throw new ClassCastException(mContext.toString() + " must implement DetailRSSFeedFragment.OnGetFeedTitleListener");
        }
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

    private void onRefresh() {
        mDetailFeedPresenter.getFeedDetail(mRSSFeed, this);
    }

    // RSS Items updated
    public interface OnListUpdateListener {
        void onListUpdate();
    }

    // RSS Items updated
    public interface OnGetFeedTitleListener {
        void getTitle(String title);
    }

    @Override
    public void setPresenter(DetailFeedPresenter presenter) {
        mDetailFeedPresenter = presenter;
    }

    @Override
    public void start() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void stop(ReturnObj retObject) {
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
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onListHasUpdate() {
        onListUpdateListener.onListUpdate();
    }

    @Override
    public void onSuccess(RSSFeed feed) {
        mRSSFeed = feed;
        refreshListItems();
        onGetFeedTitleListener.getTitle(feed.getTitle());
    }

    @Override
    public void onFailure(String errorMessage) {
        Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();
    }
}
