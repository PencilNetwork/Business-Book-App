package com.pencil.pencil.businessbook.Business.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.pencil.pencil.businessbook.Api.APIClient;
import com.pencil.pencil.businessbook.Api.APIInterface;
import com.pencil.pencil.businessbook.Business.Activity.BusinessProfileActivity;
import com.pencil.pencil.businessbook.Business.Activity.CreateBusinessActivity;
import com.pencil.pencil.businessbook.Business.Interface.EditProfileInterface;
import com.pencil.pencil.businessbook.Business.Pojo.CreateBusinessInput;
import com.pencil.pencil.businessbook.Business.Pojo.GeneralResponseBody;
import com.pencil.pencil.businessbook.Business.Pojo.Offer;
import com.pencil.pencil.businessbook.Business.Pojo.RelatedFile;
import com.pencil.pencil.businessbook.R;
import com.pencil.pencil.businessbook.Util.FileUtils;
import com.pencil.pencil.businessbook.Util.PrefManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditOfferAdapter extends RecyclerView.Adapter<EditOfferAdapter.ViewHolder> {

    private List<Offer> mData;

    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context mContext;
    private Boolean size;
    private int mSelectedFilePosition;
    private EditProfileInterface editProfileInterface;

    // data is passed into the constructor
    public EditOfferAdapter(Context context, List<Offer> data, EditProfileInterface ic) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        mContext = context;
        editProfileInterface = ic;

    }

    public void replace(Bitmap newBitmap, Uri uri) {
        Offer offerNew = mData.get(mSelectedFilePosition);
        offerNew.setOfferBitmap(newBitmap);
        offerNew.setmUri(uri);
        mData.set(mSelectedFilePosition, offerNew);
        notifyItemChanged(mSelectedFilePosition);
    }

    public void add(ArrayList<Offer> offers) {
        mData.addAll(offers);
        notifyDataSetChanged();
    }

    public int getDataLength() {
        return mData.size();
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.edit_offer_item, parent, false);
        Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();

        int width = display.getWidth();

        ImageView iv = view.findViewById(R.id.offerIv);
        EditText offerET = view.findViewById(R.id.offerET);

        iv.getLayoutParams().width = width / 4;
        iv.getLayoutParams().height = width / 4;
        offerET.getLayoutParams().width = width / 4;

        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Offer item = mData.get(position);
        if (item.getOfferBitmap() != null) {
            holder.offerIv.setImageBitmap(item.getOfferBitmap());
        } else
            Glide.with(mContext).load(item.getOfferImg()).into(holder.offerIv);
        holder.offerET.setText(item.getOfferText());
        listener(holder);
    }

    private void listener(final ViewHolder holder) {
        holder.offerET.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                Offer offer = mData.get(holder.getAdapterPosition());
                offer.setOfferText(s.toString().trim());
                mData.set(holder.getAdapterPosition(), offer);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });
        holder.removeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteImage(mData.get(holder.getAdapterPosition()).getId(), holder);
            }
        });

        holder.replaceIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectedFilePosition = holder.getAdapterPosition();

                editProfileInterface.replaceImage();

            }
        });
        holder.confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = mData.get(holder.getAdapterPosition()).getmUri();
                File mImageFile = null;
                if (uri != null) {
                    mImageFile = new File(FileUtils.getRealPathFromURI(mContext, uri));
                }
                if (mData.get(holder.getAdapterPosition()).getOfferText().length() > 0) {
                    updateImage(mImageFile, mData.get(holder.getAdapterPosition()).getOfferText(), mData.get(holder.getAdapterPosition()).getId());
                } else
                    Toast.makeText(mContext, mContext.getResources().getText(R.string.enter_caption), Toast.LENGTH_LONG).show();

            }
        });
    }

    private void updateImage(File mImageFile, String caption, int id) {


        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        MultipartBody.Part filePart = null;
        if (mImageFile != null)
            filePart = MultipartBody.Part.createFormData("image", mImageFile.getName(), RequestBody.create(MediaType.parse("image/*"), mImageFile));
        RequestBody captionReq = RequestBody.create(MediaType.parse("text/plain"), caption);

        final Dialog dialog = FileUtils.ProgressDialog(mContext);
        dialog.show();

        Call<GeneralResponseBody<Object>> call = apiInterface.updateOffer(id, captionReq, filePart);

        call.enqueue(new Callback<GeneralResponseBody<Object>>() {
            @Override
            public void onResponse(@NonNull Call<GeneralResponseBody<Object>> call, @NonNull Response<GeneralResponseBody<Object>> response) {
                Log.v("update", call.request().url().toString());
                if (dialog != null)
                    dialog.dismiss();
                if (response.isSuccessful()) {

                    GeneralResponseBody<Object> body = response.body();
                    String flag = body.flag;
                    if (flag.equals("1")) {
                        Toast.makeText(mContext, mContext.getResources().getText(R.string.sucess_update), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mContext, mContext.getResources().getText(R.string.failed_update), Toast.LENGTH_LONG).show();
                    }

                    Log.v("XXXX", body.toString());

                } else {
                    Toast.makeText(mContext, mContext.getResources().getText(R.string.failed_update), Toast.LENGTH_LONG).show();


                }

            }

            @Override
            public void onFailure(@NonNull Call<GeneralResponseBody<Object>> call, @NonNull Throwable t) {

                try {
                    if (dialog != null)
                        dialog.dismiss();
                    Log.e("failed", t.getMessage() + call.request().url());
                    Toast.makeText(mContext, "Connection error", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void deleteImage(int id, final ViewHolder holder) {


        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);


        final Dialog dialog = FileUtils.ProgressDialog(mContext);
        dialog.show();

        Call<GeneralResponseBody<Object>> call = apiInterface.deleteOffer(id);

        call.enqueue(new Callback<GeneralResponseBody<Object>>() {
            @Override
            public void onResponse(@NonNull Call<GeneralResponseBody<Object>> call, @NonNull Response<GeneralResponseBody<Object>> response) {

                if (dialog != null)
                    dialog.dismiss();
                if (response.isSuccessful()) {
                    mData.remove(holder.getAdapterPosition());
                    notifyItemRemoved(holder.getAdapterPosition());
                    notifyItemRangeChanged(holder.getAdapterPosition(), mData.size());
                    GeneralResponseBody<Object> result = response.body();
                    Object body = response.body();
                    String flag = result.flag;
                    if (flag.equals("1")) {
                        Toast.makeText(mContext, "deleted sucessfully", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mContext, "failed to delete", Toast.LENGTH_LONG).show();
                    }

                    Log.v("XXXX", body.toString());

                } else {
                    Gson gson = new Gson();


                }

            }

            @Override
            public void onFailure(@NonNull Call<GeneralResponseBody<Object>> call, @NonNull Throwable t) {

                try {
                    if (dialog != null)
                        dialog.dismiss();
                    Log.e("failed", t.getMessage() + call.request().url());
                    Toast.makeText(mContext, "Connection error", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView offerIv;
        ImageView replaceIv;
        ImageView removeIv;
        EditText offerET;
        Button confirmBtn;


        ViewHolder(View itemView) {
            super(itemView);
            offerIv = itemView.findViewById(R.id.offerIv);
            replaceIv = itemView.findViewById(R.id.replaceIv);
            removeIv = itemView.findViewById(R.id.removeIv);
            offerET = itemView.findViewById(R.id.offerET);
            confirmBtn = itemView.findViewById(R.id.editBtn);
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