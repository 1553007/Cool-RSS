package com.example.coolrss.screen.detailfeed;

/*
 * Created by dutnguyen on 4/23/2020.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.example.coolrss.R;
import com.example.coolrss.screen.home.HomeActivity;

public class DetailFeedActivity extends AppCompatActivity implements DetailRSSFeedFragment.OnListUpdateListener {
    private String LOG_TAG = HomeActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private DetailRSSFeedFragment mDetailRSSFeedFragment;
    private boolean isFeedUpdated = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_feed);
        setupToolbar();
        initViews();
    }

    private void setupToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        // TODO: add back button top left toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationIcon(getDrawable(R.drawable.back_icon_white));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("isFeedUpdated", isFeedUpdated);
                setResult(Activity.RESULT_OK, returnIntent);
                onBackPressed();
            }
        });
    }

    private void initViews() {
        String fragmentTag = DetailRSSFeedFragment.class.getSimpleName();
        String feedLink = getIntent().getStringExtra("DETAIL_FEED_LINK");
        mDetailRSSFeedFragment = new DetailRSSFeedFragment(feedLink);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, mDetailRSSFeedFragment, fragmentTag).commit();
    }

    @Override
    public void onListUpdate() {
        isFeedUpdated = true;
    }
}
