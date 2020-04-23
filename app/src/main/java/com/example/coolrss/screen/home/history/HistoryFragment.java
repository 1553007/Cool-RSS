package com.example.coolrss.screen.home.history;

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
import com.example.coolrss.adapter.ListRSSFeedsAdapter;
import com.example.coolrss.model.RSSFeed;
import com.example.coolrss.screen.home.readmore.ReadMoreFragment;
import com.example.coolrss.utils.ReturnObj;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class HistoryFragment extends Fragment
        implements HistoryView, OnHistoryViewUpdateListener {
    private String LOG_TAG = ReadMoreFragment.class.getSimpleName();
    private Context mContext;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mListFeedsRecyclerView;
    private MaterialTextView mTextEmpty;
    private ListRSSFeedsAdapter mListRSSFeedsAdapter;
    private HistoryPresenter mHistoryPresenter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
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
        mHistoryPresenter.getListFeedsHistory(this);
    }

    @Override
    public void setPresenter(HistoryPresenter presenter) {
        mHistoryPresenter = presenter;
    }

    @Override
    public void start() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void stop(ReturnObj retObject) {
        if (retObject.isError()) {
            switch (retObject.getType()) {
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

    @Override
    public void onSuccess(List<RSSFeed> rssFeedList) {
        setList(rssFeedList);
    }

    @Override
    public void onFailure(String errorMessage) {
        Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();
    }
}
