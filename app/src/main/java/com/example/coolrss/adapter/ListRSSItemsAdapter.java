package com.example.coolrss.adapter;

/*
 * Created by dutnguyen on 4/17/2020.
 */

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coolrss.R;
import com.example.coolrss.model.RSSItem;
import com.example.coolrss.utils.StringUtils;
import com.google.android.material.textview.MaterialTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ListRSSItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String LOG_TAG = ListRSSItemsAdapter.class.getSimpleName();
    private Context mContext;
    private List<RSSItem> mListItems = new ArrayList<>();

    public ListRSSItemsAdapter(Context context) {
        mContext = context;
    }

    // Inflates view present a RSS item in RecyclerView
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.rss_item, parent, false);
        return new RSSItemViewHolder(view);
    }

    // Bind the data at specified position
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final RSSItem currentItem = mListItems.get(position);
        if (holder instanceof RSSItemViewHolder) {
            final MaterialTextView title = ((RSSItemViewHolder) holder).title;
            final MaterialTextView description = ((RSSItemViewHolder) holder).description;
            final MaterialTextView time = ((RSSItemViewHolder) holder).time;
            final ImageView image = ((RSSItemViewHolder) holder).image;

            title.setText(currentItem.getTitle());
            description.setText(currentItem.getDescription());
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
            // format date time to show diff to current time
            String timeFormat = StringUtils.getDiffDateTime(currentItem.getPubDateStr());
            if (timeFormat != null) {
                time.setText(timeFormat);
            } else {
                time.setText(currentItem.getPubDateStr());
            }

            ((RSSItemViewHolder) holder).onItemClickListener = new RSSItemViewHolder.OnItemClickListener() {
                @Override
                public void onClick(int position1) {
                    startWebView(currentItem.getLink());
                }
            };
        }
    }

    private void startWebView(String urlStr) {
        Uri uri = Uri.parse(urlStr);

        // create custom tabs intent
        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
        // set toolbar colors
        intentBuilder.setToolbarColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
        CustomTabsIntent customTabsIntent = intentBuilder.build();
        customTabsIntent.launchUrl(mContext, uri);
    }

    @Override
    public int getItemCount() {
        return mListItems.size();
    }

    public void setListContent(List<RSSItem> listItems) {
        this.mListItems = new ArrayList<>(listItems);
        // refresh all list items
        notifyItemRangeChanged(0, listItems.size());
    }

    static class RSSItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        MaterialTextView title;
        MaterialTextView time;
        MaterialTextView description;
        ImageView image;
        OnItemClickListener onItemClickListener;

        RSSItemViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_item);
            title = itemView.findViewById(R.id.text_title_item);
            time = itemView.findViewById(R.id.text_time_item);
            description = itemView.findViewById(R.id.text_description_item);

            // set item on click listener
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onClick(getLayoutPosition());
        }

        // item click listener
        interface OnItemClickListener {
            void onClick(int position);
        }
    }
}
