package com.example.mcommerce.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.mcommerce.R;
import com.example.mcommerce.models.Alert;
import com.example.mcommerce.utils.DateTimeUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlertRecyclerViewAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;

    private List<Alert> mListAlerts;
    private Context context;

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        public void longClickedListener(int pos);
    }


    public AlertRecyclerViewAdapter(Context context, List<Alert> mListTransactions, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.mListAlerts = mListTransactions;
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.alert_item, parent, false));
            case VIEW_TYPE_LOADING:
                return new FooterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == mListAlerts.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return mListAlerts == null ? 0 : mListAlerts.size();
    }

    public void add(Alert response) {
        mListAlerts.add(response);
        notifyItemInserted(mListAlerts.size() - 1);
    }

    public void addAll(List<Alert> postItems) {
        for (Alert response : postItems) {
            add(response);
        }
    }


    private void remove(Alert postItems) {
        int position = mListAlerts.indexOf(postItems);
        if (position > -1) {
            mListAlerts.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void addLoading() {
        isLoaderVisible = true;
        add(new Alert());
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = mListAlerts.size() - 1;
        Alert item = getItem(position);
        if (item != null) {
            mListAlerts.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }


    public Alert getItem(int position) {
        return mListAlerts.get(position);
    }


    public class ViewHolder extends BaseViewHolder {


        View itemView;
        ImageView mImageProductPhoto;
        TextView mTextProductName;
        TextView mTextProductCode;
        TextView mTextProductDescription;
        TextView mTextCreatedAt;

        ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            mImageProductPhoto = itemView.findViewById(R.id.product_photo);
            mTextProductName = itemView.findViewById(R.id.product_name);
            mTextProductCode = itemView.findViewById(R.id.product_code);
            mTextProductDescription = itemView.findViewById(R.id.description);
            mTextCreatedAt = itemView.findViewById(R.id.created_at);
        }


        protected void clear() {
        }

        public void onBind(final int position) {
            super.onBind(position);
            final Alert item = mListAlerts.get(position);

            mTextProductName.setText(item.product_name);
            mTextProductDescription.setText(item.product_description);
            mTextProductCode.setText(item.product_code);
            mTextCreatedAt.setText(DateTimeUtils.convertFormat(DateTimeUtils.FMT_FULL, DateTimeUtils.FMT_STANDARD, item.created_at));

            Picasso.with(context).load(item.product_photo_1).fit().centerCrop().placeholder(R.drawable.ic_no_image).into(mImageProductPhoto);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemClickListener.longClickedListener(position);
                    return false;
                }
            });

        }
    }


    public class FooterHolder extends BaseViewHolder {

        ProgressBar mProgressBar;

        FooterHolder(View itemView) {
            super(itemView);
            mProgressBar = itemView.findViewById(R.id.progressBar);
        }

        @Override
        protected void clear() {

        }

    }


    public void removeItem(int pos) {
        this.mListAlerts.remove(pos);
        notifyDataSetChanged();
    }

}