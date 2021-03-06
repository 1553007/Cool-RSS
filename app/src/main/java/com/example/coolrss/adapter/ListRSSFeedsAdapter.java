package com.example.coolrss.adapter;

/*
 * Created by dutnguyen on 4/21/2020.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coolrss.R;
import com.example.coolrss.model.RSSFeed;
import com.example.coolrss.utils.StringUtils;
import com.google.android.material.textview.MaterialTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListRSSFeedsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String LOG_TAG = ListRSSFeedsAdapter.class.getSimpleName();
    private Context mContext;
    private List<RSSFeed> listRSSFeeds = new ArrayList<>();
    private OnFeedItemClickListener onFeedClickListener;

    public ListRSSFeedsAdapter(Context context) {
        mContext = context;
        try {
            onFeedClickListener = (OnFeedItemClickListener) mContext;
        } catch (ClassCastException e) {
            throw new ClassCastException(mContext.toString() + " must implement ListRSSFeedsAdapter.OnFeedItemClickListener");
        }
    }

    // Inflates view present a RSS item in RecyclerView
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.rss_feed, parent, false);
        return new RSSFeedViewHolder(view);
    }

    // Bind the data at specified position
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final RSSFeed currentItem = listRSSFeeds.get(position);
        if (holder instanceof RSSFeedViewHolder) {
            final MaterialTextView title = ((RSSFeedViewHolder) holder).title;
            final MaterialTextView link = ((RSSFeedViewHolder) holder).link;
            final ImageView image = ((RSSFeedViewHolder) holder).image;
            final MaterialTextView time = ((RSSFeedViewHolder) holder).time;

            title.setText(currentItem.getTitle());
            link.setText(StringUtils.removeHttpInUrl(currentItem.getLink()));
            if (!currentItem.getImage().isEmpty()) {
                Picasso.get().load(currentItem.getImage())
                        .placeholder(R.drawable.default_image)
                        .error(R.drawable.default_image)
                        .into(image);
            } else {
                // set default website image
                Picasso.get().load(StringUtils.getLogoInWebsite(currentItem.getLink()))
                        .placeholder(R.drawable.default_image)
                        .error(R.drawable.default_image)
                        .into(image);
            }
            // format date time
            Date date = StringUtils.getDateFromString(currentItem.getLastBuildDateStr());
            String timeFormat = StringUtils.getStringNoZoneFromDate(date);
            if (timeFormat != null) {
                time.setText("Last update: " + timeFormat);
            } else {
                time.setText("Last update: " + currentItem.getLastBuildDateStr());
            }

            ((RSSFeedViewHolder) holder).onFeedViewHolderClickListener = positionClicked -> {
                // send RSS Feed data to listener
                onFeedClickListener.onClick(listRSSFeeds.get(positionClicked).getLink());
            };
        }
    }

    @Override
    public int getItemCount() {
        return listRSSFeeds.size();
    }

    public void setListContent(List<RSSFeed> listItems) {
        this.listRSSFeeds = new ArrayList<>(listItems);
        // refresh all list items
        notifyItemRangeChanged(0, listItems.size());
    }

    static class RSSFeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        MaterialTextView title;
        MaterialTextView link;
        ImageView image;
        MaterialTextView time;
        OnFeedViewHolderClickListener onFeedViewHolderClickListener;

        RSSFeedViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_feed);
            link = itemView.findViewById(R.id.link_feed);
            image = itemView.findViewById(R.id.image_feed);
            time = itemView.findViewById(R.id.temp_time_feed);

            // set item on click listener
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onFeedViewHolderClickListener.onClick(getLayoutPosition());
        }

        // item view holder click listener
        interface OnFeedViewHolderClickListener {
            void onClick(int position);
        }
    }

    // RSS Feed clicked listener
    public interface OnFeedItemClickListener {
        void onClick(String feedLink);
    }
}
