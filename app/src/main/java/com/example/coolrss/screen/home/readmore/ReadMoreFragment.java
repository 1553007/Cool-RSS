package com.example.coolrss.screen.home.readmore;

/*
 * Created by dutnguyen on 4/17/2020.
 */

import android.content.Context;
import android.os.AsyncTask;
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
import com.example.coolrss.dbhelper.AppDatabaseHelper;
import com.example.coolrss.dbhelper.repository.RSSFeedRepository;
import com.example.coolrss.model.RSSFeed;
import com.example.coolrss.utils.PermissionUtils;
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
        // prepare listener when a new feed is loaded
        try {
            onFeedLoadListener = (OnFeedLoadListener) mContext;
        } catch (ClassCastException e) {
            throw new ClassCastException(mContext.toString() + " must implement ReadMoreFragment.OnFeedLoadListener");
        }
        // get db instance for update RSS feeds
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

        // Setup listener when user click Search button on keyboard
        mSearchBoxEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                ViewUtils.hideKeyboard(v, mContext);
                mSearchBoxEditText.clearFocus();
                new GetFeedTask().execute((Void) null);
                return true;
            }
            return false;
        });

        // Setup listener when user pull swipe layout to refresh
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            ViewUtils.hideKeyboard(mSearchBoxEditText.getRootView(), mContext);
            mSearchBoxEditText.clearFocus();
            new GetFeedTask().execute((Void) null);
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

    // Perform get feed task in background thread
    private class GetFeedTask extends AsyncTask<Void, Void, ReturnObj> {
        private String urlStr;
        private List<RSSFeed> retListFeeds;
        private Context currentContext;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            currentContext = mContext;
            mSwipeRefreshLayout.setRefreshing(true);
            mSearchBoxInputLayout.setErrorEnabled(false);
            // get input text from Search box
            urlStr = mSearchBoxEditText.getText().toString().trim();
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
        protected void onPostExecute(ReturnObj retObject) {
            super.onPostExecute(retObject);
            if (retObject.isError()) {
                switch (retObject.getType()) {
                    case CONNECTIVITY_EXCEPTION:
                        Toast.makeText(mContext, "Loaded list RSS feeds from database successfully", Toast.LENGTH_LONG).show();
                        // show no connection error (dialog / message)
                        mSearchBoxInputLayout.setError(retObject.getErrorMessage());
                        mSearchBoxInputLayout.setErrorEnabled(true);
                        break;
                    case ERROR_EXCEPTION:
                    case UI_ERROR:
                        mSearchBoxInputLayout.setError(retObject.getErrorMessage());
                        mSearchBoxInputLayout.setErrorEnabled(true);
                        break;
                    case NO_ERROR:

                        break;
                    default:
                }
            }
            setList(retListFeeds);
            mSwipeRefreshLayout.setRefreshing(false);
        }
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
