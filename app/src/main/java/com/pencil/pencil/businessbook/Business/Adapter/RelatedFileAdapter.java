package com.pencil.pencil.businessbook.Business.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.pencil.pencil.businessbook.Business.Pojo.Offer;
import com.pencil.pencil.businessbook.Business.Pojo.RelatedFile;
import com.pencil.pencil.businessbook.R;

import java.util.ArrayList;
import java.util.List;

public class RelatedFileAdapter extends RecyclerView.Adapter<RelatedFileAdapter.ViewHolder> {

    private List<RelatedFile> mData;

    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context mContext;
    private Boolean size;

    // data is passed into the constructor
    public RelatedFileAdapter(Context context, List<RelatedFile> data, Boolean fullsize) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        mContext = context;
        size = fullsize;
    }

    public void add(RelatedFile relatedFile) {
        mData.add(relatedFile);
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<RelatedFile> relatedFile) {
        mData.addAll(relatedFile);
        notifyDataSetChanged();
    }

    public void clearAll() {
        mData = new ArrayList<>();
        notifyDataSetChanged();
    }
    public int getDataLength() {
        return mData.size();
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.related_file_item, parent, false);
        Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();

        int width = display.getWidth();
        int height = display.getHeight();
        ImageView iv = view.findViewById(R.id.relatedFileIv);
        if (size) {

            iv.getLayoutParams().width = width / 2;
            iv.getLayoutParams().height = width / 2;
        } else {
            iv.getLayoutParams().width = width / 4;
            iv.getLayoutParams().height = width / 4;
        }

        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RelatedFile item = mData.get(position);
        if (item.getmRelatedFileBitmap() != null) {
            holder.relatedFileIv.setImageBitmap(item.getmRelatedFileBitmap());
        } else
            Glide.with(mContext).load(item.getmRelatedFileUrl()).into(holder.relatedFileIv);
         }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView relatedFileIv;
        TextView titleTv;

        ViewHolder(View itemView) {
            super(itemView);
            relatedFileIv = itemView.findViewById(R.id.relatedFileIv);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }


    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}