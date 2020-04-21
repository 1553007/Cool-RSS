package com.example.coolrss.screen.home.history;

/*
 * Created by dutnguyen on 4/21/2020.
 */

import android.content.Context;
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
import com.example.coolrss.screen.home.readmore.ReadMoreFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

public class HistoryFragment extends Fragment {
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
        return inflater.inflate(R.layout.rss_list_feeds, container, false);
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

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mSwipeRefreshLayout.setRefreshing(false);
//            ViewUtils.hideKeyboard(mSearchBoxEditText.getRootView(), mContext);
//            mSearchBoxEditText.clearFocus();
//            new GetFeedTask().execute((Void) null);
        });
    }
}
