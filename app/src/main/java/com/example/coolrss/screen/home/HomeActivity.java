package com.example.coolrss.screen.home;

/*
 * Created by dutnguyen on 4/17/2020.
 */

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.coolrss.R;
import com.example.coolrss.adapter.ListRSSFeedsAdapter;
import com.example.coolrss.adapter.TabAdapter;
import com.example.coolrss.screen.detailfeed.DetailFeedActivity;
import com.example.coolrss.screen.home.history.HistoryFragment;
import com.example.coolrss.screen.home.history.HistoryPresenter;
import com.example.coolrss.screen.home.readmore.ReadMoreFragment;
import com.example.coolrss.screen.home.readmore.ReadMorePresenter;
import com.google.android.material.tabs.TabLayout;

public class HomeActivity extends AppCompatActivity
        implements ListRSSFeedsAdapter.OnFeedItemClickListener, ReadMoreFragment.OnFeedLoadListener {
    private String LOG_TAG = HomeActivity.class.getSimpleName();
    public static final int OPEN_DETAIL_FEED_ACTIVITY = 1412;
    private Toolbar mToolbar;
    private TabAdapter mTabAdapter;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ReadMoreFragment mReadMoreFragment;
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
        new ReadMorePresenter(mReadMoreFragment, this);
        mTabAdapter.addFragment(mReadMoreFragment, "Read more");
        mHistoryFragment = new HistoryFragment();
        new HistoryPresenter(mHistoryFragment, this);
        mTabAdapter.addFragment(mHistoryFragment, "History");

        mViewPager.setAdapter(mTabAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    // setup listener when a new RSS feed loaded
    // update History tab
    @Override
    public void onFeedLoad() {
        mHistoryFragment.onRefresh();
    }

    // setup listener when a RSS Feed clicked
    // navigate to Detail RSS Feed Screen
    @Override
    public void onClick(String feedLink) {
        Intent intent = new Intent(HomeActivity.this, DetailFeedActivity.class);
        intent.putExtra("DETAIL_FEED_LINK", feedLink);
        startActivityForResult(intent, OPEN_DETAIL_FEED_ACTIVITY);
    }

    // handle on back from Detail Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_DETAIL_FEED_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                boolean isFeedUpdated = data.getBooleanExtra("isFeedUpdated", false);
                if (isFeedUpdated) {
                    // update "Last update" information in RSS feed has updated
                    mHistoryFragment.onRefresh();
                }
            }
        }
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
}
