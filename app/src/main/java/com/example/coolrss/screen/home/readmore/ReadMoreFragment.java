package com.example.coolrss.screen.home.readmore;

/*
 * Created by dutnguyen on 4/17/2020.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Xml;
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
import com.example.coolrss.model.RSSFeed;
import com.example.coolrss.model.RSSItem;
import com.example.coolrss.utils.ReturnObj;
import com.example.coolrss.utils.StringUtils;
import com.example.coolrss.utils.ViewUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_rss, container, false);
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

        mSearchBoxEditText.setText("https://vnexpress.net/rss/the-thao.rss");
//        mSearchBoxEditText.setText("https://timesofindia.indiatimes.com/rssfeedstopstories.cms");
    }

    // Perform get feed task in background thread
    private class GetFeedTask extends AsyncTask<Void, Void, ReturnObj> {
        private String urlStr;
        private RSSFeed retRSSFeed;
        private Boolean isDone = false;
        private String mErrorMessage = "";

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
                if (!urlStr.startsWith("http://") && !urlStr.startsWith("https://"))
                    urlStr = "http://" + urlStr;

                retRSSFeed = parseRSSFeed(urlStr);
            } catch (NetworkOnMainThreadException | XmlPullParserException | IOException e) {
                return new ReturnObj(true, ReturnObj.TYPE.EXCEPTION,
                        e.getMessage());
            }
            return new ReturnObj(false);
        }

        @Override
        protected void onPostExecute(ReturnObj obj) {
            super.onPostExecute(obj);
            mSwipeRefreshLayout.setRefreshing(false);
            if (!obj.isError()) {
                List<RSSFeed> listRSSFeeds = new ArrayList<>();
                listRSSFeeds.add(retRSSFeed);
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
        }
    }

    private void setList(List<RSSFeed> listItems) {
        if (!listItems.isEmpty()) {
            mTextEmpty.setVisibility(View.INVISIBLE);
        } else {
            mTextEmpty.setVisibility(View.VISIBLE);
        }
        mListRSSFeedsAdapter.setListContent(listItems);
    }

    // Parse RSS Feed from a URL
    public RSSFeed parseRSSFeed(String inputURL) throws XmlPullParserException,
            IOException {
        RSSFeed rssFeed = new RSSFeed();
        String title = "";
        String link = "";
        String description = "";
        String image = "";
        String pubDate = "";
        String currentTag = "";
        List<RSSItem> itemList = new ArrayList<>();

        URL url = new URL(inputURL);
        InputStream inputStream = url.openConnection().getInputStream();
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, null);

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = parser.getName();
                if (name != null) {
                    switch (eventType) {
                        case XmlPullParser.START_TAG:
                            if (name.equalsIgnoreCase("item") || name.equalsIgnoreCase("image")) {
                                currentTag = name;
                            }
                            if (!currentTag.equalsIgnoreCase("image") && parser.next() == XmlPullParser.TEXT) {
                                String result = parser.getText();
                                if (name.equalsIgnoreCase("title")) {
                                    title = result;
                                } else if (name.equalsIgnoreCase("description")) {
                                    image = StringUtils.getImageUrlInString(result);
                                    if (result.contains("</br>")) {
                                        description = result.substring(result.indexOf("</br>") + 5);
                                    } else {
                                        description = result;
                                    }
                                } else if (name.equalsIgnoreCase("link")) {
                                    link = result;
                                } else if (name.equalsIgnoreCase("pubDate")) {
                                    pubDate = result;
                                }
                                if (!title.isEmpty() && !description.isEmpty() && !link.isEmpty()) {
                                    if (currentTag.equalsIgnoreCase("item")) {
                                        itemList.add(new RSSItem(title, description, link, pubDate, image));
                                    } else {
                                        rssFeed.setTitle(title);
                                        rssFeed.setDescription(description);
                                        rssFeed.setLink(link);
                                    }
                                    title = "";
                                    link = "";
                                    description = "";
                                }
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            if (name.equalsIgnoreCase("item") || name.equalsIgnoreCase("image")) {
                                currentTag = "";
                            }
                            break;
                    }
                }
                eventType = parser.next();
            }
            rssFeed.setListRSSItems(itemList);
            return rssFeed;
        } finally {
            inputStream.close();
        }
    }
}
