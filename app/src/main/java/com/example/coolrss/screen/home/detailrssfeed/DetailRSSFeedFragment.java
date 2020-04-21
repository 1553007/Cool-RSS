package com.example.coolrss.screen.home.detailrssfeed;

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
import com.example.coolrss.adapter.ListRSSItemsAdapter;
import com.example.coolrss.screen.home.readmore.ReadMoreFragment;
import com.google.android.material.textview.MaterialTextView;

public class DetailRSSFeedFragment extends Fragment {
    private String LOG_TAG = ReadMoreFragment.class.getSimpleName();
    private Context mContext;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mListItemsRecyclerView;
    private MaterialTextView mTextEmpty;
    private ListRSSItemsAdapter mListRSSItemsAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
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
            mSwipeRefreshLayout.setRefreshing(false);
//            ViewUtils.hideKeyboard(mSearchBoxEditText.getRootView(), mContext);
//            mSearchBoxEditText.clearFocus();
//            new GetFeedTask().execute((Void) null);
        });
    }
}
