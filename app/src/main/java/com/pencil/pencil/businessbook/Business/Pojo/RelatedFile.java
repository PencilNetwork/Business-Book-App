package com.pencil.pencil.businessbook.Business.Pojo;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.gson.annotations.SerializedName;

public class RelatedFile {

    private Bitmap mRelatedFileBitmap;
    private Uri   mRelatedFileUri;
    @SerializedName("id")
    private int id;
    @SerializedName("bussines_id")
    private String businessId;
    @SerializedName("image")
    private String mRelatedFileUrl;
    @SerializedName("created_at")
    private String created_at;
    @SerializedName("updated_at")
    private String updated_at;

    public String getmRelatedFileUrl() {
        return mRelatedFileUrl;
    }

    public Uri getmRelatedFileUri() {
        return mRelatedFileUri;
    }

    public RelatedFile(String relatedFileUrl){
        mRelatedFileUrl=relatedFileUrl;
    }
    public RelatedFile(Bitmap relatedFileUrl){
        mRelatedFileBitmap=relatedFileUrl;
    }

    public Bitmap getmRelatedFileBitmap() {
        return mRelatedFileBitmap;
    }

    public void setmRelatedFileBitmap(Bitmap mRelatedFileBitmap) {
        this.mRelatedFileBitmap = mRelatedFileBitmap;
    }

    public void setmRelatedFileUri(Uri mRelatedFileUri) {
        this.mRelatedFileUri = mRelatedFileUri;
    }

    public int getId() {
        return id;
    }

    public String getBusinessId() {
        return businessId;
    }
}
