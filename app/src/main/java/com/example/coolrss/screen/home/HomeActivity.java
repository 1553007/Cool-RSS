package com.example.coolrss.screen.home;

/*
 * Created by dutnguyen on 4/17/2020.
 */

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.coolrss.R;
import com.example.coolrss.adapter.ListRSSFeedsAdapter;
import com.example.coolrss.adapter.TabAdapter;
import com.example.coolrss.model.RSSFeed;
import com.example.coolrss.screen.home.detailrssfeed.DetailRSSFeedFragment;
import com.example.coolrss.screen.home.history.HistoryFragment;
import com.example.coolrss.screen.home.readmore.ReadMoreFragment;
import com.google.android.material.tabs.TabLayout;

public class HomeActivity extends AppCompatActivity implements ListRSSFeedsAdapter.OnFeedItemClickListener,
        ReadMoreFragment.OnFeedLoadListener, DetailRSSFeedFragment.OnItemsLoadListener {
    private String LOG_TAG = HomeActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private TabAdapter mTabAdapter;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ReadMoreFragment mReadMoreFragment;
    private DetailRSSFeedFragment mDetailRSSFeedFragment;
    private HistoryFragment mHistoryFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();
        initViews();
    }

    private void setupToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    private void initViews() {
        mViewPager = findViewById(R.id.viewpager);
        mTabLayout = findViewById(R.id.tabs);

        mTabAdapter = new TabAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mReadMoreFragment = new ReadMoreFragment();
        mTabAdapter.addFragment(mReadMoreFragment, "Read more");
        mDetailRSSFeedFragment = new DetailRSSFeedFragment();
        mTabAdapter.addFragment(mDetailRSSFeedFragment, "Detail");
        mHistoryFragment = new HistoryFragment();
        mTabAdapter.addFragment(mHistoryFragment, "History");

        mViewPager.setAdapter(mTabAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onBackPressed() {
        int currentItemIndex = mViewPager.getCurrentItem();
        if (currentItemIndex > 0) {
            mViewPager.setCurrentItem(currentItemIndex - 1);
        } else {
            super.onBackPressed();
        }
    }

    // TODO: navigate to Detail RSS Feed Screen : temp
    // setup listener when a RSS item clicked
    // navigate to Detail RSS Feed Screen
    @Override
    public void onClick(RSSFeed rssFeed) {
        mViewPager.setCurrentItem(1);
        mDetailRSSFeedFragment.onReceiveRSSFeed(rssFeed);
    }

    // setup listener when a new RSS feed loaded
    // update History tab
    @Override
    public void onFeedLoad() {
        mHistoryFragment.onRefresh();
    }

    // setup listener when a list of new RSS Items in RSS Feed loaded
    // update list RSS Feed in Read more tab
    @Override
    public void onListItemsLoad(RSSFeed feed) {
        mReadMoreFragment.onRefresh(feed);
    }
}
