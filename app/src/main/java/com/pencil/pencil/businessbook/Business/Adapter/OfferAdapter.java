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
import com.pencil.pencil.businessbook.R;

import java.util.ArrayList;
import java.util.List;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.ViewHolder> {

    private List<Offer> mData;

    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context mContext;

    // data is passed into the constructor
    public OfferAdapter(Context context, List<Offer> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        mContext=context;
    }
    public void add(ArrayList<Offer> offer){
        mData.addAll(offer);
    notifyDataSetChanged();
    }
    public void clearAll(){
        mData=new ArrayList<>();
        notifyDataSetChanged();
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.offer_item, parent, false);

        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Offer item = mData.get(position);
        holder.titleTv.setText(item.getOfferText());
        Glide.with(mContext).load(item.getOfferImg()).into( holder.offerIv);
       // Glide.with(mContext).load(item.getOfferImg()).into( holder.offerIv);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView offerIv;
        TextView titleTv;

        ViewHolder(View itemView) {
            super(itemView);
            offerIv = itemView.findViewById(R.id.offerIv);
            titleTv = itemView.findViewById(R.id.offerTv);
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