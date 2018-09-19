package com.pencil.pencil.businessbook.Business.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.pencil.pencil.businessbook.Business.Pojo.RelatedFile;
import com.pencil.pencil.businessbook.R;
import com.pencil.pencil.businessbook.Util.FileUtils;
import com.pencil.pencil.businessbook.Util.PrefManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.pencil.pencil.businessbook.Util.FileUtils.ProgressDialog;

public class RelatedEditProFileAdapter extends RecyclerView.Adapter<RelatedEditProFileAdapter.ViewHolder> {

    private List<RelatedFile> mData;

    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context mContext;
    private Boolean size;
    private int mSelectedFilePosition;
    private EditProfileInterface editProfileInterface;

    // data is passed into the constructor
    public RelatedEditProFileAdapter(Context context, List<RelatedFile> data, EditProfileInterface ic) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        mContext = context;
        editProfileInterface=ic;

    }
    public void add(ArrayList<RelatedFile> relatedFiles) {
        mData.addAll(relatedFiles);
        notifyDataSetChanged();
    }
    public void replace(RelatedFile relatedFile) {
        RelatedFile oldRelatedFile= mData.get(mSelectedFilePosition);
        oldRelatedFile.setmRelatedFileUri(relatedFile.getmRelatedFileUri());
        oldRelatedFile.setmRelatedFileBitmap(relatedFile.getmRelatedFileBitmap());
        mData.set(mSelectedFilePosition,oldRelatedFile);
        notifyItemChanged(mSelectedFilePosition);
    }

    public int getDataLength() {
        return mData.size();
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.related_edit_profile, parent, false);
        Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();

        int width = display.getWidth();
        int height = display.getHeight();
        ImageView iv = view.findViewById(R.id.relatedFileIv);
        RelativeLayout relatedLayout = view.findViewById(R.id.relativeLayout);
        iv.getLayoutParams().width = width / 4;
        iv.getLayoutParams().height = width / 4;
        relatedLayout.getLayoutParams().width = width / 4;

        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RelatedFile item = mData.get(position);
        if (item.getmRelatedFileBitmap() != null) {
            holder.relatedFileIv.setImageBitmap(item.getmRelatedFileBitmap());
        } else
         Glide.with(mContext).load(item.getmRelatedFileUrl()).into( holder.relatedFileIv);
        listener(holder);
    }

    private void listener(final ViewHolder holder) {
        holder.removeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteImage(mData.get(holder.getAdapterPosition()).getId(),holder);
            }
        });

        holder.replaceIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectedFilePosition=holder.getAdapterPosition();

                editProfileInterface.replaceImage();
            }
        });
        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri=mData.get(holder.getAdapterPosition()).getmRelatedFileUri();
                if(uri!=null) {
                    File    mImageFile = new File(FileUtils.getRealPathFromURI(mContext,uri));
                    updateImage( mImageFile ,mData.get(holder.getAdapterPosition()).getBusinessId(),mData.get(holder.getAdapterPosition()).getId());
                }
            }});
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }
    private void updateImage(File    mImageFile ,String businessId,int id) {


        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);

        MultipartBody.Part filePart = MultipartBody.Part.createFormData("image", mImageFile.getName(), RequestBody.create(MediaType.parse("image/*"), mImageFile));
        RequestBody businessIdReq = RequestBody.create(MediaType.parse("text/plain"), businessId);

      final    Dialog dialog= FileUtils.ProgressDialog(  mContext);




        Call<GeneralResponseBody<Object>> call = apiInterface.updateRelatedFile(id ,filePart);

        call.enqueue(new Callback<GeneralResponseBody<Object>>() {
            @Override
            public void onResponse(@NonNull Call<GeneralResponseBody<Object>> call, @NonNull Response<GeneralResponseBody<Object>> response) {
                Log.v("XXXX",call.request().body().toString());
                if (dialog != null)
                    dialog.dismiss();
                if (response.isSuccessful()) {

//                    dialog.dismiss();
                    Object body = response.body();


                    Log.v("XXXX", body.toString());

                }else {
                    Gson gson = new Gson();



                }

            }

            @Override
            public void onFailure(@NonNull Call<GeneralResponseBody<Object>> call, @NonNull Throwable t) {

                try {
                    if (dialog != null)
                        dialog.dismiss();
                    Log.e("failed", t.getMessage()+call.request().url());
                    Toast.makeText(mContext, "Connection error", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
    private void deleteImage(int id,final ViewHolder holder) {


        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);


        final    Dialog dialog= FileUtils.ProgressDialog(  mContext);




        Call<GeneralResponseBody<Object>> call = apiInterface.deleteRelatedFile(id );

        call.enqueue(new Callback<GeneralResponseBody<Object>>() {
            @Override
            public void onResponse(@NonNull Call<GeneralResponseBody<Object>> call, @NonNull Response<GeneralResponseBody<Object>> response) {

                if (dialog != null)
                    dialog.dismiss();
                if (response.isSuccessful()) {
                    mData.remove( holder.getAdapterPosition());
                    notifyItemRemoved(holder.getAdapterPosition());
                    notifyItemRangeChanged(holder.getAdapterPosition(), mData.size());
                    GeneralResponseBody<Object> result=response.body();
                    Object body = response.body();
                    String flag=result.flag;
                    if(flag.equals("1")){
                        Toast.makeText(mContext,"deleted sucessfully",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(mContext,"failed to delete",Toast.LENGTH_LONG).show();
                    }

                    Log.v("XXXX", body.toString());

                }else {
                    Gson gson = new Gson();



                }

            }

            @Override
            public void onFailure(@NonNull Call<GeneralResponseBody<Object>> call, @NonNull Throwable t) {

                try {
                    if (dialog != null)
                        dialog.dismiss();
                    Log.e("failed", t.getMessage()+call.request().url());
                    Toast.makeText(mContext, "Connection error", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView relatedFileIv;
        ImageView replaceIv;
        ImageView removeIv;
        Button editBtn;

        ViewHolder(View itemView) {
            super(itemView);
            relatedFileIv = itemView.findViewById(R.id.relatedFileIv);
            replaceIv = itemView.findViewById(R.id.replaceIv);
            removeIv = itemView.findViewById(R.id.removeIv);
            editBtn= itemView.findViewById(R.id.editBtn);
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