package com.mostafa_anter.loaders.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mostafa_anter.loaders.R;
import com.mostafa_anter.loaders.R2;
import com.mostafa_anter.loaders.model.FeedItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mostafa_anter on 2/25/17.
 */

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    private List<FeedItem> mDataSet;
    private Context mContext;

    private int mStackLevel = 0;

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public  class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.name) TextView name;
        @BindView(R2.id.timestamp) TextView timestamp;
        @BindView(R2.id.txtStatusMsg)TextView txtStatusMsg;
        @BindView(R2.id.txtUrl) TextView txtUrl;
        @BindView(R2.id.profilePic) ImageView profilePic;
        @BindView(R2.id.feedImage1) ImageView feedImage1;

        public TextView getName() {
            return name;
        }

        public TextView getTimestamp() {
            return timestamp;
        }

        public TextView getTxtStatusMsg() {
            return txtStatusMsg;
        }

        public TextView getTxtUrl() {
            return txtUrl;
        }

        public ImageView getProfilePic() {
            return profilePic;
        }

        public ImageView getFeedImage1() {
            return feedImage1;
        }

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                }
            });

        }


    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public FeedAdapter(Context mContext, List<FeedItem> dataSet) {
        this.mContext = mContext;
        mDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.feed_item, viewGroup, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");
        viewHolder.getName().setText(mDataSet.get(position).getName());

        // Converting timestamp into x ago format
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                Long.parseLong(mDataSet.get(position).getTimeStamp()),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        viewHolder.getTimestamp().setText(timeAgo);

        // Chcek for empty status message
        if (!TextUtils.isEmpty(mDataSet.get(position).getStatus())) {
            viewHolder.getTxtStatusMsg().setText(mDataSet.get(position).getStatus());
            viewHolder.getTxtStatusMsg().setVisibility(View.VISIBLE);
        } else {
            // status is empty, remove from view
            viewHolder.getTxtStatusMsg().setVisibility(View.GONE);
        }

        // Checking for null feed url
        if (mDataSet.get(position).getUrl() != null) {
            viewHolder.getTxtUrl().setText(Html.fromHtml("<a href=\"" + mDataSet.get(position).getUrl() + "\">"
                    + mDataSet.get(position).getUrl() + "</a> "));

            // Making url clickable
            viewHolder.getTxtUrl().setMovementMethod(LinkMovementMethod.getInstance());
            viewHolder.getTxtUrl().setVisibility(View.VISIBLE);
        } else {
            // url is null, remove from the view
            viewHolder.getTxtUrl().setVisibility(View.GONE);
        }

        // user profile pic
        Glide.with(mContext)
                .load(mDataSet.get(position).getProfilePic())
                .thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .crossFade()
                .into(viewHolder.getProfilePic());

        // Feed image
        if (mDataSet.get(position).getImge() != null) {
            viewHolder.getFeedImage1().setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(mDataSet.get(position).getImge())
                    .thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .crossFade()
                    .into(viewHolder.getFeedImage1());
        } else {
            viewHolder.getFeedImage1().setVisibility(View.GONE);
        }




    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}