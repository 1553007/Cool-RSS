package com.example.coolrss.screen.home.readmore;

/*
 * Created by dutnguyen on 4/17/2020.
 */

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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
import com.example.coolrss.utils.ReturnObj;
import com.example.coolrss.utils.ViewUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class ReadMoreFragment extends Fragment
        implements ReadMoreView, OnReadMoreViewUpdateListener {
    private String LOG_TAG = ReadMoreFragment.class.getSimpleName();
    private Context mContext;
    private TextInputLayout mSearchBoxInputLayout;
    private TextInputEditText mSearchBoxEditText;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mListFeedsRecyclerView;
    private MaterialTextView mTextEmpty;
    private ListRSSFeedsAdapter mListRSSFeedsAdapter;
    private OnFeedLoadListener onFeedLoadListener;
    private ReadMorePresenter mReadMorePresenter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        // prepare listener when a new feed is loaded
        try {
            onFeedLoadListener = (OnFeedLoadListener) mContext;
        } catch (ClassCastException e) {
            throw new ClassCastException(mContext.toString() + " must implement ReadMoreFragment.OnFeedLoadListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.read_rss_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initViews(view);
    }

    private void initViews(View view) {
        mSearchBoxInputLayout = view.findViewById(R.id.search_box_layout);
        mSearchBoxEditText = view.findViewById(R.id.search_box_input);
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

        // Setup listener when user click Search button on keyboard
        mSearchBoxEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                ViewUtils.hideKeyboard(v, mContext);
                mSearchBoxEditText.clearFocus();
                String searchStr = mSearchBoxEditText.getText().toString().trim();
                mReadMorePresenter.searchListFeeds(searchStr, this);
                return true;
            }
            return false;
        });

        // Setup listener when user pull swipe layout to refresh
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            ViewUtils.hideKeyboard(mSearchBoxEditText.getRootView(), mContext);
            mSearchBoxEditText.clearFocus();
            String searchStr = mSearchBoxEditText.getText().toString().trim();
            mReadMorePresenter.searchListFeeds(searchStr, this);
        });

        // TODO: delete fixed RSS link
        mSearchBoxEditText.setText("https://vnexpress.net/rss/tin-moi-nhat.rss");
    }

    // update current list
    private void setList(List<RSSFeed> listItems) {
        if (!listItems.isEmpty()) {
            mTextEmpty.setVisibility(View.INVISIBLE);
        } else {
            mTextEmpty.setVisibility(View.VISIBLE);
        }
        mListRSSFeedsAdapter.setListContent(listItems);
        // notice listener when a list of new RSS feeds is load
        onFeedLoadListener.onFeedLoad();
    }

    @Override
    public void setPresenter(ReadMorePresenter presenter) {
        mReadMorePresenter = presenter;
    }

    @Override
    public void start() {
        mSwipeRefreshLayout.setRefreshing(true);
        mSearchBoxInputLayout.setErrorEnabled(false);
    }

    @Override
    public void stop(ReturnObj returnObj) {
        if (returnObj.isError()) {
            switch (returnObj.getType()) {
                case CONNECTIVITY_EXCEPTION:
                    Toast.makeText(mContext, "Loaded list RSS feeds from database successfully", Toast.LENGTH_LONG).show();
                    // show no connection error (dialog / message)
                    mSearchBoxInputLayout.setError(returnObj.getErrorMessage());
                    mSearchBoxInputLayout.setErrorEnabled(true);
                    break;
                case ERROR_EXCEPTION:
                case UI_ERROR:
                    mSearchBoxInputLayout.setError(returnObj.getErrorMessage());
                    mSearchBoxInputLayout.setErrorEnabled(true);
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

    public void onRefresh(RSSFeed feed) {
        List<RSSFeed> listRSSFeeds = new ArrayList<>();
        listRSSFeeds.add(feed);
        setList(listRSSFeeds);
    }

    // RSS Feed new load -> update History list
    public interface OnFeedLoadListener {
        void onFeedLoad();
    }
}
