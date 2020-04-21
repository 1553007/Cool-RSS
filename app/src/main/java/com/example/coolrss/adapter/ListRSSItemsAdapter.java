package com.example.coolrss.adapter;

/*
 * Created by dutnguyen on 4/17/2020.
 */

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coolrss.R;
import com.example.coolrss.model.RSSItem;
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
            time.setText(currentItem.getPubDate());
            if (!currentItem.getImage().isEmpty()) {
                Picasso.get().load(currentItem.getImage())
                        .placeholder(R.drawable.default_image)
                        .error(R.drawable.default_image)
                        .into(image);
            } else {
                image.setImageResource(R.drawable.default_image);
            }

            ((RSSItemViewHolder) holder).onItemClickListener = position1 -> Toast.makeText(mContext, "onClick RSS Item: " + currentItem.getLink(), Toast.LENGTH_SHORT).show();

            // TODO: Implement "Show more / less" text at the end of description text.

//        String textDescription = description.getText().toString();
//        if (textDescription.length() > 20) {
//            textDescription = textDescription.substring(0, 20) + "...";
//            description.setText(Html.fromHtml(textDescription + "<font color='#3498db'> Show more</font>"));
//        }

//            description.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onClickExpandableText(description);
//                }
//            });

//            SpannableString ss = new SpannableString("Android is a Software stack");
//            ClickableSpan clickableSpan = new ClickableSpan() {
//                @Override
//                public void onClick(View textView) {
//                    Toast.makeText(mContext, "Show more/less", Toast.LENGTH_SHORT).show();
//                }
//                @Override
//                public void updateDrawState(TextPaint ds) {
//                    super.updateDrawState(ds);
//                    ds.setUnderlineText(false);
//                }
//            };
//            ss.setSpan(clickableSpan, 22, 27, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//            description.setText(ss);
//            description.setMovementMethod(LinkMovementMethod.getInstance());
//            description.setHighlightColor(Color.TRANSPARENT);
        }
    }

    private void onClickExpandableText(MaterialTextView textView) {
        int collapsedMaxLines = 3;
        if (textView.getMaxLines() == collapsedMaxLines) {
            ObjectAnimator animation = ObjectAnimator.ofInt(textView, "maxLines", textView.getLineCount());
            animation.setDuration(200).start();
        } else {
            ObjectAnimator animation = ObjectAnimator.ofInt(textView, "maxLines", collapsedMaxLines);
            animation.setDuration(200).start();
        }
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
        public MaterialTextView title;
        public MaterialTextView time;
        public MaterialTextView description;
        public ImageView image;
        public OnItemClickListener onItemClickListener;

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
