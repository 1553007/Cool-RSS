package com.example.coolrss.screen.detailfeed;

/*
 * Created by dutnguyen on 4/23/2020.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.example.coolrss.R;
import com.example.coolrss.screen.home.HomeActivity;
import com.google.android.material.textview.MaterialTextView;

public class DetailFeedActivity extends AppCompatActivity
        implements DetailFeedFragment.OnListUpdateListener, DetailFeedFragment.OnGetFeedTitleListener {
    private String LOG_TAG = HomeActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private MaterialTextView mToolbarTitle;
    private DetailFeedFragment mDetailFeedFragment;
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
        mToolbarTitle = findViewById(R.id.toolbar_title);
        setSupportActionBar(mToolbar);
        // add back button top left toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationIcon(getDrawable(R.drawable.back_icon_white));
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void initViews() {
        String fragmentTag = DetailFeedFragment.class.getSimpleName();
        String feedLink = getIntent().getStringExtra("DETAIL_FEED_LINK");
        mDetailFeedFragment = new DetailFeedFragment(feedLink);
        new DetailFeedPresenter(mDetailFeedFragment, this);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, mDetailFeedFragment, fragmentTag).commit();
    }

    // listen list update from Detail RSS Feed Fragment
    @Override
    public void onListUpdate() {
        isFeedUpdated = true;
    }

    // send data when user press back button
    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("isFeedUpdated", isFeedUpdated);
        setResult(Activity.RESULT_OK, returnIntent);
        super.onBackPressed();
    }

    // listen feed title from Detail RSS Feed Fragment
    @Override
    public void getTitle(String title) {
        // Change toolbar title ~ Feed title
        mToolbarTitle.setText(title);
    }
}
