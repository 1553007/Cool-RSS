package com.example.coolrss.screen.home.readmore;

/*
 * Created by dutnguyen on 4/17/2020.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

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
import com.example.coolrss.utils.RSSUtils;
import com.example.coolrss.utils.ReturnObj;
import com.example.coolrss.utils.StringUtils;
import com.example.coolrss.utils.ViewUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadMoreFragment extends Fragment {
    private String LOG_TAG = ReadMoreFragment.class.getSimpleName();
    private Context mContext;
    private TextInputLayout mSearchBoxInputLayout;
    private TextInputEditText mSearchBoxEditText;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mListFeedsRecyclerView;
    private MaterialTextView mTextEmpty;
    private ListRSSFeedsAdapter mListRSSFeedsAdapter;
    private RSSFeedRepository rssFeedRepository;
    private OnFeedLoadListener onFeedLoadListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        try {
            onFeedLoadListener = (OnFeedLoadListener) mContext;
        } catch (ClassCastException e) {
            throw new ClassCastException(mContext.toString() + " must implement ReadMoreFragment.OnFeedLoadListener");
        }
        rssFeedRepository = RSSFeedRepository.getInstance(AppDatabaseHelper.getInstance(mContext));
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

        mSearchBoxEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                ViewUtils.hideKeyboard(v, mContext);
                mSearchBoxEditText.clearFocus();
                new GetFeedTask().execute((Void) null);
                return true;
            }
            return false;
        });

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            ViewUtils.hideKeyboard(mSearchBoxEditText.getRootView(), mContext);
            mSearchBoxEditText.clearFocus();
            new GetFeedTask().execute((Void) null);
        });

        // TODO: delete fixed RSS link
        mSearchBoxEditText.setText("https://vnexpress.net/rss/tin-moi-nhat.rss");
//        mSearchBoxEditText.setText("https://techcrunch.com/feed");
    }

    // Perform get feed task in background thread
    private class GetFeedTask extends AsyncTask<Void, Void, ReturnObj> {
        private String urlStr;
        private RSSFeed retRSSFeed;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mSwipeRefreshLayout.setRefreshing(true);
            mSearchBoxInputLayout.setErrorEnabled(false);
            urlStr = mSearchBoxEditText.getText().toString().trim();
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
            if (!obj.isError()) {
                List<RSSFeed> listRSSFeeds = new ArrayList<>();
                listRSSFeeds.add(retRSSFeed);
                rssFeedRepository.add(listRSSFeeds);
                setList(listRSSFeeds);
            } else {
                switch (obj.getType()) {
                    case EXCEPTION:
                    case UI_ERROR:
                        mSearchBoxInputLayout.setError(obj.getErrorMessage());
                        mSearchBoxInputLayout.setErrorEnabled(true);
                        break;
                    case NO_ERROR:

                        break;
                    default:
                }
            }
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void setList(List<RSSFeed> listItems) {
        if (!listItems.isEmpty()) {
            mTextEmpty.setVisibility(View.INVISIBLE);
        } else {
            mTextEmpty.setVisibility(View.VISIBLE);
        }
        mListRSSFeedsAdapter.setListContent(listItems);
        onFeedLoadListener.onFeedLoad();
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
